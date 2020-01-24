package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.apps.detection.dataAccess.interfaces.DetectionDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.EventDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.OriginDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.PickDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.SeismogramDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.StationDAO;


/**
 *
 * @author dodge1
 */
public class OracleFactoryHelper {
    
    public static EventDAO getEventDAO()
    {
        return OracleEventDAO.getInstance();
    }

    public static OriginDAO getOriginDAO() {
        return OracleOriginDAO.getInstance();
    }

    public static SeismogramDAO getSeismogramDAO() {
        return OracleSeismogramDAO.getInstance();
    }

    public static DetectionDAO getDetectionDAO() {
        return OracleDetectionDAO.getInstance();
    }

    public static PickDAO getPickDAO() {
        return OraclePickDAO.getInstance();
    }

    public static StationDAO getStationDAO() {
        return OracleStationDAO.getInstance();
    }

    public static DetectorDAO getDetectorDAO() {
        return OracleDetectorDAO.getInstance();
    }
    
}
