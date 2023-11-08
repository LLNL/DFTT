/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.database;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.DetectorFactory;
import llnl.gnem.apps.detection.FrameworkPreprocessorParams;

import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenRange;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.SlownessRangeSpecification;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import llnl.gnem.apps.detection.core.framework.StreamProcessor;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayPowerDetector;
import llnl.gnem.apps.detection.core.framework.detectors.array.FKScreenConfiguration;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.apps.detection.dataAccess.interfaces.StreamProcessorDAO;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.ArrayInfoModel;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamInfo;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public abstract class DbStreamProcessorDAO implements StreamProcessorDAO {

    private ConcreteStreamProcessor cachedProcessor = null;
    private int cachedStreamid = -1;
    private double cachedMaxTemplateLengthSeconds = -1;
    private boolean cachedTriggerOnlyOnCorrelators = false;

    @Override
    public Collection<StreamProcessor> constructProcessors(String configName,
            double sampleRate,
            Map<String, Boolean> streamTriggeringMap) throws DataAccessException {

        try {
            return constructStreamProcessors(configName, sampleRate, streamTriggeringMap);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public ConcreteStreamProcessor createStreamProcessor(double sampleRate,
            int streamid,
            String streamName,
            boolean triggerOnlyOnCorrelators) throws DataAccessException {
        try {
            return createStreamProcessorP(sampleRate,
                    streamid,
                    streamName,
                    triggerOnlyOnCorrelators);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public ConcreteStreamProcessor createStreamProcessor(FrameworkPreprocessorParams params,
            int streamid,
            String streamName,
            double maxTemplateLengthSeconds,
            boolean triggerOnlyOnCorrelators) throws DataAccessException {
        try {
            return createStreamProcessorP(params,
                    streamid,
                    streamName,
                    maxTemplateLengthSeconds,
                    triggerOnlyOnCorrelators);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public ConcreteStreamProcessor createStreamProcessor(int streamid, double maxTemplateLengthSeconds, boolean triggerOnlyOnCorrelators) throws DataAccessException {
        try {
            return createStreamProcessorP(streamid, maxTemplateLengthSeconds, triggerOnlyOnCorrelators);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Collection<StreamProcessor> constructStreamProcessors(String configName,
            double sampleRate,
            Map<String, Boolean> streamTriggeringMap) throws Exception {
        Collection<StreamProcessor> result = new ArrayList<>();
        String sql = String.format("select streamid, stream_name\n"
                + "  from %s a, %s b\n"
                + " where config_name = ? and a.configid = b.configid",
                TableNames.getConfigurationTable(),
                TableNames.getStreamTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, configName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int streamid = rs.getInt(1);
                        String streamName = rs.getString(2);
                        Boolean triggerOnlyOnCorrelators = streamTriggeringMap.get(streamName);
                        if (triggerOnlyOnCorrelators == null) {
                            triggerOnlyOnCorrelators = false;
                        }
                        StreamProcessor processor = createStreamProcessor(sampleRate, streamid, streamName, triggerOnlyOnCorrelators);
                        result.add(processor);

                    }
                }
            }

        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }

        return result;
    }

    private ConcreteStreamProcessor createStreamProcessorP(double sampleRate,
            int streamid,
            String streamName,
            boolean triggerOnlyOnCorrelators) throws Exception {

        if (StreamsConfig.getInstance().isArrayStream(streamName)) {
            return getArrayStreamProcessor(streamid, streamName, sampleRate, triggerOnlyOnCorrelators);
        } else {
            return getNonArrayStreamProcessor(streamid, streamName, sampleRate, triggerOnlyOnCorrelators);
        }
    }

    private ConcreteStreamProcessor getArrayStreamProcessor(int streamid, String streamName, double sampleRate, boolean triggerOnlyOnCorrelators) throws Exception {
        boolean forceFKComputations = true;

        ArrayList<StreamKey> streamKeys = DetectionDAOFactory.getInstance().getStreamDAO().getStreamKeysForStream(streamid);
        PreprocessorParams params = new FrameworkPreprocessorParams(streamName, sampleRate);
        double maxTemplateLengthSeconds = ProcessingPrescription.getInstance().getMaxTemplateLength();
        double decRate = params.getPreprocessorParams().getDecimatedSampleRate();
        int maxTemplateLength = (int) (maxTemplateLengthSeconds * decRate + 1);
        ConcreteStreamProcessor processor = new ConcreteStreamProcessor(params, streamid, streamKeys, streamName, sampleRate, maxTemplateLength, triggerOnlyOnCorrelators);
        Collection<Detector> detectors = DetectorFactory.getDetectorsForStream(processor);
        FKScreenParams screenParams = StreamsConfig.getInstance().getFKScreenParams(streamName);
        Collection<FKScreenConfiguration> fkConfigs = new ArrayList<>();
        for (Detector detector : detectors) {
            if (detector instanceof ArrayPowerDetector) {
                ArrayPowerDetector apd = (ArrayPowerDetector) detector;

                FKScreenConfiguration fkConfig = apd.createFKScreen(screenParams);
                if (fkConfig != null) {
                    fkConfigs.add(fkConfig);
                }
            }
        }
        double decimatedRate = params.getPreprocessorParams().getDecimatedSampleRate();
        FKScreenConfiguration compositeScreen = FKScreenConfiguration.buildCompositeConfiguration(fkConfigs, decimatedRate);
        if (compositeScreen != null) {
            processor.addFKScreen(compositeScreen);

        } else if (forceFKComputations) {
            int jdate = ProcessingPrescription.getInstance().getMinJdateToProcess();
            ArrayConfiguration ac = ArrayInfoModel.getInstance().getGeometry(streamKeys);
            if (ac != null) {
                SlownessSpecification slowness = new SlownessSpecification(10000.0, 0.0); // See how this will be used
                FKScreenRange range = new FKScreenRange(360.0, 10000.0);
                SlownessRangeSpecification srs = new SlownessRangeSpecification(slowness, range);
                Map<StreamKey, ArrayElementInfo> ourElements = new HashMap<>();
                for (StreamKey key : streamKeys) {
                    ArrayElementInfo aei = ac.getElement(key, jdate);
                    if(aei != null){
                        ourElements.put(key,aei);
                    }
                }
                FKScreenConfiguration config = new FKScreenConfiguration(screenParams, srs, ourElements);
                processor.addFKScreen(config);
            }
        }
        processor.addDetectors(detectors);
        return processor;

    }

    private ConcreteStreamProcessor getNonArrayStreamProcessor(int streamid, String streamName, double sampleRate, boolean triggerOnlyOnCorrelators) throws Exception {
        ArrayList<StreamKey> streamKeys = DetectionDAOFactory.getInstance().getStreamDAO().getStreamKeysForStream(streamid);
        PreprocessorParams params = new FrameworkPreprocessorParams(streamName, sampleRate);
        double maxTemplateLengthSeconds = ProcessingPrescription.getInstance().getMaxTemplateLength();
        double decRate = params.getPreprocessorParams().getDecimatedSampleRate();
        int maxTemplateLength = (int) (maxTemplateLengthSeconds * decRate + 1);
        ConcreteStreamProcessor processor = new ConcreteStreamProcessor(params, streamid, streamKeys, streamName, sampleRate, maxTemplateLength, triggerOnlyOnCorrelators);
        Collection<Detector> detectors = DetectorFactory.getDetectorsForStream(processor);
        processor.addDetectors(detectors);
        return processor;

    }

    private ConcreteStreamProcessor createStreamProcessorP(FrameworkPreprocessorParams params,
            int streamid,
            String streamName,
            double maxTemplateLengthSeconds,
            boolean triggerOnlyOnCorrelators) throws Exception {
        ArrayList<StreamKey> streamKeys = DetectionDAOFactory.getInstance().getStreamDAO().getStreamKeysForStream(streamid);

        double decRate = params.getPreprocessorParams().getDecimatedSampleRate();
        int maxTemplateLength = (int) (maxTemplateLengthSeconds * decRate + 1);
        ConcreteStreamProcessor processor = new ConcreteStreamProcessor(params, streamid, streamKeys,
                streamName, params.getSampleRate(), maxTemplateLength, triggerOnlyOnCorrelators);

        return processor;

    }

    private ConcreteStreamProcessor createStreamProcessorP(int streamid, double maxTemplateLengthSeconds, boolean triggerOnlyOnCorrelators) throws Exception {
        if (streamid == cachedStreamid && maxTemplateLengthSeconds == cachedMaxTemplateLengthSeconds && triggerOnlyOnCorrelators == cachedTriggerOnlyOnCorrelators && cachedProcessor != null) {
            return cachedProcessor;
        }
        String sql = String.format("select stream_name, stream_dir, stream_config_file_name, PREPROCESSOR_PARAMS from %s where streamid = ? ",
                TableNames.getStreamTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, streamid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String streamName = rs.getString(1);
                        String dir = DriveMapper.getInstance().maybeMapPath(rs.getString(2));
                        String dfile = rs.getString(3);
                        File aFile = new File(dir, dfile);
                        Map<String, StreamInfo> streams = new HashMap<>();

                        StreamInfo info = new StreamInfo(aFile.getAbsolutePath());
                        streams.put(info.getStreamName(), info);

                        StreamsConfig.getInstance().populateMap(streams);
                        Blob blob = rs.getBlob(4);
                        if (!rs.wasNull()) {
                            try (InputStream is = blob.getBinaryStream()) {
                                try (ObjectInputStream oip = new ObjectInputStream(is)) {
                                    Object obj = oip.readObject();
                                    FrameworkPreprocessorParams params = (FrameworkPreprocessorParams) obj;
                                    cachedProcessor = createStreamProcessor(params, streamid, streamName, maxTemplateLengthSeconds, triggerOnlyOnCorrelators);
                                    cachedStreamid = streamid;
                                    cachedMaxTemplateLengthSeconds = maxTemplateLengthSeconds;
                                    cachedTriggerOnlyOnCorrelators = triggerOnlyOnCorrelators;
                                    return cachedProcessor;

                                }
                            }
                        }
                    }
                }
                return null;
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

}
