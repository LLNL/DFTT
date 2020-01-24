/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

/**
 *
 * @author dodge1
 */
public class DetectorProjection {
    private final int detectorid;
    private final int shift;
    private final double projection;

    public DetectorProjection(int detectorid, int shift, double projection) {
        this.detectorid = detectorid;
        this.shift = shift;
        this.projection = projection;
    }

    @Override
    public String toString() {
        return "DetectorProjection{" + "detectorid=" + detectorid + ", shift=" + shift + ", projection=" + projection + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.detectorid;
        hash = 13 * hash + this.shift;
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.projection) ^ (Double.doubleToLongBits(this.projection) >>> 32));
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
        final DetectorProjection other = (DetectorProjection) obj;
        if (this.detectorid != other.detectorid) {
            return false;
        }
        if (this.shift != other.shift) {
            return false;
        }
        if (Double.doubleToLongBits(this.projection) != Double.doubleToLongBits(other.projection)) {
            return false;
        }
        return true;
    }

    /**
     * @return the detectorid
     */
    public int getDetectorid() {
        return detectorid;
    }

    /**
     * @return the shift
     */
    public int getShift() {
        return shift;
    }

    /**
     * @return the projection
     */
    public double getProjection() {
        return projection;
    }
    
}
