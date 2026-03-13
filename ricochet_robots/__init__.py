# -*- coding: utf-8 -*-

"""Initialisations."""

from .__version__ import VERSION, __version__
from .board import Board, DIRECTIONS, OFFSETS_DIR
from .point import Point
from .solver import Solver

__all__ = [
    "Board",
    "DIRECTIONS",
    "OFFSETS_DIR",
    "Point",
    "Solver",
    "VERSION",
    "__version__",
]
