# -*- coding: utf-8 -*-

"""Board classes."""

from enum import Enum
from typing import Any, Iterable, Optional, Tuple


class Direction(Enum):
    """A direction on the board."""

    RIGHT = ("LEFT", ("UP", "DOWN"), (+1, 0))
    UP = ("DOWN", ("LEFT", "RIGHT"), (0, -1))
    LEFT = ("RIGHT", ("UP", "DOWN"), (-1, 0))
    DOWN = ("UP", ("LEFT", "RIGHT"), (0, +1))

    def __init__(
        self: "Direction", opposite: str, perp: Tuple[str, str], offset: Tuple[int, int]
    ) -> None:
        self._opposite = opposite
        self._perp = perp
        self.offset = offset

    @property
    def opposite(self: "Direction") -> "Direction":
        """Opposite direction."""
        return Direction[self._opposite]

    @property
    def perp(self: "Direction") -> Tuple["Direction", "Direction"]:
        """Perpendicular direction."""
        return (Direction[self._perp[0]], Direction[self._perp[1]])


BARS = (
    (" ", "\\", "|", "/"),
    ("\\", " ", "/", "-"),
    ("|", "/", " ", "\\"),
    ("/", "-", "\\", " "),
)
CONNECTED_CHAR = " "
EMPTY_FIELD_CHAR = "\u2591"
TARGET_CHAR = "\u2593"

BIT_RIGHT = 0b00000001
BIT_UP = 0b00000010
BIT_LEFT = 0b00000100
BIT_DOWN = 0b00001000
BITS_DIR = (BIT_RIGHT, BIT_UP, BIT_LEFT, BIT_DOWN)


class Point:
    """A point on the board."""

    def __init__(self: "Point", row: int, col: int) -> None:
        self.row = row
        self.col = col

    def __eq__(self: "Point", other: Any) -> bool:
        return (
            isinstance(other, Point) and self.row == other.row and self.col == other.col
        )

    def __hash__(self: "Point") -> int:
        return hash(self.row) ^ hash(self.col)

    def __str__(self: "Point") -> str:
        return f"{type(self).__name__}({self.row}, {self.col})"

    def __repr__(self: "Point") -> str:
        return f"{type(self)}({self.row}, {self.col})"

    def move(self: "Point", direction: Direction) -> "Point":
        """Move this point in the given direction."""
        return Point(self.row + direction.offset[0], self.col + direction.offset[1])


class Tile(Point):
    """A tile on the board."""

    def __init__(
        self: "Tile",
        row: int,
        col: int,
        accessible: bool = True,
        connected: Optional[Iterable[Direction]] = None,
    ) -> None:
        super().__init__(row, col)
        connected = () if not accessible or connected is None else connected
        self.accessible = accessible
        self.connected = frozenset(connected)

    def move(self: "Tile", direction: Direction) -> Point:
        if not self.accessible or direction not in self.connected:
            return self
        return super().move(direction)
