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
package llnl.gnem.core.dataAccess.selectionCriteria.position;

/**
 *
 * @author dodge1
 */
public class LatLonBox {

    private final double minLat;
    private final double maxLat;
    private final double minLon;
    private final double maxLon;

    /**
     * @return the minLat
     */
    public double getMinLat() {
        return minLat;
    }

    /**
     * @return the maxLat
     */
    public double getMaxLat() {
        return maxLat;
    }

    /**
     * @return the minLon
     */
    public double getMinLon() {
        return minLon;
    }

    /**
     * @return the maxLon
     */
    public double getMaxLon() {
        return maxLon;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.minLat) ^ (Double.doubleToLongBits(this.minLat) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.maxLat) ^ (Double.doubleToLongBits(this.maxLat) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.minLon) ^ (Double.doubleToLongBits(this.minLon) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.maxLon) ^ (Double.doubleToLongBits(this.maxLon) >>> 32));
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
        final LatLonBox other = (LatLonBox) obj;
        if (Double.doubleToLongBits(this.minLat) != Double.doubleToLongBits(other.minLat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxLat) != Double.doubleToLongBits(other.maxLat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minLon) != Double.doubleToLongBits(other.minLon)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxLon) != Double.doubleToLongBits(other.maxLon)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LatLonBox{" + "minLat=" + minLat + ", maxLat=" + maxLat + ", minLon=" + minLon + ", maxLon=" + maxLon + '}';
    }

    public LatLonBox(double minLat, double maxLat, double minLon, double maxLon) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
    }

    public LatLonBox() {
        minLat = -90;
        maxLat = 90;
        minLon = -180;
        maxLon = 180;
    }
    
    public boolean contains(double lat, double lon){
        return lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon;
    }

}
