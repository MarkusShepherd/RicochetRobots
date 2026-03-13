# -*- coding: utf-8 -*-

"""Point (cell coordinates) for the solver board."""


class Point:
    """A point (x, y) on the board. x is column, y is row."""

    __slots__ = ("x", "y")

    def __init__(self, x: int, y: int) -> None:
        self.x = x
        self.y = y

    def __eq__(self, other: object) -> bool:
        if self is other:
            return True
        if not isinstance(other, Point):
            return NotImplemented
        return self.x == other.x and self.y == other.y

    def __hash__(self) -> int:
        return self.x ^ self.y

    def __repr__(self) -> str:
        return f"Point({self.x}, {self.y})"

    def move(self, dx: int, dy: int) -> "Point":
        """Return the point one step by (dx, dy)."""
        return Point(self.x + dx, self.y + dy)
