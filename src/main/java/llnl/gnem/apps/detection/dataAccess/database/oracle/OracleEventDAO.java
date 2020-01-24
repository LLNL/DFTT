package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.database.DbEventDAO;


public class OracleEventDAO extends DbEventDAO {

    private static final OracleEventDAO INSTANCE = new OracleEventDAO();

    static OracleEventDAO getInstance() {
        return INSTANCE;
    }

    private OracleEventDAO() {
    }

}
