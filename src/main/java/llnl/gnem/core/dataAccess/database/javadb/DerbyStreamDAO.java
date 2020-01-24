/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.StreamEpochInfo;
import llnl.gnem.core.dataAccess.interfaces.StreamDAO;

/**
 *
 * @author dodge1
 */
public class DerbyStreamDAO implements StreamDAO{

    @Override
    public StreamEpochInfo getBestStreamEpoch(int streamId, double time) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
