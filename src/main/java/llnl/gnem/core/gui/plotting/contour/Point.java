/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.contour;

public class Point {

    private double x;
    private double y;

    public Point() {
        super();
    }

    public Point(final double x, final double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    @Override
    public boolean equals(final Object p) {
        boolean result;
        if (p.getClass().isAssignableFrom(Point.class)) {
            final Point pp = (Point) p;
            result = ((x == pp.getX()) && (y == pp.getY()));
        } else {
            result = this.equals(p);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "[" + getX() + "," + getY() + "]";
    }
}
