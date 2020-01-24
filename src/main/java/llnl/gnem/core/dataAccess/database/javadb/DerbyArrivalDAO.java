/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.interfaces.ArrivalDAO;

/**
 *
 * @author dodge1
 */
public class DerbyArrivalDAO implements ArrivalDAO{

    @Override
    public NominalArrival getNominalArrival(long eventId, long stationId, long streamId, String phase) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
