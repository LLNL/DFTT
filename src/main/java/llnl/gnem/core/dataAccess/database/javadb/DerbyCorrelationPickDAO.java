/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.util.Collection;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.clustering.GroupData;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.interfaces.CorrelationPickDAO;

/**
 *
 * @author dodge1
 */
public class DerbyCorrelationPickDAO implements CorrelationPickDAO{

    @Override
    public void writeCorrelationPickSet(Collection<CorrelationComponent> componentGroup, long streamId, double groupThreshold, double preRefSeconds, double postRefSeconds, Integer filterid, double analystShift, String phase, double uncertainty, GroupData gd) 
            throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
