/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.util.List;

/**
 * @author MarkusSchepke
 * 
 */
public class Solver {
    private final List<Robot> robots;
    private final Board board;
    private final Point target;
    private final Robot targetRobot;

    private final Iterable<List<Point>> solution;

    public Solver(Board board, List<Robot> robots, Point target,
            Robot targetRobot) {
        this.board = board;
        this.robots = robots;
        this.target = target;
        this.targetRobot = targetRobot;

        this.solution = brute();
    }

    /**
     * @return
     */
    private Iterable<List<Point>> brute() {
        // TODO Auto-generated method stub
        return null;
    }

    
}
