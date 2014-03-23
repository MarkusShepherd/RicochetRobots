/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Markus Schepke
 * 
 */
public class Board {

    public final static int RIGHT = 0;
    public final static int UP = 1;
    public final static int LEFT = 2;
    public final static int DOWN = 3;
    public final static int[] DIRECTIONS = new int[] { RIGHT, UP, LEFT, DOWN };

    private final static byte BIT_RIGHT = 0b00000001;
    private final static byte BIT_UP = 0b00000010;
    private final static byte BIT_LEFT = 0b00000100;
    private final static byte BIT_DOWN = 0b00001000;
    private final static byte[] BITS_DIR = new byte[] { BIT_RIGHT, BIT_UP,
            BIT_LEFT, BIT_DOWN };
    private final static byte[][] OFFSETS_DIR = new byte[][] { { 0, 1 },
            { -1, 0 }, { 0, -1 }, { 1, 0 } };

    private final byte[][] board;
    private final int dim;

    public Board(int dim) {
        this.dim = dim;
        this.board = new byte[dim][dim];
    }

    public int getDim() {
        return dim;
    }

    public boolean isConnected(Point p, int dir) {
        return (board[p.x][p.y] & BITS_DIR[dir]) == BITS_DIR[dir];
    }

    public Point dest(Point p, int dir) {
        Point result = new Point(p.x, p.y);
        while (isConnected(result, dir)) {
            result.x += OFFSETS_DIR[dir][0];
            result.y += OFFSETS_DIR[dir][1];
        }
        return result;
    }

    public Point dest(Point p, int dir, Iterable<Robot> robots) {
        Point result = new Point(p.x, p.y);
        while (isConnected(result, dir)) {
            result.x += OFFSETS_DIR[dir][0];
            result.y += OFFSETS_DIR[dir][1];
            for (Robot robot : robots) {
                if (result.equals(robot.getPosition())) {
                    result.x -= OFFSETS_DIR[dir][0];
                    result.y -= OFFSETS_DIR[dir][1];
                    return result;
                }
            }
        }
        return result;
    }

    public Set<Point> reachable(Point p) {
        Set<Point> result = new HashSet<Point>();
        for (int dir : DIRECTIONS) {
            result.add(dest(p, dir));
        }
        return result;
    }

    public Set<Point> reachable(Point p, Iterable<Robot> robots) {
        Set<Point> result = new HashSet<Point>();
        for (int dir : DIRECTIONS) {
            result.add(dest(p, dir, robots));
        }
        return result;
    }
}
