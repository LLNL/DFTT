/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.database.DbStationDAO;

/**
 *
 * @author dodge1
 */
public class DerbyStationDAO extends DbStationDAO{
    
    private DerbyStationDAO() {
    }
    
    public static DerbyStationDAO getInstance() {
        return DerbyStationDAOHolder.INSTANCE;
    }
    
    private static class DerbyStationDAOHolder {

        private static final DerbyStationDAO INSTANCE = new DerbyStationDAO();
    }
}
