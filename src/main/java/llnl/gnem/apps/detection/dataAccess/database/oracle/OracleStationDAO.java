/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.database.DbStationDAO;

/**
 *
 * @author dodge1
 */
public class OracleStationDAO extends DbStationDAO{
    
    private OracleStationDAO() {
    }
    
    public static OracleStationDAO getInstance() {
        return OracleStationDAOHolder.INSTANCE;
    }
    
    private static class OracleStationDAOHolder {

        private static final OracleStationDAO INSTANCE = new OracleStationDAO();
    }
}
