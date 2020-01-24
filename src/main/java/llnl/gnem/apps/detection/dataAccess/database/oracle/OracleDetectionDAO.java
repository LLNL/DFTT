/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.database.DbDetectionDAO;

/**
 *
 * @author dodge1
 */
public class OracleDetectionDAO extends DbDetectionDAO {
    
    private OracleDetectionDAO() {
    }
    
    public static OracleDetectionDAO getInstance() {
        return OracleDetectionDAOHolder.INSTANCE;
    }
    
    private static class OracleDetectionDAOHolder {

        private static final OracleDetectionDAO INSTANCE = new OracleDetectionDAO();
    }
}
