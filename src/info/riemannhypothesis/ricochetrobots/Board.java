/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
    private final int dimX, dimY;

    private final Set<Point> targets;

    public Board(int dim) {
        dimX = dim;
        dimY = dim;
        board = new byte[dimX][dimY];
        targets = new HashSet<Point>();
    }

    public Board(File file) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line = br.readLine();
        String[] dims = line.split("\\s+", 2);

        dimX = Integer.parseInt(dims[0], 10);
        dimY = Integer.parseInt(dims[1], 10);
        board = new byte[dimX][dimY];
        targets = new HashSet<Point>();

        char[][] input = new char[2 * dimX - 1][2 * dimX - 1];

        for (int x = 0; x < 2 * dimX - 1; x++) {
            line = br.readLine();
            for (int y = 0; y < 2 * dimY - 1; y++) {
                input[x][y] = line.charAt(y);
            }
        }

        br.close();
        br = null;

        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                byte tile = 0;
                // can go left?
                if (y > 0 && input[2 * x][2 * y - 1] != '|') {
                    tile |= BIT_LEFT;
                }
                if (y < dimY - 1 && input[2 * x][2 * y + 1] != '|') {
                    tile |= BIT_RIGHT;
                }
                if (x > 0 && input[2 * x - 1][2 * y] != '-') {
                    tile |= BIT_UP;
                }
                if (x < dimX - 1 && input[2 * x + 1][2 * y] != '-') {
                    tile |= BIT_DOWN;
                }

                board[x][y] = tile;

                if (input[2 * x][2 * y] == 'X') {
                    targets.add(new Point(x, y));
                }
            }
        }

    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }

    public boolean isConnected(Point p, int dir) {
        return (board[p.x][p.y] & BITS_DIR[dir]) == BITS_DIR[dir];
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

    /**
     * 
     * @param p
     * @param dir
     * @param blocked
     * @return
     */
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

    /**
     * 
     * @param p
     * @return
     */
    public Set<Point> reachable(Point p) {
        Set<Point> result = new HashSet<Point>();
        for (int dir : DIRECTIONS) {
            result.add(dest(p, dir));
        }
        return result;
    }

    /**
     * 
     * @param p
     * @param blocked
     * @return
     */
    public Set<Point> reachable(Point p, Point[] blocked) {
        Set<Point> result = new HashSet<Point>();
        for (int dir : DIRECTIONS) {
            result.add(dest(p, dir, blocked));
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int y = 0; y < dimY * 2 + 1; y++) {
            result.append('-');
        }
        result.append('\n');
        for (int x = 0; x < dimX; x++) {
            result.append('|');
            for (int y = 0; y < dimY; y++) {
                Point p = new Point(x, y);
                if (targets.contains(p)) {
                    result.append('X');
                } else {
                    result.append(' ');
                }
                if (!isConnected(p, RIGHT)) {
                    result.append('|');
                } else {
                    result.append(' ');
                }
            }
            result.append('\n');
            if (x < dimX - 1) {
                result.append('|');
                for (int y = 0; y < dimY; y++) {
                    Point p = new Point(x, y);
                    if (!isConnected(p, DOWN)) {
                        result.append('-');
                    } else {
                        result.append(' ');
                    }
                    if (y < dimY - 1) {
                        result.append(' ');
                    }
                }
                result.append('|');
                result.append('\n');
            }
        }
        for (int y = 0; y < dimY * 2 + 1; y++) {
            result.append('-');
        }
        return result.toString();
    }

    /**
     * 
     * @param robots
     * @param target
     * @param targetRobot
     * @return
     */
    public String toString(Robot[] robots, Point target) {
        StringBuilder result = new StringBuilder();
        HashMap<Point, Character> robotsMarkers = new HashMap<Point, Character>();
        for (Robot robot : robots) {
            robotsMarkers.put(robot.getPosition(), robot.getLetter());
        }

        for (int y = 0; y < dimY * 2 + 1; y++) {
            result.append('\u2588');
        }
        result.append('\n');
        for (int x = 0; x < dimX; x++) {
            result.append('\u2588');
            for (int y = 0; y < dimY; y++) {
                Point p = new Point(x, y);
                if (robotsMarkers.containsKey(p)) {
                    result.append(robotsMarkers.get(p).charValue());
                } else if (target.equals(p)) {
                    result.append('X');
                } else {
                    result.append(' ');
                }
                if (!isConnected(p, RIGHT)) {
                    result.append('\u2588');
                } else {
                    result.append(' ');
                }
            }
            result.append('\n');
            if (x < dimX - 1) {
                result.append('\u2588');
                for (int y = 0; y < dimY; y++) {
                    Point p = new Point(x, y);
                    if (!isConnected(p, DOWN)) {
                        result.append('\u2588');
                    } else {
                        result.append(' ');
                    }
                    if (y < dimY - 1) {
                        result.append(' ');
                    }
                }
                result.append('\u2588');
                result.append('\n');
            }
        }
        for (int y = 0; y < dimY * 2 + 1; y++) {
            result.append('\u2588');
        }
        return result.toString();
    }

    /**
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        Board board = new Board(file);
        System.out.println(board.toString());

        System.out.println();
        System.out.println();
        Robot[] robots = Robot.robotSet(board.getDimX(), new String[] { "Red",
                "Yellow", "Green", "Blue" });
        Point target = (Point) board.targets.toArray()[(int) (Math.random() * board.targets
                .size())];
        // System.out.println(board.toString(robots, target));

        System.out.println();
        System.out.println();
        Solver solver = new Solver(board, robots, target, 0);
        System.out.println(solver.moves());

        for (Point[] config : solver.solution()) {
            // update robot positions
            for (int i = 0; i < config.length; i++) {
                robots[i].setPosition(config[i]);
            }
            System.out.println(board.toString(robots, target));
            System.out.println();
        }
    }
}
