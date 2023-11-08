/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.dataAccess;

import llnl.gnem.apps.detection.dataAccess.database.SequenceNames;
import llnl.gnem.apps.detection.dataAccess.database.TableNames;
import llnl.gnem.apps.detection.dataAccess.database.oracle.OracleFactoryHelper;
import llnl.gnem.apps.detection.dataAccess.interfaces.ArrayConfigurationDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.ArrayCorrelationDetectorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.ArrayDetectorDAO;

import llnl.gnem.apps.detection.dataAccess.interfaces.BulletinDetectorDAO;

import llnl.gnem.apps.detection.dataAccess.interfaces.ConfigurationDAO;

import llnl.gnem.apps.detection.dataAccess.interfaces.DetectionDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.EventDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.FeatureDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.FrameworkRunDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.OriginDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.PickDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.PredictedPhasePickDAO;

import llnl.gnem.apps.detection.dataAccess.interfaces.StaLtaDetectorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.StationDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.StreamDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.StreamProcessorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.SubspaceDetectorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.SubspaceTemplateDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.TriggerDAO;
import llnl.gnem.dftt.core.dataAccess.DAOFactory;

import llnl.gnem.dftt.core.database.Connections;

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
//                TableNames.setSchemaFromConnectedUser();
//                SequenceNames.setSchemaFromConnectedUser();
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

    public synchronized TriggerDAO getTriggerDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getTriggerDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized ConfigurationDAO getConfigurationDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getConfigurationDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized StreamDAO getStreamDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getStreamDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized SubspaceDetectorDAO getSubspaceDetectorDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getSubspaceDetectorDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized SubspaceTemplateDAO getSubspaceTemplateDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getSubspaceTemplateDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized ArrayCorrelationDetectorDAO getArrayCorrelationDetectorDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getArrayCorrelationDetectorDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized ArrayDetectorDAO getArrayDetectorDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getArrayDetectorDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized BulletinDetectorDAO getBulletinDetectorDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getBulletinDetectorDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized StaLtaDetectorDAO getStaLtaDetectorDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getStaLtaDetectorDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized PredictedPhasePickDAO getPredictedPhasePickDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getPredictedPhasePickDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized StreamProcessorDAO getStreamProcessorDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getStreamProcessorDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    public synchronized FrameworkRunDAO getFrameworkRunDAO() {
        switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getFrameworkRunDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    
    public synchronized FeatureDAO getFeatureDAO(){
       switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getFeatureDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }
    
    public synchronized ArrayConfigurationDAO getArrayConfigurationDAO(){
       switch (DAOFactory.getDataSource()) {
            case ORACLE:
                return OracleFactoryHelper.getArrayConfigurationDAO();
            case DERBY:
            default:
                throw new IllegalStateException("DATA_SOURCE_NOT_SET_IN_FACTORY");
        }
    }

    

}
