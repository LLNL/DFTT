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
public class LatLonRestriction implements PositionRestrictionSpec, Serializable {

    private final double minLat;
    private final double minLon;
    private final double maxLat;
    private final double maxLon;

    /**
     * @return the minLat
     */
    public double getMinLat() {
        return minLat;
    }

    /**
     * @return the minLon
     */
    public double getMinLon() {
        return minLon;
    }

    /**
     * @return the maxLat
     */
    public double getMaxLat() {
        return maxLat;
    }

    /**
     * @return the maxLon
     */
    public double getMaxLon() {
        return maxLon;
    }

    private static final long serialVersionUID = 1244019868841067321L;

    public LatLonRestriction(double minLat, double maxLat, double minLon, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }

    @Override
    public PositionRestriction getImplementation() {
        return new LatLonRestrictionImpl(minLat, maxLat, minLon, maxLon);
    }

}
