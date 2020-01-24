package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.database.DbSeismogramDAO;


public class DerbySeismogramDAO extends DbSeismogramDAO {

    private static final DerbySeismogramDAO INSTANCE = new DerbySeismogramDAO();

    static DerbySeismogramDAO getInstance() {
        return INSTANCE;
    }

    private DerbySeismogramDAO() {
    }

}
