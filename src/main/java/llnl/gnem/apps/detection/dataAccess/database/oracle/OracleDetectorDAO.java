/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.database.DbDetectorDAO;

/**
 *
 * @author dodge1
 */
public class OracleDetectorDAO extends DbDetectorDAO {

    private OracleDetectorDAO() {
    }

    public static OracleDetectorDAO getInstance() {
        return OracleDetectorDAOHolder.INSTANCE;
    }

    private static class OracleDetectorDAOHolder {

        private static final OracleDetectorDAO INSTANCE = new OracleDetectorDAO();
    }
}
