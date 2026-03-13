"""Board representation for the Ricochet Robots solver (ported from Java)."""

from .point import Point

# Direction constants (match Java Board)
RIGHT = 0
UP = 1
LEFT = 2
DOWN = 3
DIRECTIONS = (RIGHT, UP, LEFT, DOWN)
PERP = ((UP, DOWN), (LEFT, RIGHT), (UP, DOWN), (LEFT, RIGHT))
OPPOSITE = (LEFT, DOWN, RIGHT, UP)

BIT_RIGHT = 0b00000001
BIT_UP = 0b00000010
BIT_LEFT = 0b00000100
BIT_DOWN = 0b00001000
BITS_DIR = (BIT_RIGHT, BIT_UP, BIT_LEFT, BIT_DOWN)
OFFSETS_DIR = ((1, 0), (0, -1), (-1, 0), (0, 1))


class Board:
    """
    Board with connectivity bits per cell and target positions.
    board[x][y] = byte with bits set for directions that have no wall.
    """

    def __init__(
        self,
        width: int,
        height: int,
        board: list[list[int]] | None = None,
        targets: set[Point] | None = None,
    ) -> None:
        self.width = width
        self.height = height
        if board is not None:
            self._board = board
        else:
            self._board = [[0] * height for _ in range(width)]
        self.targets = set(targets) if targets is not None else set()

    @classmethod
    def from_stream(cls, stream) -> Board:
        """Load board from a text stream (same format as Java)."""
        line = stream.readline().strip()
        parts = line.split(None, 1)
        width = int(parts[0], 10)
        height = int(parts[1], 10)
        # input[col][row] for grid (2*width-1) x (2*height-1)
        input_grid = [[None] * (2 * height - 1) for _ in range(2 * width - 1)]
        for y in range(2 * height - 1):
            line = stream.readline()
            if not line:
                break
            for x in range(2 * width - 1):
                if x < len(line):
                    input_grid[x][y] = line[x]
                else:
                    input_grid[x][y] = " "

        board = [[0] * height for _ in range(width)]
        targets: set[Point] = set()
        for x in range(width):
            for y in range(height):
                tile = 0
                if x > 0 and input_grid[2 * x - 1][2 * y] != "|":
                    tile |= BIT_LEFT
                if x < width - 1 and input_grid[2 * x + 1][2 * y] != "|":
                    tile |= BIT_RIGHT
                if y > 0 and input_grid[2 * x][2 * y - 1] != "-":
                    tile |= BIT_UP
                if y < height - 1 and input_grid[2 * x][2 * y + 1] != "-":
                    tile |= BIT_DOWN
                board[x][y] = tile
                if input_grid[2 * x][2 * y] == "X":
                    targets.add(Point(x, y))
        return cls(width, height, board=board, targets=targets)

    def is_connected(self, p: Point, dir: int) -> bool:
        """True if there is no wall in direction dir from p."""
        return (self._board[p.x][p.y] & BITS_DIR[dir]) == BITS_DIR[dir]

    def is_connected_with_blocked(
        self, p: Point, dir: int, blocked: tuple[Point, ...]
    ) -> bool:
        """True if we can step from p in dir (no wall, first step not blocked)."""
        if not self.is_connected(p, dir):
            return False
        dx, dy = OFFSETS_DIR[dir]
        result = Point(p.x + dx, p.y + dy)
        for block in blocked:
            if result == block:
                return False
        return True

    def dest(self, p: Point, dir: int) -> Point:
        """Slide from p in direction dir until hitting a wall. Return last cell."""
        if not self.is_connected(p, dir):
            return Point(p.x, p.y)
        dx, dy = OFFSETS_DIR[dir]
        rx, ry = p.x, p.y
        while True:
            rx += dx
            ry += dy
            if not (0 <= rx < self.width and 0 <= ry < self.height):
                rx -= dx
                ry -= dy
                return Point(rx, ry)
            if (self._board[rx][ry] & BITS_DIR[dir]) != BITS_DIR[dir]:
                return Point(rx, ry)

    def dest_with_blocked(
        self, p: Point, dir: int, blocked: tuple[Point, ...]
    ) -> Point:
        """Slide from p in dir until wall or blocked point; return last cell."""
        if not self.is_connected(p, dir):
            return Point(p.x, p.y)
        dx, dy = OFFSETS_DIR[dir]
        rx, ry = p.x, p.y
        while True:
            rx += dx
            ry += dy
            for block in blocked:
                if rx == block.x and ry == block.y:
                    return Point(rx - dx, ry - dy)
            if not (0 <= rx < self.width and 0 <= ry < self.height):
                return Point(rx - dx, ry - dy)
            if (self._board[rx][ry] & BITS_DIR[dir]) != BITS_DIR[dir]:
                return Point(rx, ry)

    def reachable(self, p: Point) -> set[Point]:
        """Set of positions reachable from p in one move (no other robots)."""
        result: set[Point] = set()
        for d in DIRECTIONS:
            result.add(self.dest(p, d))
        return result

    def reachable_with_blocked(
        self, p: Point, blocked: tuple[Point, ...]
    ) -> set[Point]:
        """Set of positions reachable from p in one move with other robots at blocked."""
        result: set[Point] = set()
        for d in DIRECTIONS:
            result.add(self.dest_with_blocked(p, d, blocked))
        return result
