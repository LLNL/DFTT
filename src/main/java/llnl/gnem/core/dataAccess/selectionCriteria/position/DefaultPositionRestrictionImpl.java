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
public class DefaultPositionRestrictionImpl implements PositionRestriction {

    @Override
    public boolean isInside(double lat, double lon) {
        return true;
    }

    @Override
    public LatLonBox getSqlLimits() {
        return new LatLonBox(-90, 90, -180, 180);
    }

    @Override
    public String getSQLClause() {
        return " ";
    }

}
