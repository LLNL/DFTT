/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Map;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.WSDataSource;
import llnl.gnem.core.dataAccess.dataObjects.WSServiceType;

/**
 *
 * @author dodge1
 */
public interface WSDataSourceDAO {

    Map<String, WSDataSource> getDataSources(WSServiceType serviceType) throws DataAccessException;

    Map<WSServiceType, WSDataSource> getDataSources(int sourceId) throws DataAccessException;
}
