# Ricochet Robots 🤖

A Python implementation of Alex Randolph's game
[*Ricochet Robots*](https://recommend.games/#/game/51) including a solver.

## Setup

- **Python 3.14** (see [pyenv](https://github.com/pyenv/pyenv) or [uv](https://docs.astral.sh/uv/)).
- Install dependencies and the package with [uv](https://docs.astral.sh/uv/):

```bash
uv sync
```

This creates a virtual environment in `.venv` and installs the project in editable mode.

## Run the solver

Use the board file path, and optionally `--max-moves` and `--max-time`:

```bash
uv run python -m ricochet_robots -b boards/3.txt
```

With limits:

```bash
uv run python -m ricochet_robots -b boards/3.txt -m 25 -t 60
```

The solver picks random starting positions for four robots and a random target cell, then searches for a solution. Output is the number of moves and each step (position of the target robot).

**Board file format:** first line `width height`, then an ASCII grid of `(2×width−1)` columns × `(2×height−1)` rows: use `|` and `-` for walls between cells, space for no wall, and `X` for a target cell. (The `boards/*.txt` files in this repo may use a different format; the Java solver expected the format above.)

## Use the API

```python
from ricochet_robots import Board, Point, Solver

with open("board.txt") as f:
    board = Board.from_stream(f)

positions = (Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1))
target = next(iter(board.targets))
solver = Solver(board, positions, target, target_robot_index=0)

print(solver.moves())   # -1 if no solution
print(solver.solution())  # list of (position tuple per step)
```

## Java (legacy)

To compile and run the original Java solver:

```bash
mkdir -p bin
javac -classpath java/lib/commons-cli-1.3.1.jar \
  -d bin java/src/info/riemannhypothesis/ricochetrobots/*.java
./solve.sh -b boards/3.txt -g
```
