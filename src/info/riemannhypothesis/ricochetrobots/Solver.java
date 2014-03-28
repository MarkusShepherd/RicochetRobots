/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author MarkusSchepke
 * 
 */
public class Solver {

    public final static int DEFAULT_MAX_MOVES = 7;
    public final static double DEFAULT_MAX_TIME = 10 * 60;

    private final Board board;
    private final Point target;
    // private final Set<Robot> robots;
    private final Map<Robot, Integer> indexes;
    // private final Robot targetRobot;
    private final int targetRobotIndex;
    private final int numberRobots;
    private final int moves;

    private final List<Point[]> solution;

    public Solver(Board board, Robot[] robots, Point target, int targetRobot) {
        this(board, robots, target, targetRobot, DEFAULT_MAX_MOVES,
                DEFAULT_MAX_TIME);
    }

    public Solver(Board board, Robot[] robots, Point target, int targetRobot,
            int maxMoves) {
        this(board, robots, target, targetRobot, maxMoves, DEFAULT_MAX_TIME);
    }

    public Solver(Board board, Robot[] robots, Point target, int targetRobot,
            int maxMoves, double maxTime) {
        // assert robots.contains(targetRobot);
        this.board = board;
        // this.robots = robots;
        this.target = target;
        // this.targetRobot = targetRobot;

        this.numberRobots = robots.length;
        this.indexes = new HashMap<Robot, Integer>();
        Point[] initial = new Point[numberRobots];
        int counter = 0;
        for (Robot robot : robots) {
            indexes.put(robot, counter);
            initial[counter] = robot.getPosition();
            counter++;
        }
        this.targetRobotIndex = targetRobot;

        this.solution = solveBruteForce(initial, maxMoves, maxTime);
        this.moves = this.solution == null ? -1 : this.solution.size() - 1;
    }

    /**
     * Finds a solution by trying out all possible moves.
     * 
     * @param initial
     *            the initial configuration of the robots
     * @param maxMoves
     *            the maximal number of moves to be searched
     * @return a List of configurations representing the moves of the solution
     *         found, or null if none could be found withing {@link maxMoves}.
     */
    private List<Point[]> solveBruteForce(Point[] initial, int maxMoves,
            double maxTime) {
        long start = System.nanoTime();
        long end = (long) (start + maxTime * 1e9);
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(new Node(initial, 0, null));
        Node current;
        boolean solved = false;
        while ((current = queue.poll()) != null) {
            if (current.configuration[targetRobotIndex].equals(target)) {
                solved = true;
                break;
            }
            if (System.nanoTime() >= end) {
                break;
            }
            if (current.moves >= maxMoves) {
                continue;
            }
            for (int i = 0; i < numberRobots; i++) {
                Point position = current.configuration[i];
                for (Point dest : board.reachable(position,
                        current.configuration)) {
                    if (!dest.equals(position)) {
                        Point[] newConfig = Arrays.copyOf(
                                current.configuration, numberRobots);
                        newConfig[i] = dest;
                        if (current.previous == null
                                || !Arrays.equals(newConfig,
                                        current.previous.configuration)) {
                            queue.add(new Node(newConfig, current.moves + 1,
                                    current));
                        }
                    }
                }
            }
        }
        if (!solved) {
            return null;
        }
        LinkedList<Point[]> result = new LinkedList<Point[]>();
        while (current != null) {
            // result.add(current.configuration);
            result.addFirst(current.configuration);
            current = current.previous;
        }
        return result;
    }

    public int moves() {
        return moves;
    }

    public List<Point[]> solution() {
        return solution;
    }

    private class Node {
        private final Point[] configuration;
        private final int moves;
        private final Node previous;

        private Node(Point[] configuration, int moves, Node previous) {
            this.configuration = configuration;
            this.moves = moves;
            this.previous = previous;
        }
    }

    /**
     * Constructs a board from a file given through a command line argument and
     * tries to solve it for a random configuration of robots.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Board board = new Board(new FileInputStream(new File(args[0])));
        int maxMoves = DEFAULT_MAX_MOVES;
        if (args.length >= 2) {
            try {
                maxMoves = Integer.parseInt(args[1], 10);
            } catch (NumberFormatException e) {
                maxMoves = DEFAULT_MAX_MOVES;
            }
        }

        double maxTime = DEFAULT_MAX_TIME;
        if (args.length >= 3) {
            try {
                maxTime = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                maxTime = DEFAULT_MAX_TIME;
            }
        }

        Robot[] robots = Robot.robotSet(board.getDimX(), board.getDimY(),
                new String[] { "Red", "Yellow", "Green", "Blue" });
        int targetRobot = (int) (Math.random() * robots.length);
        Point target = (Point) board.getTargets().toArray()[(int) (Math
                .random() * board.getTargets().size())];
        HashSet<Point> targetSet = new HashSet<Point>();
        targetSet.add(target);
        System.out.println("Searching solution for robot "
                + robots[targetRobot].getLabel() + " on board:");
        System.out.println(board.toString(robots, targetSet));
        System.out.println();

        long start = System.nanoTime();
        Solver solver = new Solver(board, robots, target, targetRobot,
                maxMoves, maxTime);
        long end = System.nanoTime();
        double seconds = (end - start) / 1000000000.0;

        if (solver.solution() == null) {
            System.out.println("No solution found with " + maxMoves
                    + " moves or aborted after " + seconds + " seconds.");
            return;
        }

        System.out.println("Found solution in " + seconds + " seconds with "
                + solver.moves() + " moves:");

        int counter = 0;
        for (Point[] config : solver.solution()) {
            if (counter++ == 0) {
                continue;
            }
            // update robot positions
            for (int i = 0; i < config.length; i++) {
                robots[i].setPosition(config[i]);
            }
            System.out.println(board.toString(robots, targetSet));
            System.out.println();
        }
    }
}
