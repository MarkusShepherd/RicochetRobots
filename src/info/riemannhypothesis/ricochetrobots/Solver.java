/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @author MarkusSchepke
 * 
 */
public class Solver {
    // private final Set<Robot> robots;
    private final Map<Robot, Integer> indexes;
    private final Board board;
    private final Point target;
    private final Robot targetRobot;

    private final Iterable<Point[]> solution;

    public Solver(Board board, Set<Robot> robots, Point target,
            Robot targetRobot) {
        assert robots.contains(targetRobot);
        this.board = board;
        // this.robots = robots;
        this.target = target;
        this.targetRobot = targetRobot;

        int counter = 0;
        indexes = new HashMap<Robot, Integer>();
        for (Robot robot : robots) {
            indexes.put(robot, counter++);
        }

        this.solution = brute();
    }

    /**
     * @return
     */
    private Iterable<Point[]> brute() {
        LinkedList<Point[]> result = new LinkedList<Point[]>();
        return result;
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
