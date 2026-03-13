"""Solver for Ricochet Robots (ported from Java)."""

import time
from collections import deque

from .board import DIRECTIONS, OFFSETS_DIR, OPPOSITE, PERP, Board
from .point import Point

# Type alias: configuration is a tuple of positions (one per robot)
Configuration = tuple[Point, ...]


class _Node:
    """A search node: configuration, move count, and previous node."""

    __slots__ = ("configuration", "moves", "previous")

    def __init__(
        self,
        configuration: Configuration,
        moves: int,
        previous: _Node | None,
    ) -> None:
        self.configuration = configuration
        self.moves = moves
        self.previous = previous


class _MoveNode:
    """A node in the 'end moves' chain: position, moves to target, next toward target."""

    __slots__ = ("point", "moves", "next")

    def __init__(
        self,
        point: Point,
        moves: int,
        next_node: _MoveNode | None,
    ) -> None:
        self.point = point
        self.moves = moves
        self.next = next_node


class Solver:
    """
    Finds a shortest sequence of moves to bring the target robot to the target cell.
    """

    DEFAULT_MAX_MOVES = 20
    DEFAULT_MAX_TIME = 2 * 60.0

    def __init__(
        self,
        board: Board,
        robots_positions: tuple[Point, ...],
        target: Point,
        target_robot_index: int,
        max_moves: int = DEFAULT_MAX_MOVES,
        max_time: float = DEFAULT_MAX_TIME,
    ) -> None:
        """Solve for target robot to reach target; stores solution and move count."""
        self.board = board
        self.target = target
        self.target_robot_index = target_robot_index
        self.number_robots = len(robots_positions)
        self._solution: list[Configuration] | None = None
        self._moves = -1
        self._solution = self._solve_brute_force(robots_positions, max_moves, max_time)
        self._moves = len(self._solution) - 1 if self._solution else -1

    def moves(self) -> int:
        """Number of moves in the solution, or -1 if no solution found."""
        return self._moves

    def solution(self) -> list[Configuration] | None:
        """List of configurations from initial to final (each step after a move)."""
        return self._solution

    def _solve_brute_force(
        self,
        initial: tuple[Point, ...],
        max_moves: int,
        max_time: float,
    ) -> list[Configuration] | None:
        start = time.perf_counter()
        end_time = start + max_time
        queue: deque[_Node] = deque()
        queue.append(_Node(initial, 0, None))
        current: _Node | None = None
        solved = False

        while queue:
            current = queue.popleft()
            if current.configuration[self.target_robot_index] == self.target:
                solved = True
                break

            config_without_target = tuple(
                current.configuration[i]
                for i in range(self.number_robots)
                if i != self.target_robot_index
            )
            end_moves_map = self._end_moves(config_without_target)
            move_node = None
            best_moves = -1
            target_pos = current.configuration[self.target_robot_index]
            blocked = current.configuration
            for dest in self.board.reachable_with_blocked(target_pos, blocked):
                temp = end_moves_map.get(dest)
                if temp is not None and (best_moves < 0 or temp.moves < best_moves):
                    move_node = temp
                    best_moves = temp.moves

            if move_node is not None:
                while move_node is not None:
                    new_config = list(current.configuration)
                    new_config[self.target_robot_index] = move_node.point
                    current = _Node(
                        tuple(new_config),
                        current.moves + 1,
                        current,
                    )
                    queue.append(current)
                    move_node = move_node.next
                solved = True
                break

            if time.perf_counter() >= end_time:
                break

            if current.moves >= max_moves:
                continue

            for i in range(self.number_robots):
                position = current.configuration[i]
                for dest in self.board.reachable_with_blocked(
                    position, current.configuration
                ):
                    if dest != position:
                        new_config = list(current.configuration)
                        new_config[i] = dest
                        new_config_t = tuple(new_config)
                        prev = current.previous
                        if prev is None or prev.configuration != new_config_t:
                            queue.append(
                                _Node(new_config_t, current.moves + 1, current)
                            )

        if not solved or current is None:
            return None
        assert current.configuration[self.target_robot_index] == self.target

        result: list[Configuration] = []
        while current is not None:
            result.append(current.configuration)
            current = current.previous
        result.reverse()
        return result

    def _end_moves(self, configuration: tuple[Point, ...]) -> dict[Point, _MoveNode]:
        """Positions from which target robot can reach target (others fixed)."""
        for p in configuration:
            if p == self.target:
                return {}
        node = _MoveNode(self.target, 0, None)
        end_moves: dict[Point, _MoveNode] = {self.target: node}
        for dir in DIRECTIONS:
            if self.board.is_connected_with_blocked(self.target, dir, configuration):
                dx, dy = OFFSETS_DIR[dir]
                one_step = self.target.move(dx, dy)
                self._add_to_map(end_moves, configuration, one_step, 1, dir, node)
        return end_moves

    def _add_to_map(
        self,
        map: dict[Point, _MoveNode],
        configuration: tuple[Point, ...],
        point: Point,
        moves: int,
        direction: int,
        next_node: _MoveNode,
    ) -> None:
        existing = map.get(point)
        if existing is not None and existing.moves <= moves:
            return
        node = _MoveNode(point, moves, next_node)
        map[point] = node
        if self.board.is_connected_with_blocked(point, direction, configuration):
            dx, dy = OFFSETS_DIR[direction]
            self._add_to_map(
                map,
                configuration,
                point.move(dx, dy),
                moves,
                direction,
                next_node,
            )
        for perp in PERP[direction]:
            if not self.board.is_connected_with_blocked(
                point, perp, configuration
            ) and self.board.is_connected_with_blocked(
                point, OPPOSITE[perp], configuration
            ):
                opp = OPPOSITE[perp]
                dx, dy = OFFSETS_DIR[opp]
                self._add_to_map(
                    map,
                    configuration,
                    point.move(dx, dy),
                    moves + 1,
                    opp,
                    node,
                )
