package info.riemannhypothesis.ricochetrobots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Markus Schepke
 * @date 13 Aug 2014
 */
public class BoardPanel extends JPanel {

    private static final long serialVersionUID = 6045738513707473906L;

    public final int          fieldWidth, fieldHeight;
    public final int          totalWidth, totalHeight;
    public final int          dimX, dimY;

    private Board             board;
    private Set<Point>        targets;
    private Robot[]           robots;

    public BoardPanel(Board board, int fieldWidth, int fieldHeight) {
        this.board = board;
        targets = board.getTargets();

        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;

        dimX = board.getWidth();
        dimY = board.getHeight();

        totalWidth = fieldWidth * dimX;
        totalHeight = fieldHeight * dimY;

        setBackground(Color.GRAY);
        setSize(totalWidth, totalHeight);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        if (board.getWidth() != dimX || board.getHeight() != dimY) {
            throw new IllegalArgumentException(
                    "Dimension of new board must match the old one");
        }

        this.board = board;
    }

    public Set<Point> getTargets() {
        return targets;
    }

    public void setTargets(Set<Point> targets) {
        this.targets = targets;
    }

    public Robot[] getRobots() {
        return robots;
    }

    public void setRobots(Robot[] robots) {
        this.robots = robots;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);

        for (int x = 1; x < dimX; x++) {
            g.drawLine(x * fieldWidth, 0, x * fieldWidth, totalHeight);
        }
        for (int y = 1; y < dimY; y++) {
            g.drawLine(0, y * fieldHeight, totalWidth, y * fieldHeight);
        }

        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                Point p = new Point(x, y);
                if (x < dimX - 1 && !board.isConnected(p, Board.RIGHT)) {
                    g.fillRect((x + 1) * fieldWidth - 1, y * fieldHeight, 3,
                            fieldHeight);
                }
                if (y < dimY - 1 && !board.isConnected(p, Board.DOWN)) {
                    g.fillRect(x * fieldWidth, (y + 1) * fieldHeight - 1,
                            fieldWidth, 3);
                }
            }
        }

        if (targets != null) {
            g.setColor(Color.WHITE);
            for (Point p : targets) {
                g.fillRect(p.x * fieldWidth + 2, p.y * fieldHeight + 2,
                        fieldWidth - 4, fieldHeight - 4);
            }
        }

        if (robots != null) {
            for (Robot robot : robots) {
                g.setColor(robot.getColor());
                Point p = robot.getPosition();
                g.fillOval(p.x * fieldWidth + 2, p.y * fieldHeight + 2,
                        fieldWidth - 4, fieldHeight - 4);
            }
        }
    }

	public void save(String fileName, String formatName) throws IOException {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = image.createGraphics();
		paint(graphics2D);
		ImageIO.write(image, formatName, new File(fileName));
	}

    public static void main(String[] args) throws IOException {
        Board board = new Board(new FileInputStream(new File(args[0])));
        Robot[] robots = Robot.robotSet(board.getWidth(), board.getHeight(),
                new String[] { "Red", "Yellow", "Green", "Blue" });
        System.out.println(board.toString(robots, board.getTargets()));

        int fieldWidth = Integer.parseInt(args[1], 10);
        int fieldHeight = Integer.parseInt(args[2], 10);

        JFrame window = new JFrame("Ricochet Robots");

        BoardPanel content = new BoardPanel(board, fieldWidth, fieldHeight);
        content.setRobots(robots);

        System.out.println(content.dimX);
        System.out.println(content.dimY);
        System.out.println(content.fieldWidth);
        System.out.println(content.fieldHeight);
        System.out.println(content.totalWidth);
        System.out.println(content.totalHeight);

        window.setContentPane(content);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocation(100, 75);
        window.setSize(content.totalWidth, content.totalHeight + 25);
        window.setVisible(true);

    }

}
