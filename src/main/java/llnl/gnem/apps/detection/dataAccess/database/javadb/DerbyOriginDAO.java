package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.database.DbOriginDAO;


public class DerbyOriginDAO extends DbOriginDAO {

    private static final DerbyOriginDAO INSTANCE = new DerbyOriginDAO();

    static DerbyOriginDAO getInstance() {
        return INSTANCE;
    }

    private DerbyOriginDAO() {
    }

}
