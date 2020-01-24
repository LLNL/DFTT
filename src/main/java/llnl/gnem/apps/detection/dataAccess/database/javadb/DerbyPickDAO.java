/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.database.DbPickDAO;

/**
 *
 * @author dodge1
 */
public class DerbyPickDAO extends DbPickDAO{
    
    private DerbyPickDAO() {
    }
    
    public static DerbyPickDAO getInstance() {
        return DerbyPickDAOHolder.INSTANCE;
    }
    
    private static class DerbyPickDAOHolder {

        private static final DerbyPickDAO INSTANCE = new DerbyPickDAO();
    }
}
