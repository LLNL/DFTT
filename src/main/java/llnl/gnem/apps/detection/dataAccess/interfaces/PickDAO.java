/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public interface PickDAO {
    Collection<PhasePick> getPicksForDetection(int detectionid) throws DataAccessException;
    
    Collection<PhasePick> getDetectionPhasePicks(int runid, int detectorid) throws DataAccessException;

    void saveDetectionPhasePicks(ArrayList<PhasePick> picks, ArrayList<Integer> picksToRemove) throws DataAccessException;

    Collection<PhasePick> getPicks(int configid, Epoch epoch) throws DataAccessException;
}
