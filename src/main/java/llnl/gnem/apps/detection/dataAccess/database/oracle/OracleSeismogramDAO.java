package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.database.DbSeismogramDAO;


public class OracleSeismogramDAO extends DbSeismogramDAO {

    private static final OracleSeismogramDAO INSTANCE = new OracleSeismogramDAO();

    static OracleSeismogramDAO getInstance() {
        return INSTANCE;
    }

    private OracleSeismogramDAO() {
    }

}
