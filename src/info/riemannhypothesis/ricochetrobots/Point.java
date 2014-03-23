package info.riemannhypothesis.ricochetrobots;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point that = (Point) obj;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        return x ^ y;
    }

    public Point right() {
        return new Point(x, y + 1);
    }

    public Point up() {
        return new Point(x - 1, y);
    }

    public Point left() {
        return new Point(x, y - 1);
    }

    public Point down() {
        return new Point(x + 1, y);
    }
}