package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.database.DbOriginDAO;


public class OracleOriginDAO extends DbOriginDAO {

    private static final OracleOriginDAO INSTANCE = new OracleOriginDAO();

    static OracleOriginDAO getInstance() {
        return INSTANCE;
    }

    private OracleOriginDAO() {
    }

}
