/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria.position;

import java.util.Objects;
import llnl.gnem.core.util.Geometry.EModel;

/**
 *
 * @author dodge1
 */
public class CircleRestrictionImpl implements PositionRestriction {

    private final double centerLat;
    private final double centerLon;
    private final double radiusDeg;
    private final LatLonBox allowableBox;

    CircleRestrictionImpl(double centerLat, double centerLon, double radiusDeg) {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.radiusDeg = radiusDeg;
        double maxLat = Math.min(90.0, centerLat + radiusDeg);
        double minLat = Math.max(-90, centerLat - radiusDeg);
        double minLon = Math.max(-180, centerLon - radiusDeg);
        double maxLon = Math.min(180, centerLon + radiusDeg);
        allowableBox = new LatLonBox(minLat, maxLat, minLon, maxLon);
    }

    @Override
    public boolean isInside(double lat, double lon) {
        double delta = EModel.getDeltaWGS84(lat, lon, centerLat, centerLon);
        return delta <= radiusDeg;
    }

    @Override
    public LatLonBox getSqlLimits() {
        return allowableBox;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.centerLat) ^ (Double.doubleToLongBits(this.centerLat) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.centerLon) ^ (Double.doubleToLongBits(this.centerLon) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.radiusDeg) ^ (Double.doubleToLongBits(this.radiusDeg) >>> 32));
        hash = 71 * hash + Objects.hashCode(this.allowableBox);
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
        final CircleRestrictionImpl other = (CircleRestrictionImpl) obj;
        if (Double.doubleToLongBits(this.centerLat) != Double.doubleToLongBits(other.centerLat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.centerLon) != Double.doubleToLongBits(other.centerLon)) {
            return false;
        }
        if (Double.doubleToLongBits(this.radiusDeg) != Double.doubleToLongBits(other.radiusDeg)) {
            return false;
        }
        if (!Objects.equals(this.allowableBox, other.allowableBox)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CircleRestriction{" + "centerLat=" + centerLat + ", centerLon=" + centerLon + ", radiusDeg=" + radiusDeg + ", allowableBox=" + allowableBox + '}';
    }

    @Override
    public String getSQLClause() {
        String sql = String.format(" and lat between %f and %f and lon between %f and %f ",
                allowableBox.getMinLat(), allowableBox.getMaxLat(),
                allowableBox.getMinLon(), allowableBox.getMaxLon());
        return sql;
    }

}
