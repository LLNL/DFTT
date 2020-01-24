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
public interface PositionRestriction {

    boolean isInside(double lat, double lon);

    LatLonBox getSqlLimits();

    String getSQLClause();
}
