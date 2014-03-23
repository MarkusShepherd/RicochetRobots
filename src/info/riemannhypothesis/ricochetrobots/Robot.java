/**
 * 
 */
package info.riemannhypothesis.ricochetrobots;

import java.awt.Color;
import java.util.HashSet;

/**
 * @author MarkusSchepke
 * 
 */
public class Robot {

    private Point position;
    private final String label;
    private final Color color;

    public Robot(String label, Color color, Point position) {
        this.label = label;
        this.color = color;
        this.position = position;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    public static HashSet<Robot> robotSet(int dim, String[] labels) {
        HashSet<Robot> robots = new HashSet<Robot>();
        HashSet<Point> positions = new HashSet<Point>();
        HashSet<Color> colors = new HashSet<Color>();
        for (String label : labels) {
            Color c;
            do {
                c = new Color((int) (Math.random() * 16777216));
            } while (colors.contains(c));
            colors.add(c);
            Point p;
            do {
                p = new Point((int) (Math.random() * dim),
                        (int) (Math.random() * dim));
            } while (positions.contains(p));
            positions.add(p);
            robots.add(new Robot(label, c, p));
        }
        return robots;
    }

    public static HashSet<Robot> robotSet(int dim, int number) {
        String[] labels = new String[number];
        for (int i = 0; i < number; i++) {
            labels[i] = "Robot #" + (i + 1);
        }
        return robotSet(dim, labels);
    }

}
