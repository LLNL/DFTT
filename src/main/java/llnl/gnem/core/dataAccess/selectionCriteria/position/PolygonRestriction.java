/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria.position;

import java.io.Serializable;
import llnl.gnem.core.polygon.PolygonSet;
import llnl.gnem.core.polygon.PolygonSetType;

/**
 *
 * @author dodge1
 */
public class PolygonRestriction implements PositionRestrictionSpec, Serializable {

    private final PolygonSet polySet;
    private static final long serialVersionUID = 5628151176571378134L;

    public PolygonRestriction(PolygonSet polySet) {
        this.polySet = polySet;
    }

    @Override
    public PositionRestriction getImplementation() {
        return new PolygonRestrictionImpl(polySet);
    }
    
    public PolygonSetType getRestrictionType()
    {
        return polySet.getType();
    }
    
    public String getPolygonSetName()
    {
        return polySet.getName();
    }
    
    public PolygonSet getSet()
    {
        return polySet;
    }

}
