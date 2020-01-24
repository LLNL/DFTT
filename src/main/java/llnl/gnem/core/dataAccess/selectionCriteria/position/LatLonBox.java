/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
