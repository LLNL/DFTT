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
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import llnl.gnem.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo.SourceType;
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
import llnl.gnem.core.dataAccess.streaming.FDSNContinuousWaveformDAO;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.database.oracle.OracleContinuousWaveformDAO;
import llnl.gnem.core.dataAccess.interfaces.ContinuousWaveformDAO;


/**
 *
 * @author dodge1
 */
public class OracleFactoryHelper {

    public static EventDAO getEventDAO() {
        return OracleEventDAO.getInstance();
    }

    public static OriginDAO getOriginDAO() {
        return OracleOriginDAO.getInstance();
    }

    public static ContinuousWaveformDAO getContinuousWaveformDAO() {
        SeismogramSourceInfo info = DAOFactory.getInstance().getSeismogramSourceInfo();
        if (info.getSourceType() == SourceType.FDSN) {
            FDSNContinuousWaveformDAO dao = FDSNContinuousWaveformDAO.getInstance();
             return dao;
        } else {
            return new OracleContinuousWaveformDAO();
        }
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

    public static TriggerDAO getTriggerDAO() {
        return OracleTriggerDAO.getInstance();
    }

    public static StreamDAO getStreamDAO() {
        return OracleStreamDAO.getInstance();
    }

    public static SubspaceDetectorDAO getSubspaceDetectorDAO() {
        return OracleSubspaceDetectorDAO.getInstance();
    }

    public static SubspaceTemplateDAO getSubspaceTemplateDAO() {
        return OracleSubspaceTemplateDAO.getInstance();
    }

    public static ArrayCorrelationDetectorDAO getArrayCorrelationDetectorDAO() {
        return OracleArrayCorrelationDetectorDAO.getInstance();
    }

    public static ArrayDetectorDAO getArrayDetectorDAO() {
        return OracleArrayDetectorDAO.getInstance();
    }

    public static BulletinDetectorDAO getBulletinDetectorDAO() {
        return OracleBulletinDetectorDAO.getInstance();
    }

    public static StaLtaDetectorDAO getStaLtaDetectorDAO() {
        return OracleStaLtaDetectorDAO.getInstance();
    }

    public static PredictedPhasePickDAO getPredictedPhasePickDAO() {
        return OraclePredictedPhasePickDAO.getInstance();
    }

    public static StreamProcessorDAO getStreamProcessorDAO() {
        return OracleStreamProcessorDAO.getInstance();
    }

    public static FrameworkRunDAO getFrameworkRunDAO() {
        return OracleFrameworkRunDAO.getInstance();
    }

    public static FeatureDAO getFeatureDAO() {
        return OracleFeatureDAO.getInstance();
    }

    public static ArrayConfigurationDAO getArrayConfigurationDAO() {
        return OracleArrayConfigurationDAO.getInstance();
    }


    public static ConfigurationDAO getConfigurationDAO() {
        return OracleConfigurationDAO.getInstance();
    }
}
