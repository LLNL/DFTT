/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria.position;

import java.util.Objects;
import llnl.gnem.core.polygon.PolygonSet;

/**
 *
 * @author dodge1
 */
 class PolygonRestrictionImpl implements PositionRestriction {

    private final PolygonSet polySet;
    private final LatLonBox allowableBox;

    public PolygonRestrictionImpl(PolygonSet polySet) {
        this.polySet = polySet;
        allowableBox = new LatLonBox(polySet.getMinLat(), polySet.getMaxLat(), 
                polySet.getMinLon(), polySet.getMaxLon());
    }

    @Override
    public boolean isInside(double lat, double lon) {
        return polySet.contains(lat, lon);
    }

    @Override
    public LatLonBox getSqlLimits() {
        return allowableBox;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.polySet);
        hash = 23 * hash + Objects.hashCode(this.allowableBox);
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
        final PolygonRestrictionImpl other = (PolygonRestrictionImpl) obj;
        if (!Objects.equals(this.polySet, other.polySet)) {
            return false;
        }
        if (!Objects.equals(this.allowableBox, other.allowableBox)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PolygonRestriction{" + "polygon=" + polySet + ", allowableBox=" + allowableBox + '}';
    }

    @Override
    public String getSQLClause() {
        String sql = String.format(" and lat between %f and %f and lon between %f and %f ",
                allowableBox.getMinLat(), allowableBox.getMaxLat(),
                allowableBox.getMinLon(), allowableBox.getMaxLon());
        return sql;
    }

}
