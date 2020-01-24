/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria.position;

import java.io.Serializable;

/**
 *
 * @author dodge1
 */
public class CircleRestriction implements PositionRestrictionSpec, Serializable{

    /**
     * @return the centerLat
     */
    public double getCenterLat() {
        return centerLat;
    }

    /**
     * @return the centerLon
     */
    public double getCenterLon() {
        return centerLon;
    }

    /**
     * @return the radiusDeg
     */
    public double getRadiusDeg() {
        return radiusDeg;
    }

    private final double centerLat;
    private final double centerLon;
    private final double radiusDeg;
    private static final long serialVersionUID = -1137397375455231846L;

    public CircleRestriction(double centerLat, double centerLon, double radiusDeg) {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.radiusDeg = radiusDeg;
    }
    
    
    @Override
    public PositionRestriction getImplementation() {
        return new CircleRestrictionImpl(centerLat,centerLon,radiusDeg);
    }
    
}
