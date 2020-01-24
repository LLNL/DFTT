package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.database.DbDetectionDAO;


public class DerbyDetectionDAO extends DbDetectionDAO {

    private static final DerbyDetectionDAO INSTANCE = new DerbyDetectionDAO();

    static DerbyDetectionDAO getInstance() {
        return INSTANCE;
    }

    private DerbyDetectionDAO() {
    }

}
