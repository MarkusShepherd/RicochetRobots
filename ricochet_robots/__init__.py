"""Ricochet Robots solver and game types."""

from importlib.metadata import PackageNotFoundError, version

from ricochet_robots.board import DIRECTIONS, OFFSETS_DIR, Board
from ricochet_robots.point import Point
from ricochet_robots.solver import Solver

try:
    __version__ = version("ricochet-robots")
except PackageNotFoundError:
    __version__ = "0.0.0"

__all__ = [
    "Board",
    "DIRECTIONS",
    "OFFSETS_DIR",
    "Point",
    "Solver",
    "__version__",
]
