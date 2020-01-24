package llnl.gnem.apps.detection.dataAccess;

import llnl.gnem.apps.detection.dataAccess.database.SequenceNames;
import llnl.gnem.apps.detection.dataAccess.database.TableNames;
import llnl.gnem.apps.detection.dataAccess.database.oracle.OracleFactoryHelper;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectionDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.EventDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.OriginDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.PickDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.SeismogramDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.StationDAO;
import llnl.gnem.core.dataAccess.DAOFactory;

import llnl.gnem.core.database.Connections;

/**
 *
 * @author dodge1
 */
public class DetectionDAOFactory {

    private final DAOFactory coreDAOFactory;

    private DetectionDAOFactory() {
        coreDAOFactory = DAOFactory.getInstance();

        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                TableNames.setSchemaFromConnectedUser();
                SequenceNames.setSchemaFromConnectedUser();
                coreDAOFactory.getFilterDAO().setStoredFilterTable(TableNames.getStoredFilterTable());
                coreDAOFactory.getFilterDAO().setSequenceName(SequenceNames.getFilterdSequenceName());
                break;
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    /**
     * This is a convenience method that will perform the connection to the
     * database and ask the user for credentials (if they are required)
     *
     * @return DetectionDAOFactory instance.
     */
    public static DetectionDAOFactory getInstance() {
        return DAOFactoryHolder.INSTANCE;
    }

    private static class DAOFactoryHolder {

        private static final DetectionDAOFactory INSTANCE = new DetectionDAOFactory();
    }

    public synchronized Connections getConnections() {
        return coreDAOFactory.getConnections();
    }

    public synchronized EventDAO getEventDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getEventDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized OriginDAO getOriginDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getOriginDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized SeismogramDAO getSeismogramDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getSeismogramDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized DetectionDAO getDetectionDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getDetectionDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized PickDAO getPickDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getPickDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized StationDAO getStationDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getStationDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized DetectorDAO getDetectorDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getDetectorDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

}
