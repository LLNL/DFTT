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
public interface PositionRestrictionSpec extends Serializable{
    PositionRestriction getImplementation();
}
