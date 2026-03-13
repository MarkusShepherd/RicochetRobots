"""Pygame-based graphical view of the board and solution animation."""

import pygame

from .board import Board, DOWN, RIGHT
from .point import Point

# Robot colors (match Java: Red, Yellow, Green, Blue)
ROBOT_COLORS = (
    (255, 0, 0),    # Red
    (255, 255, 0),  # Yellow
    (0, 255, 0),    # Green
    (0, 0, 255),    # Blue
)

WALL_THICKNESS = 3


class BoardView:
    """Draws a Board with optional robot positions and target highlight."""

    def __init__(
        self,
        board: Board,
        cell_width: int = 30,
        cell_height: int | None = None,
    ) -> None:
        self.board = board
        self.cell_width = cell_width
        self.cell_height = cell_height if cell_height is not None else cell_width
        self.total_width = board.width * self.cell_width
        self.total_height = board.height * self.cell_height

    def _cell_rect(self, p: Point) -> pygame.Rect:
        return pygame.Rect(
            p.x * self.cell_width,
            p.y * self.cell_height,
            self.cell_width,
            self.cell_height,
        )

    def draw(
        self,
        surface: pygame.Surface,
        robots: tuple[Point, ...] | None = None,
        targets: set[Point] | None = None,
    ) -> None:
        """Draw board, targets, and optionally robots onto surface."""
        surface.fill((128, 128, 128))
        cw, ch = self.cell_width, self.cell_height
        # Grid lines
        for x in range(1, self.board.width):
            pygame.draw.line(
                surface, (0, 0, 0),
                (x * cw, 0), (x * cw, self.total_height), 1
            )
        for y in range(1, self.board.height):
            pygame.draw.line(
                surface, (0, 0, 0),
                (0, y * ch), (self.total_width, y * ch), 1
            )
        # Walls (thick black)
        for x in range(self.board.width):
            for y in range(self.board.height):
                p = Point(x, y)
                if x < self.board.width - 1 and not self.board.is_connected(p, RIGHT):
                    pygame.draw.rect(
                        surface, (0, 0, 0),
                        ((x + 1) * cw - WALL_THICKNESS // 2, y * ch,
                         WALL_THICKNESS, ch)
                    )
                if y < self.board.height - 1 and not self.board.is_connected(p, DOWN):
                    pygame.draw.rect(
                        surface, (0, 0, 0),
                        (x * cw, (y + 1) * ch - WALL_THICKNESS // 2,
                         cw, WALL_THICKNESS)
                    )
        # Targets (white squares, inset)
        t_set = targets if targets is not None else self.board.targets
        for p in t_set:
            r = self._cell_rect(p)
            inset = 4
            pygame.draw.rect(
                surface, (255, 255, 255),
                (r.x + inset, r.y + inset, r.w - 2 * inset, r.h - 2 * inset)
            )
        # Robots (colored circles, inset)
        if robots is not None:
            for i, p in enumerate(robots):
                color = ROBOT_COLORS[i % len(ROBOT_COLORS)]
                r = self._cell_rect(p)
                inset = 4
                center = (r.x + r.w // 2, r.y + r.h // 2)
                radius = min(r.w, r.h) // 2 - inset
                pygame.draw.circle(surface, color, center, radius)

    def _wait_ms(self, screen: pygame.Surface, delay_ms: int, clock: pygame.time.Clock) -> bool:
        """Wait delay_ms, processing events. Return False if QUIT received."""
        wait_remaining = delay_ms
        while wait_remaining > 0:
            for ev in pygame.event.get():
                if ev.type == pygame.QUIT:
                    return False
            wait_remaining -= clock.tick(60)
        return True

    def run_animation(
        self,
        solution: list[tuple[Point, ...]],
        target: Point,
        delay_ms: int = 1000,
        title: str = "Ricochet Robots",
    ) -> None:
        """Open a window and animate through the solution. Blocks until window closed."""
        pygame.init()
        try:
            screen = pygame.display.set_mode(
                (self.total_width, self.total_height),
                pygame.RESIZABLE,
            )
            pygame.display.set_caption(title)
            targets = {target}
            clock = pygame.time.Clock()
            # Board without robots
            self.draw(screen, robots=None, targets=targets)
            pygame.display.flip()
            if delay_ms > 0 and not self._wait_ms(screen, delay_ms, clock):
                return
            # Animate each step
            for config in solution:
                for ev in pygame.event.get():
                    if ev.type == pygame.QUIT:
                        return
                self.draw(screen, robots=config, targets=targets)
                pygame.display.flip()
                if delay_ms > 0 and not self._wait_ms(screen, delay_ms, clock):
                    return
            # Keep window open until quit
            while True:
                for ev in pygame.event.get():
                    if ev.type == pygame.QUIT:
                        return
                clock.tick(60)
        finally:
            pygame.quit()
