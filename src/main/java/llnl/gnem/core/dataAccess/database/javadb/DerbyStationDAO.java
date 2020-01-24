/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;
import llnl.gnem.core.dataAccess.dataObjects.StationEpoch;
import llnl.gnem.core.dataAccess.interfaces.StationDAO;

/**
 *
 * @author dodge1
 */
public class DerbyStationDAO implements StationDAO{

    @Override
    public Collection<StationEpoch> getEventWaveformStations(long eventId, double delta) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApplicationStationInfo getStationInfoForWaveform(long waveformId) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
