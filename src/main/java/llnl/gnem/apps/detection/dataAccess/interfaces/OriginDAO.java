/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public interface OriginDAO {

    OriginInfo getOriginInfo(int evid) throws DataAccessException;

    Collection<OriginInfo> getOriginsInTimeWindow(Epoch epoch) throws DataAccessException;
}
