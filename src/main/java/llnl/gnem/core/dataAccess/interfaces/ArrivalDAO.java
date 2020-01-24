/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.DataAccessException;

/**
 *
 * @author dodge1
 */
public interface ArrivalDAO {
    NominalArrival getNominalArrival(long eventId, long stationId, long streamId, String phase) throws DataAccessException;
}
