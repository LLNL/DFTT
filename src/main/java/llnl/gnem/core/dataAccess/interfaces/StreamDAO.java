/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.StreamEpochInfo;

/**
 *
 * @author dodge1
 */
public interface StreamDAO {

    StreamEpochInfo getBestStreamEpoch(int streamId, double time) throws DataAccessException;
}
