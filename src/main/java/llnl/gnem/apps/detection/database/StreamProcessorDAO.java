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
package llnl.gnem.apps.detection.database;

import llnl.gnem.apps.detection.DetectorFactory;
import llnl.gnem.apps.detection.FrameworkPreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.StreamProcessor;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayPowerDetector;
import llnl.gnem.apps.detection.core.framework.detectors.array.FKScreenConfiguration;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.StreamKey;

/**
 * Created by dodge1 Date: Sep 27, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class StreamProcessorDAO {

    private StreamProcessorDAO() {
    }

    public static StreamProcessorDAO getInstance() {
        return StreamProcessorDAOHolder.INSTANCE;
    }

    private static class StreamProcessorDAOHolder {

        private static final StreamProcessorDAO INSTANCE = new StreamProcessorDAO();
    }

    public Collection<StreamProcessor> constructProcessors(String configName,
            double sampleRate,
            Map<String, Boolean> streamTriggeringMap) throws Exception {

        return constructStreamProcessors(configName, sampleRate, streamTriggeringMap);
    }

    private Collection<StreamProcessor> constructStreamProcessors(String configName,
            double sampleRate,
            Map<String, Boolean> streamTriggeringMap) throws Exception {
        Collection<StreamProcessor> result = new ArrayList<>();
        String sql = "select streamid, stream_name\n"
                + "  from configuration a, stream b\n"
                + " where config_name = ? and a.configid = b.configid";
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
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
                        StreamProcessor processor = createStreamProcessor(sampleRate, streamid, streamName, triggerOnlyOnCorrelators, conn);
                        result.add(processor);

                    }
                }
            }

        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }

        return result;
    }

    public ConcreteStreamProcessor createStreamProcessor(double sampleRate,
            int streamid,
            String streamName,
            boolean triggerOnlyOnCorrelators,
            Connection conn) throws Exception {
        if (StreamsConfig.getInstance().isArrayStream(streamName)) {
            return getArrayStreamProcessor(conn, streamid, streamName, sampleRate, triggerOnlyOnCorrelators);
        } else {
            return getNonArrayStreamProcessor(conn, streamid, streamName, sampleRate, triggerOnlyOnCorrelators);
        }
    }

    private ConcreteStreamProcessor getArrayStreamProcessor(Connection conn, int streamid, String streamName, double sampleRate, boolean triggerOnlyOnCorrelators) throws Exception {
        ArrayList<StreamKey> streamKeys = getStreamKeys(streamid, conn);
        PreprocessorParams params = new FrameworkPreprocessorParams(streamName, sampleRate);
        double maxTemplateLengthSeconds = ProcessingPrescription.getInstance().getMaxTemplateLength();
        double decRate = params.getPreprocessorParams().getDecimatedSampleRate();
        int maxTemplateLength = (int) (maxTemplateLengthSeconds * decRate + 1);
        ConcreteStreamProcessor processor = new ConcreteStreamProcessor(params, streamid, streamKeys, streamName, sampleRate, maxTemplateLength, triggerOnlyOnCorrelators);
        Collection<Detector> detectors = DetectorFactory.getDetectorsForStream(processor);
        Collection<FKScreenConfiguration> fkConfigs = new ArrayList<>();
        for (Detector detector : detectors) {
            if (detector instanceof ArrayPowerDetector) {
                ArrayPowerDetector apd = (ArrayPowerDetector) detector;
                FKScreenParams screenParams = StreamsConfig.getInstance().getFKScreenParams(streamName);
                FKScreenConfiguration fkConfig = apd.createFKScreen(screenParams);
                if (fkConfig != null) {
                    fkConfigs.add(fkConfig);
                }
            }
        }
        double decimatedRate = params.getPreprocessorParams().getDecimatedSampleRate();
        FKScreenConfiguration compositeScreen = FKScreenConfiguration.buildCompositeConfiguration(fkConfigs, decimatedRate);
        processor.addDetectors(detectors);
        processor.addFKScreen(compositeScreen);
        return processor;

    }

    private ConcreteStreamProcessor getNonArrayStreamProcessor(Connection conn, int streamid, String streamName, double sampleRate, boolean triggerOnlyOnCorrelators) throws Exception {
        ArrayList<StreamKey> streamKeys = getStreamKeys(streamid, conn);
        PreprocessorParams params = new FrameworkPreprocessorParams(streamName, sampleRate);
        double maxTemplateLengthSeconds = ProcessingPrescription.getInstance().getMaxTemplateLength();
        double decRate = params.getPreprocessorParams().getDecimatedSampleRate();
        int maxTemplateLength = (int) (maxTemplateLengthSeconds * decRate + 1);
        ConcreteStreamProcessor processor = new ConcreteStreamProcessor(params, streamid, streamKeys, streamName, sampleRate, maxTemplateLength, triggerOnlyOnCorrelators);
        Collection<Detector> detectors = DetectorFactory.getDetectorsForStream(processor);
        processor.addDetectors(detectors);
        return processor;

    }

    private static ArrayList<StreamKey> getStreamKeys(int streamid, Connection conn) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        String sql = String.format("select sta, chan\n"
                + "  from stream_channel \n"
                + " where streamid = ?\n"
                + "   order by sta, chan");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, streamid);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int idx = 1;
                    String sta = rs.getString(idx++);
                    String chan = rs.getString(idx++);
                    //If more than one site epoch is returned by query, will only retain the latest.
                    StreamKey sck = new StreamKey(sta, chan);
                    result.add(sck);
                }
                if (result.isEmpty()) {
                    throw new IllegalStateException("No channels were retrieved for stream " + streamid);
                }
                return result;
            }
        }
    }

    public static ConcreteStreamProcessor createStreamProcessor(FrameworkPreprocessorParams params,
            int streamid,
            String streamName,
            double maxTemplateLengthSeconds,
            boolean triggerOnlyOnCorrelators,
            Connection conn) throws Exception {
        ArrayList<StreamKey> streamKeys = getStreamKeys(streamid, conn);

        double decRate = params.getPreprocessorParams().getDecimatedSampleRate();
        int maxTemplateLength = (int) (maxTemplateLengthSeconds * decRate + 1);
        ConcreteStreamProcessor processor = new ConcreteStreamProcessor(params, streamid, streamKeys,
                streamName, params.getSampleRate(), maxTemplateLength, triggerOnlyOnCorrelators);

        return processor;

    }

}
