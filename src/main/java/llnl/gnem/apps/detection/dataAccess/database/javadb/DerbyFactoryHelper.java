package llnl.gnem.apps.detection.dataAccess.database.javadb;

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
public class DerbyFactoryHelper {


    public static EventDAO getEventDAO()
    {
        return DerbyEventDAO.getInstance();
    }

    public static OriginDAO getOriginDAO() {
        return DerbyOriginDAO.getInstance();
    }

    public static SeismogramDAO getSeismogramDAO() {
        return DerbySeismogramDAO.getInstance();
    }

    public static DetectionDAO getDetectionDAO() {
        return DerbyDetectionDAO.getInstance();
    }

    public static PickDAO getPickDAO() {
        return DerbyPickDAO.getInstance();
    }

    public static StationDAO getStationDAO() {
        return DerbyStationDAO.getInstance();
    }

    public static DetectorDAO getDetectorDAO() {
        return DerbyDetectorDAO.getInstance();
    }
    
}
