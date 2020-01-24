/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.EventInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public interface EventDAO {

    Collection<Integer> getEventList() throws DataAccessException;

    Collection<StationInfo> getEventStationInfo(int evid) throws DataAccessException;

    void saveEventStatus(int evid, String status) throws DataAccessException;

    void defineNewEvent(double minTime, double maxTime) throws DataAccessException;

    Collection<EventInfo> getEventsInTimeWindow(Epoch epoch) throws DataAccessException;
}
