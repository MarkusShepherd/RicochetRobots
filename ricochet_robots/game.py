# -*- coding: utf-8 -*-

"""Board classes."""

from collections import defaultdict
from enum import Enum
from typing import Dict, FrozenSet, Iterable, List, Optional, Tuple, TYPE_CHECKING

if TYPE_CHECKING:
    import pygame

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


class Robot:
    """A robot."""

    def __init__(
        self: "Robot",
        name: str,
        color: Optional["pygame.Color"] = None,
        tile: Optional[Tile] = None,
    ) -> None:
        self.name = name
        self.color = color
        self.tile = tile


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


class Tile:
    """A tile on the board."""

    row: int
    col: int
    accessible: bool = True
    connected: FrozenSet[Direction] = frozenset()
    target: Optional[Robot] = None

    def __init__(
        self: "Tile",
        row: int,
        col: int,
        accessible: bool = True,
        connected: Optional[Iterable[Direction]] = None,
        target: Optional[Robot] = None,
    ) -> None:
        self.row = row
        self.col = col
        self.accessible = accessible
        connected = () if not accessible or connected is None else connected
        self.connected = frozenset(connected)
        self.target = target

    def __str__(self: "Tile") -> str:
        return f"{type(self).__name__}({self.row}, {self.col})"

    def __repr__(self: "Tile") -> str:
        return f"{type(self)}({self.row}, {self.col})"


class Board:
    """The game board."""

    num_rows: int
    num_cols: int
    robots: FrozenSet[Robot]
    tiles: Tuple[Tuple[Tile, ...], ...]
    targets: Dict[Robot, Tuple[Tile, ...]]

    def __init__(
        self, robots: Iterable[Robot], tiles: Iterable[Iterable[Tile]]
    ) -> None:
        self.robots = frozenset(robots)
        self.tiles = tuple(tuple(row) for row in tiles)
        self.num_rows = len(self.tiles)
        self.num_cols = len(self.tiles[0])

        targets: Dict[Robot, List[Tile]] = defaultdict(list)

        for row_n, row in enumerate(self.tiles):
            assert len(row) == self.num_cols
            for col_n, tile in enumerate(row):
                assert isinstance(tile, Tile)
                assert tile.row == row_n and tile.col == col_n
                if tile.target is not None:
                    assert tile.target in self.robots
                    targets[tile.target].append(tile)

        self.targets = {
            robot: tuple(target_list) for robot, target_list in targets.items()
        }

    def blocked(self: "Board", tile: Tile) -> bool:
        if not tile.accessible:
            return True
        return any(robot.tile == tile for robot in self.robots)

    def connected(self: "Board", tile: Tile, direction: Direction) -> bool:
        if not tile.accessible or direction not in tile.connected:
            return False
        dest_row, dest_col = (tile.row, tile.col) + direction.offset
        if not 0 <= dest_row < self.num_rows or not 0 <= dest_col < self.num_cols:
            return False
        dest_tile = self.tiles[dest_row][dest_col]
        return not dest_tile.accessible and not self.blocked(dest_tile)
