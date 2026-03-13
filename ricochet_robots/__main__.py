"""Run the solver from the command line."""

import argparse
import random
import sys
import time

from .board import Board
from .point import Point
from .solver import Solver
from .view import BoardView

ROBOT_LABELS = ("Red", "Yellow", "Green", "Blue")
ROBOT_LETTERS = ("R", "Y", "G", "B")


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Solve a Ricochet Robots puzzle: move target robot to target."
    )
    parser.add_argument(
        "-b",
        "--board",
        required=True,
        metavar="FILE",
        help="Board file (first line: width height; then grid with | - and X)",
    )
    parser.add_argument(
        "-m",
        "--max-moves",
        type=int,
        default=Solver.DEFAULT_MAX_MOVES,
        metavar="N",
        help="Max moves to search (default: %(default)s)",
    )
    parser.add_argument(
        "-t",
        "--max-time",
        type=float,
        default=Solver.DEFAULT_MAX_TIME,
        metavar="SEC",
        help="Max time in seconds (default: %(default)s)",
    )
    parser.add_argument(
        "-g",
        "--graphical",
        action="store_true",
        help="Show graphical animation of the solution",
    )
    parser.add_argument(
        "-w",
        "--width",
        type=int,
        default=30,
        metavar="PX",
        help="Cell width in pixels for graphical view (default: 30)",
    )
    parser.add_argument(
        "--height",
        type=int,
        default=None,
        metavar="PX",
        help="Cell height in pixels for graphical view (default: same as width)",
    )
    parser.add_argument(
        "-r",
        "--repaint",
        type=int,
        default=1000,
        metavar="MS",
        help="Delay in ms between frames in graphical mode (default: 1000)",
    )
    parser.add_argument(
        "-l",
        "--loop",
        action="store_true",
        help="In graphical mode, loop the solution animation until window is closed",
    )
    args = parser.parse_args()

    try:
        with open(args.board) as f:
            board = Board.from_stream(f)
    except FileNotFoundError:
        print(f"Error: board file not found: {args.board}", file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"Error loading board: {e}", file=sys.stderr)
        sys.exit(1)

    if not board.targets:
        print("Error: board has no target cells (use X).", file=sys.stderr)
        sys.exit(1)

    # Random robot positions (four robots, distinct cells)
    rng = random.Random()
    positions: list[Point] = []
    while len(positions) < 4:
        p = Point(
            rng.randint(0, board.width - 1),
            rng.randint(0, board.height - 1),
        )
        if p not in positions:
            positions.append(p)
    robots_positions = tuple(positions)

    target = rng.choice(list(board.targets))
    target_robot_index = 0
    target_label = ROBOT_LABELS[target_robot_index]

    def robots_map(config: tuple[Point, ...]) -> dict[Point, str]:
        return {config[i]: ROBOT_LETTERS[i] for i in range(4)}

    print(f"Searching solution for robot {target_label} on board:")
    print(board.format_text(robots_map(robots_positions), {target}))
    print(
        f"Maximal number of moves: {args.max_moves}; "
        f"maximal execution time: {args.max_time} s."
    )
    print()

    start = time.perf_counter()
    solver = Solver(
        board,
        robots_positions,
        target,
        target_robot_index,
        max_moves=args.max_moves,
        max_time=args.max_time,
    )
    elapsed = time.perf_counter() - start

    solution = solver.solution()
    if solution is None:
        print(
            f"No solution found with {args.max_moves} moves or aborted after "
            f"{elapsed:.3f} seconds."
        )
        sys.exit(1)

    print(f"Found solution in {elapsed:.3f} seconds with {solver.moves()} moves.")
    print()

    if args.graphical:
        view = BoardView(
            board,
            cell_width=args.width,
            cell_height=args.height,
        )
        view.run_animation(
            solution,
            target,
            delay_ms=args.repaint,
            title=f"Move {target_label} to target with {solver.moves()} moves.",
            loop=args.loop,
        )
        return

    for i, config in enumerate(solution):
        if i == 0:
            continue
        print(board.format_text(robots_map(config), {target}))
        print()


if __name__ == "__main__":
    main()
