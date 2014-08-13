package info.riemannhypothesis.ricochetrobots;

import info.riemannhypothesis.ricochetrobots.Solver.MoveNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Markus Schepke
 */
public class Board {

    public static final int      RIGHT            = 0;
    public static final int      UP               = 1;
    public static final int      LEFT             = 2;
    public static final int      DOWN             = 3;
    public static final int[]    DIRECTIONS       = new int[] { RIGHT, UP,
            LEFT, DOWN                           };
    public static final int[][]  PERP             = new int[][] { { UP, DOWN },
            { LEFT, RIGHT }, { UP, DOWN }, { LEFT, RIGHT } };
    public static final int[]    OPPOSITE         = new int[] { LEFT, DOWN,
            RIGHT, UP                            };

    public static final char[][] BARS             = new char[][] {
            { ' ', '\\', '|', '/' }, { '\\', ' ', '/', '-' },
            { '|', '/', ' ', '\\' }, { '/', '-', '\\', ' ' } };
    public static final char     CONNECTED_CHAR   = ' ';
    public static final char     EMPTY_FIELD_CHAR = '\u2591';
    public static final char     TARGET_CHAR      = '\u2593';

    private static final byte    BIT_RIGHT        = 0b00000001;
    private static final byte    BIT_UP           = 0b00000010;
    private static final byte    BIT_LEFT         = 0b00000100;
    private static final byte    BIT_DOWN         = 0b00001000;
    private static final byte[]  BITS_DIR         = new byte[] { BIT_RIGHT,
            BIT_UP, BIT_LEFT, BIT_DOWN           };
    public static final byte[][] OFFSETS_DIR      = new byte[][] { { 1, 0 },
            { 0, -1 }, { -1, 0 }, { 0, 1 }       };

    private final byte[][]       board;
    private final int            width, height;

    private final Set<Point>     targets;

    public Board(int dim) {
        width = dim;
        height = dim;
        board = new byte[height][width];
        targets = new HashSet<Point>();
    }

    public Board(InputStream is) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = br.readLine();
        String[] dims = line.split("\\s+", 2);

        width = Integer.parseInt(dims[0], 10);
        height = Integer.parseInt(dims[1], 10);
        board = new byte[width][height];
        targets = new HashSet<Point>();

        char[][] input = new char[2 * width - 1][2 * height - 1];

        for (int y = 0; y < 2 * height - 1; y++) {
            line = br.readLine();
            for (int x = 0; x < 2 * width - 1 && x < line.length(); x++) {
                input[x][y] = line.charAt(x);
            }
        }

        br.close();
        br = null;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                byte tile = 0;
                // can go left?
                if (x > 0 && input[2 * x - 1][2 * y] != '|') {
                    tile |= BIT_LEFT;
                }
                if (x < width - 1 && input[2 * x + 1][2 * y] != '|') {
                    tile |= BIT_RIGHT;
                }
                if (y > 0 && input[2 * x][2 * y - 1] != '-') {
                    tile |= BIT_UP;
                }
                if (y < height - 1 && input[2 * x][2 * y + 1] != '-') {
                    tile |= BIT_DOWN;
                }

                board[x][y] = tile;

                if (input[2 * x][2 * y] == 'X') {
                    targets.add(new Point(x, y));
                }
            }
        }

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Set<Point> getTargets() {
        return targets;
    }

    public boolean isConnected(Point p, int dir) {
        return (board[p.x][p.y] & BITS_DIR[dir]) == BITS_DIR[dir];
    }

    public boolean isConnected(Point p, int dir, Point[] blocked) {
        if (!isConnected(p, dir)) {
            return false;
        }
        Point result = p.move(dir);
        for (Point block : blocked) {
            if (result.equals(block)) {
                return false;
            }
        }
        return true;
    }

    public Point dest(Point p, int dir) {
        if (!isConnected(p, dir)) {
            return p;
        }
        Point result = new Point(p.x, p.y);
        do {
            result.x += OFFSETS_DIR[dir][0];
            result.y += OFFSETS_DIR[dir][1];
        } while (isConnected(result, dir));
        return result;
    }

    public Point dest(Point p, int dir, Point[] blocked) {
        if (!isConnected(p, dir)) {
            return p;
        }
        Point result = new Point(p.x, p.y);
        do {
            result.x += OFFSETS_DIR[dir][0];
            result.y += OFFSETS_DIR[dir][1];
            for (Point block : blocked) {
                if (result.equals(block)) {
                    result.x -= OFFSETS_DIR[dir][0];
                    result.y -= OFFSETS_DIR[dir][1];
                    return result;
                }
            }
        } while (isConnected(result, dir));
        return result;
    }

    public Set<Point> reachable(Point p) {
        Set<Point> result = new HashSet<Point>();
        for (int dir : DIRECTIONS) {
            result.add(dest(p, dir));
        }
        return result;
    }

    public Set<Point> reachable(Point p, Point[] blocked) {
        Set<Point> result = new HashSet<Point>();
        for (int dir : DIRECTIONS) {
            result.add(dest(p, dir, blocked));
        }
        return result;
    }

    @Override
    public String toString() {
        return toString(null, targets, null);
    }

    public String toString(Robot[] robots, Set<Point> targets) {
        return toString(robots, targets, null);
    }

    public String toString(Robot[] robots, Set<Point> targets,
            HashMap<Point, MoveNode> map) {
        StringBuilder result = new StringBuilder();
        HashMap<Point, Character> robotsMarkers = new HashMap<Point, Character>();
        if (robots != null) {
            for (Robot robot : robots) {
                robotsMarkers.put(robot.getPosition(), robot.getLetter());
            }
        }

        result.append(BARS[RIGHT][DOWN]);
        for (int x = 0; x < width * 2 - 1; x++) {
            result.append(BARS[UP][DOWN]);
        }
        result.append(BARS[LEFT][DOWN]);
        result.append('\n');

        for (int y = 0; y < height; y++) {
            result.append(BARS[LEFT][RIGHT]);
            for (int x = 0; x < width; x++) {

                Point p = new Point(x, y);

                if (robotsMarkers.containsKey(p)) {
                    result.append(robotsMarkers.get(p).charValue());
                } else if (targets.contains(p)) {
                    result.append(TARGET_CHAR);
                } else if (map != null && map.containsKey(p)) {
                    int movesToTarget = map.get(p).moves;
                    result.append(movesToTarget);
                } else {
                    result.append(EMPTY_FIELD_CHAR);
                }

                if (!isConnected(p, RIGHT)) {
                    result.append(BARS[LEFT][RIGHT]);
                } else {
                    result.append(CONNECTED_CHAR);
                }
            }

            result.append('\n');

            if (y < height - 1) {
                result.append(BARS[LEFT][RIGHT]);

                for (int x = 0; x < width; x++) {
                    Point p = new Point(x, y);

                    if (!isConnected(p, DOWN)) {
                        result.append(BARS[UP][DOWN]);
                    } else {
                        result.append(CONNECTED_CHAR);
                    }

                    if (x < width - 1) {
                        /* Point downRight = p.move(DOWN, RIGHT); Point right =
                         * p.move(RIGHT); Point down = p.move(DOWN);
                         * 
                         * if (!isConnected(downRight, UP) &&
                         * !isConnected(downRight, LEFT)) {
                         * result.append(BARS[RIGHT][DOWN]); } else if
                         * (!isConnected(right, LEFT) && !isConnected(right,
                         * DOWN)) { result.append(BARS[RIGHT][UP]); } else if
                         * (!isConnected(down, RIGHT) && !isConnected(down, UP))
                         * { result.append(BARS[LEFT][DOWN]); } else if
                         * (!isConnected(p, RIGHT) && !isConnected(p, DOWN)) {
                         * result.append(BARS[LEFT][UP]); } else {
                         * result.append(CONNECTED_CHAR); } */

                        result.append(CONNECTED_CHAR);
                    }
                }
                result.append(BARS[LEFT][RIGHT]);
                result.append('\n');
            }
        }
        result.append(BARS[RIGHT][UP]);
        for (int x = 0; x < width * 2 - 1; x++) {
            result.append(BARS[UP][DOWN]);
        }
        result.append(BARS[LEFT][UP]);
        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        Board board = new Board(new FileInputStream(new File(args[0])));
        System.out.println(board.toString());
    }
}
