"""Run the solver from the command line."""

import argparse
import random
import sys

from .board import Board
from .point import Point
from .solver import Solver


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Solve a Ricochet Robots puzzle: move the target robot to the target cell."
    )
    parser.add_argument(
        "-b",
        "--board",
        required=True,
        metavar="FILE",
        help="Path to board file (first line: width height; then (2×width−1)×(2×height−1) grid with | - and X for targets)",
    )
    parser.add_argument(
        "-m",
        "--max-moves",
        type=int,
        default=Solver.DEFAULT_MAX_MOVES,
        metavar="N",
        help=f"Maximum number of moves to search (default: {Solver.DEFAULT_MAX_MOVES})",
    )
    parser.add_argument(
        "-t",
        "--max-time",
        type=float,
        default=Solver.DEFAULT_MAX_TIME,
        metavar="SEC",
        help=f"Maximum time in seconds (default: {Solver.DEFAULT_MAX_TIME})",
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
        print("Error: board has no target cells (mark one with X).", file=sys.stderr)
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

    solver = Solver(
        board,
        robots_positions,
        target,
        target_robot_index,
        max_moves=args.max_moves,
        max_time=args.max_time,
    )

    if solver.solution() is None:
        print(
            f"No solution found within {args.max_moves} moves or {args.max_time}s."
        )
        sys.exit(1)

    print(f"Solution: {solver.moves()} moves")
    for i, config in enumerate(solver.solution()):
        print(f"  Step {i}: robot 0 at {config[0]}")


if __name__ == "__main__":
    main()
