/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.geom;

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
