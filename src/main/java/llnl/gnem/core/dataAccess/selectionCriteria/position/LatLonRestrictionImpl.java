/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria.position;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
class LatLonRestrictionImpl implements PositionRestriction {

    private final LatLonBox allowableBox;

    public LatLonRestrictionImpl(LatLonBox box) {
        allowableBox = box;
    }

    public LatLonRestrictionImpl(double minLat, double maxLat, double minLon, double maxLon) {
        allowableBox = new LatLonBox(minLat, maxLat, minLon, maxLon);
    }

    @Override
    public boolean isInside(double lat, double lon) {
        return allowableBox.contains(lat, lon);
    }

    @Override
    public LatLonBox getSqlLimits() {
        return allowableBox;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.allowableBox);
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
        final LatLonRestrictionImpl other = (LatLonRestrictionImpl) obj;
        if (!Objects.equals(this.allowableBox, other.allowableBox)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LatLonRestriction{" + "allowableBox=" + allowableBox + '}';
    }

    @Override
    public String getSQLClause() {
        String sql = String.format(" and lat between %f and %f and lon between %f and %f ",
                allowableBox.getMinLat(), allowableBox.getMaxLat(),
                allowableBox.getMinLon(), allowableBox.getMaxLon());
        return sql;
    }

}
