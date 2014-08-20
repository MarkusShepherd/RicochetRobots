package info.riemannhypothesis.ricochetrobots;

import java.awt.Color;
import java.util.HashSet;

/**
 * @author Markus Schepke
 */
public class Robot {

    private Point        position;
    private final String label;
    private final char   letter;
    private final Color  color;

    public Robot(String label, Color color, Point position) {
        this.label = label;
        this.letter = label.charAt(0);
        this.color = color;
        this.position = position;
    }

    public String getLabel() {
        return label;
    }

    public char getLetter() {
        return letter;
    }

    public Color getColor() {
        return color;
    }

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

    public static Robot[] robotSet(int dimX, int dimY, String[] labels,
            Color[] colors) {
        Robot[] robots = new Robot[labels.length];
        HashSet<Point> positions = new HashSet<Point>();
        int counter = 0;
        for (String label : labels) {
            Point p;
            do {
                p = new Point((int) (Math.random() * dimX),
                        (int) (Math.random() * dimY));
            } while (positions.contains(p));
            positions.add(p);
            robots[counter] = new Robot(label, colors[counter], p);
            counter++;
        }
        return robots;
    }

    public static Robot[] robotSet(int dimX, int dimY, String[] labels) {
        HashSet<Color> colors = new HashSet<Color>();
        for (int i = 0; i < labels.length; i++) {
            Color c;
            do {
                c = new Color((int) (Math.random() * 16777216));
            } while (colors.contains(c));
            colors.add(c);
        }
        return robotSet(dimX, dimY, labels, (Color[]) colors.toArray());
    }

    public static Robot[] robotSet(int dimX, int dimY, int number) {
        String[] labels = new String[number];
        for (int i = 0; i < number; i++) {
            labels[i] = Integer.toString(i + 1, 10);
        }
        return robotSet(dimX, dimY, labels);
    }

}
