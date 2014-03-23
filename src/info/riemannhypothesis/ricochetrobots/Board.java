/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Markus Schepke
 * 
 */
public class Board {

    private final static byte BIT_RIGHT = 0b00000001;
    private final static byte BIT_UP = 0b00000010;
    private final static byte BIT_LEFT = 0b00000100;
    private final static byte BIT_DOWN = 0b00001000;

    public final static int RIGHT = 0;
    public final static int UP = 1;
    public final static int LEFT = 2;
    public final static int DOWN = 3;

    private final static byte[] BITS_DIR = new byte[] { BIT_RIGHT, BIT_UP,
            BIT_LEFT, BIT_DOWN };
    private final static byte[][] OFFSETS_DIR = new byte[][] { { 0, 1 },
            { -1, 0 }, { 0, -1 }, { 1, 0 } };

    private final byte[][] board;
    private final int dim;

    public enum Color {
        RED, BLUE, GREEN, YELLOW;
    }

    /*
     * public enum Direction { RIGHT, UP, LEFT, DOWN; }
     */

    private EnumMap<Color, Point> robots;

    public Board(int dim) {
        this.dim = dim;
        this.board = new byte[dim][dim];
        Set<Point> positions = new HashSet<Point>();
        for (Color color : Color.values()) {
            Point p;
            do {
                p = new Point((int) (Math.random() * dim),
                        (int) (Math.random() * dim));
            } while (positions.contains(p));
            positions.add(p);
            robots.put(color, p);
        }
    }

    public int getDim() {
        return dim;
    }

    public boolean canMove(Point p, int dir) {
        return (board[p.x][p.y] & BITS_DIR[dir]) == BITS_DIR[dir];
    }

    /*
     * public boolean canMoveRight(Point p) { return (board[p.x][p.y] &
     * BIT_RIGHT) == BIT_RIGHT; }
     * 
     * public boolean canMoveUp(Point p) { return (board[p.x][p.y] & BIT_UP) ==
     * BIT_UP; }
     * 
     * public boolean canMoveLeft(Point p) { return (board[p.x][p.y] & BIT_LEFT)
     * == BIT_LEFT; }
     * 
     * public boolean canMoveDown(Point p) { return (board[p.x][p.y] & BIT_DOWN)
     * == BIT_DOWN; }
     */

    public Point dest(Point p, int dir) {
        if (!canMove(p, dir)) {
            return p;
        }
    }

    public Point destRight(Point p) {
        if (!canMoveRight(p)) {
            return p;
        }
        return destRight(p.right());
    }

    public Point destUp(Point p) {
        if (!canMoveUp(p)) {
            return p;
        }
        return destUp(p.up());
    }

    public Point destLeft(Point p) {
        if (!canMoveLeft(p)) {
            return p;
        }
        return destLeft(p.left());
    }

    public Point destDown(Point p) {
        if (!canMoveDown(p)) {
            return p;
        }
        return destDown(p.down());
    }
}
