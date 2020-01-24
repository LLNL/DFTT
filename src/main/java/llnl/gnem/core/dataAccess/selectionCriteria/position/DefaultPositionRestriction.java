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
public class DefaultPositionRestriction implements PositionRestrictionSpec, Serializable{

    @Override
    public PositionRestriction getImplementation() {
        return new DefaultPositionRestrictionImpl();
    }
    private static final long serialVersionUID = -140976586016081652L;
    
}
