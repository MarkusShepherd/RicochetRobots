/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author MarkusSchepke
 * 
 */
public class Solver {
    private final Board board;
    private final Point target;
    // private final Set<Robot> robots;
    private final Map<Robot, Integer> indexes;
    // private final Robot targetRobot;
    private final int targetRobotIndex;
    private final int numberRobots;
    private final int moves;

    private final List<Point[]> solution;

    public Solver(Board board, Set<Robot> robots, Point target,
            Robot targetRobot) {
        assert robots.contains(targetRobot);
        this.board = board;
        // this.robots = robots;
        this.target = target;
        // this.targetRobot = targetRobot;

        this.numberRobots = robots.size();
        this.indexes = new HashMap<Robot, Integer>();
        Point[] initial = new Point[numberRobots];
        int counter = 0;
        for (Robot robot : robots) {
            indexes.put(robot, counter);
            initial[counter] = robot.getPosition();
            counter++;
        }
        this.targetRobotIndex = indexes.get(targetRobot);

        this.solution = solveBruteForce(initial);
        this.moves = this.solution.size();
    }

    /**
     * @return
     */
    private List<Point[]> solveBruteForce(Point[] initial) {
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(new Node(initial, 0, null));
        Node current;
        while ((current = queue.poll()) != null) {
            if (current.configuration[targetRobotIndex].equals(target)) {
                break;
            }
            for (int i = 0; i < numberRobots; i++) {
                Point position = current.configuration[i];
                for (Point dest : board.reachable(position,
                        current.configuration)) {
                    if (!dest.equals(position)) {
                        Point[] newConfig = Arrays.copyOf(
                                current.configuration, numberRobots);
                        newConfig[i] = dest;
                        queue.add(new Node(newConfig, current.moves + 1,
                                current));
                    }
                }
            }
        }
        LinkedList<Point[]> result = new LinkedList<Point[]>();
        while (current != null) {
            result.add(current.configuration);
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
}
