/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.core.dataAccess.DataAccessException;

/**
 *
 * @author dodge1
 */
public interface StationDAO {

    Collection<StationInfo> getGroupStations(int groupid) throws DataAccessException;

    int getGroupForDetectionid(long detectionid) throws DataAccessException;
}
