package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.database.DbDetectorDAO;


public class DerbyDetectorDAO extends DbDetectorDAO {

    private static final DerbyDetectorDAO INSTANCE = new DerbyDetectorDAO();

    static DerbyDetectorDAO getInstance() {
        return INSTANCE;
    }

    private DerbyDetectorDAO() {
    }

}
