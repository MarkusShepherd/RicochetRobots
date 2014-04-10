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

    public Point move(int dir) {
        return new Point(x + Board.OFFSETS_DIR[dir][0], y
                + Board.OFFSETS_DIR[dir][1]);
    }

    public Point move(int dirX, int dirY) {
        return new Point(x + Board.OFFSETS_DIR[dirX][0], y
                + Board.OFFSETS_DIR[dirY][1]);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}