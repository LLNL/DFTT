/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.database.DbPickDAO;

/**
 *
 * @author dodge1
 */
public class OraclePickDAO extends DbPickDAO{
    
    private OraclePickDAO() {
    }
    
    public static OraclePickDAO getInstance() {
        return OraclePickDAOHolder.INSTANCE;
    }
    
    private static class OraclePickDAOHolder {

        private static final OraclePickDAO INSTANCE = new OraclePickDAO();
    }
}
