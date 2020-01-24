/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;
import llnl.gnem.core.dataAccess.dataObjects.StationEpoch;

/**
 *
 * @author dodge1
 */
public interface StationDAO {

    Collection<StationEpoch> getEventWaveformStations(long eventId, double delta) throws DataAccessException;

    ApplicationStationInfo getStationInfoForWaveform(long waveformId) throws DataAccessException;
}
