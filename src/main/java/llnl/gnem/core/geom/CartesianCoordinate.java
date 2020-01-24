package llnl.gnem.core.geom;

import net.jcip.annotations.Immutable;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author addair1
 */
@Immutable
public class CartesianCoordinate implements Coordinate<CartesianCoordinate> {

    private Vector3D point;

    /**
     * Limited visibility setter specifically added for the ECEFCoordinate class
     * which must first perform a conversion before setting the point member
     *
     * @param point
     */
    protected void setPoint(Vector3D point) {
        this.point = point;
    }

    public CartesianCoordinate(double x, double y) {
        this(x, y, 0.0);
    }

    public CartesianCoordinate(double x, double y, Double z) {
        point = new Vector3D(x, y, z!= null ? z : -999.0);
    }

    @Override
    public double getDistance(CartesianCoordinate other) {
        return point.distance(other.point);
    }

    @Override
    public double getElevation() {
        return getZ();
    }

    /**
     * Returns the coordinate as an array
     *
     * @return a double array of [x,y,z]
     */
    public double[] getArray() {
        if (point == null) {
            return null;
        }
        return point.toArray();
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", getX(), getY(), getZ());
    }

    public Vector3D getPoint() {
        return point;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public double getZ() {
        return point.getZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CartesianCoordinate other = (CartesianCoordinate) obj;
        if (this.point != other.point && (this.point == null || !this.point.equals(other.point))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.point != null ? this.point.hashCode() : 0);
        return hash;
    }
}
