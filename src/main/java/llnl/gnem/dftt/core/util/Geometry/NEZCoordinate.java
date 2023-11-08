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
package llnl.gnem.dftt.core.util.Geometry;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class NEZCoordinate {

    private final double xNorthKm;
    private final double yEastKm;
    private final double zDownKm;

    public NEZCoordinate(double xNorthKm, double yEastKm, double zDownKm) {
        this.xNorthKm = xNorthKm;
        this.yEastKm = yEastKm;
        this.zDownKm = zDownKm;
    }

    public NEZCoordinate(Vector3D nezKmVector) {
        xNorthKm = nezKmVector.getX();
        yEastKm = nezKmVector.getY();
        zDownKm = nezKmVector.getZ();
    }

    @Override
    public String toString() {
        return "NEZCoordinate{" + "xNorthKm=" + getxNorthKm() + ", yEastKm=" + getyEastKm() + ", zDownKm=" + getzDownKm() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.getxNorthKm()) ^ (Double.doubleToLongBits(this.getxNorthKm()) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.getyEastKm()) ^ (Double.doubleToLongBits(this.getyEastKm()) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.getzDownKm()) ^ (Double.doubleToLongBits(this.getzDownKm()) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NEZCoordinate other = (NEZCoordinate) obj;
        if (Double.doubleToLongBits(this.getxNorthKm()) != Double.doubleToLongBits(other.getxNorthKm())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getyEastKm()) != Double.doubleToLongBits(other.getyEastKm())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getzDownKm()) != Double.doubleToLongBits(other.getzDownKm())) {
            return false;
        }
        return true;
    }

    /**
     * @return the xNorthKm
     */
    public double getxNorthKm() {
        return xNorthKm;
    }

    /**
     * @return the yEastKm
     */
    public double getyEastKm() {
        return yEastKm;
    }

    /**
     * @return the zDownKm
     */
    public double getzDownKm() {
        return zDownKm;
    }

    public NEZCoordinate subtract(NEZCoordinate other) {
        return new NEZCoordinate(xNorthKm - other.xNorthKm, yEastKm - other.yEastKm, zDownKm - other.zDownKm);
    }

    public double getNorm() {
        return Math.sqrt(xNorthKm * xNorthKm + yEastKm * yEastKm + zDownKm * zDownKm);
    }

    public Vector3D toVector3D() {
       return new Vector3D(xNorthKm, yEastKm,zDownKm);
    }

}
