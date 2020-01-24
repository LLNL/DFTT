/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.util.Map;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.WSDataSource;
import llnl.gnem.core.dataAccess.dataObjects.WSServiceType;
import llnl.gnem.core.dataAccess.interfaces.WSDataSourceDAO;

/**
 *
 * @author dodge1
 */
public class DerbyWSDataSourceDAO implements WSDataSourceDAO{

    @Override
    public Map<String, WSDataSource> getDataSources(WSServiceType serviceType) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<WSServiceType, WSDataSource> getDataSources(int sourceId) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
