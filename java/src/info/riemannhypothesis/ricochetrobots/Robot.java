package info.riemannhypothesis.ricochetrobots;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author Markus Schepke
 */
public class Robot {

	private Point position;
	private final String label;
	private final char letter;
	private final Color color;

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

	public static Robot[] robotSet(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = br.readLine();
		String[] dims = line.split("\\s+", 2);

		int width = Integer.parseInt(dims[0], 10);
		int height = Integer.parseInt(dims[1], 10);
		HashMap<Character, Point> map = new HashMap<Character, Point>();

		for (int y = 0; y < 2 * height - 1; y++) {
			line = br.readLine();
			if (y % 2 == 1)
				continue;
			for (int x = 0; x < 2 * width - 1 && x < line.length(); x++)
				if (x % 2 == 0)
					map.put(line.charAt(x), new Point(x/2, y/2));
		}

		List<Robot> robots = new ArrayList<Robot>();
		
		while((line = br.readLine()) != null) {
			String[] info = line.split("\\s+");
			if (info.length < 3)
				continue;
			char symbol = info[0].charAt(0);
			String label = info[1];
			int rgb = Integer.parseInt(info[2], 16);
			Color color = new Color(rgb);
			Point point = map.get(symbol);
			if (point == null)
				return null;
			Robot robot = new Robot(label, color, point);
			robots.add(robot);
		}
		
		if (robots.size() == 0)
			return null;
		
		Robot[] robotArray = new Robot[robots.size()];
		return robots.toArray(robotArray);
	}

}
