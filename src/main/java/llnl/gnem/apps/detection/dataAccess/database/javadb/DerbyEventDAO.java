package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.database.DbEventDAO;


public class DerbyEventDAO extends DbEventDAO {

    private static final DerbyEventDAO INSTANCE = new DerbyEventDAO();

    static DerbyEventDAO getInstance() {
        return INSTANCE;
    }

    private DerbyEventDAO() {
    }

}
