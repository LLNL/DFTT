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

import Jampack.JampackException;
import com.oregondsp.util.TimeStamp;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.FileBasedDetectorFactory;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.TemplateNormalization;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.Projection;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;

import llnl.gnem.apps.detection.util.RunInfo;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;

import llnl.gnem.apps.detection.core.dataObjects.SubspaceParameters;
import llnl.gnem.apps.detection.util.SubspaceThreshold;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.statistics.HistogramData;
import llnl.gnem.core.util.PairT;

/**
 *
 * @author dodge1
 */
public class SubspaceDetectorDAO {

    private static final double DUPLICATE_TEMPLATE_THRESHOLD = 0.95;
    private static final int SUBSPACE_OFFSET_RANGE = 10;

    private SubspaceDetectorDAO() {
    }

    public static SubspaceDetectorDAO getInstance() {
        return SubspaceDetectorDAOHolder.INSTANCE;
    }

    /**
     * This method creates rank-1 subspace detectors based on a segment of
     * pre-processed data extracted from the stream. This is the method by which
     * power detections are used to create correlators.
     *
     * @param segment
     * @param triggerTime
     * @param templateLeadSeconds
     * @param duration
     * @param streamProcessor
     * @return
     * @throws IllegalStateException
     * @throws Exception
     */
    public SubspaceDetector createDetectorFromStreamSegment(StreamSegment segment,
            TimeStamp triggerTime,
            double templateLeadSeconds,
            double duration,
            ConcreteStreamProcessor streamProcessor) throws IllegalStateException, Exception {
        TimeT segmentStart = segment.getStartTime();
        double sampleInterval = segment.getSampleInterval();
        int startIndex = (int) Math.round((triggerTime.epochAsDouble() - templateLeadSeconds - segmentStart.getEpochTime()) / sampleInterval);
        if (startIndex < 0) {
            throw new IllegalStateException("Requested template data starts before segment start!");
        }
        int endIndex = (int) Math.round((triggerTime.epochAsDouble() + duration - segmentStart.getEpochTime()) / sampleInterval);
        if (endIndex >= segment.size()) {
            throw new IllegalStateException("Requested template data ends after segment end!");
        }

        int N = endIndex - startIndex + 1;
        int nch = segment.getNumChannels();
        float[][] preprocessedDataFromStream = new float[nch][N];
        for (int ich = 0; ich < nch; ich++) {
            float[] streamData = segment.getChannelData(ich);
            for (int i = 0; i < N; i++) {

                preprocessedDataFromStream[ich][i] = streamData[startIndex + i];
            }
        }

        SubspaceSpecification spec = createSubspaceSpecification(streamProcessor, templateLeadSeconds, duration);

        SubspaceTemplate template = new SubspaceTemplate(streamProcessor.getParams(), preprocessedDataFromStream, spec);

        SubspaceDetector detector = createAndSaveSubspaceDetector(streamProcessor, template, spec);
        return detector;
    }

    private static SubspaceSpecification createSubspaceSpecification(ConcreteStreamProcessor streamProcessor, double templateLeadSeconds, double duration) {
        Collection<StreamKey> streamChannels = streamProcessor.getChannels();
        String streamName = streamProcessor.getStreamName();
        double threshold = SubspaceThreshold.getInstance().getNewDetectorThreshold(streamName);
        double blackoutSeconds = StreamsConfig.getInstance().getSubspaceBlackoutPeriod(streamName);
        double energyCapture = StreamsConfig.getInstance().getSubspaceEnergyCaptureThreshold(streamName);
        SubspaceSpecification spec = new SubspaceSpecification(
                (float) threshold,
                (float) blackoutSeconds,
                templateLeadSeconds,
                duration,
                (float) energyCapture,
                streamChannels);
        return spec;
    }

    /**
     * This method creates a multi-rank subspace detector and stores it into the
     * database. This method is called by the Builder program, and passes a
     * collection of unfiltered raw trace collections. The outer collection
     * represents events and the contained collections are template channels per
     * event.
     *
     * @param processor
     * @param eventSegments
     * @param prepickSeconds
     * @param correlationWindowLength
     * @param params
     * @param produceTriggers
     * @param fixToSpecifiedDimension
     * @param requiredDimension
     * @return
     * @throws Exception
     */
    public SubspaceDetector createAndSaveSubspaceDetector(ConcreteStreamProcessor processor, 
            Collection<StreamSegment> eventSegments, 
            double prepickSeconds, 
            double correlationWindowLength, 
            SubspaceParameters params, 
            boolean produceTriggers,
            boolean fixToSpecifiedDimension,
            int requiredDimension) throws Exception {

        Collection<StreamKey> streamChannels = processor.getChannels();

        PreprocessorParams pparams = processor.getParams();
        double decimatedSampleRate = pparams.getPreprocessorParams().getDecimatedSampleRate();
        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();

        ArrayList< float[][]> eventData = DbOps.getInstance().extractFromTemplates(pparams, eventSegments,
                decimatedSampleRate, streamChannels);

        SubspaceSpecification spec = new SubspaceSpecification(
                (float) params.getDetectionThreshold(),
                (float) params.getBlackoutSeconds(),
                prepickSeconds,
                correlationWindowLength,
                (float) params.getEnergyCapture(),
                streamChannels);

        SubspaceTemplate template = new SubspaceTemplate(eventData, spec, pparams,fixToSpecifiedDimension, requiredDimension);
        int detectorid = DetectorDAO.getInstance().getNewDetectorid();
        SubspaceDetector detector = new SubspaceDetector(detectorid, template, decimatedSampleRate, processor.getStreamName(), processor.getFFTSize(), decimatedBlockSize);
        saveSubspaceDetector(spec, detector, processor.getStreamId(), "Created by Builder");
        return detector;
    }

    public void updateTemplateInDB(int detectorid, SubspaceTemplate template) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("update subspace_detector_params set rank = ? where detectorid = ?");
            stmt.setInt(1, template.getdimension());
            stmt.setInt(2, detectorid);
            stmt.execute();
            SubspaceTemplateDAO.getInstance().updateTemplateData(detectorid, template, TemplateNormalization.full_array, conn);
            conn.commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public void writeHistograms(Collection<SubspaceDetector> detectors) throws SQLException {
        int runid = RunInfo.getInstance().getRunid();
        detectors.parallelStream().filter(t -> hasEnoughSamples(t)).forEach(t -> writeOneHist(runid, t));
    }

    private boolean hasEnoughSamples(SubspaceDetector detector) {
        return detector.getNumHistogramSamples() >= 500;
    }

    private void writeOneHist(int runid, SubspaceDetector detector) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            int detectorid = detector.getdetectorid();
            long[] binValues = detector.getHistogramValues();
            writeStats(detectorid, runid, binValues, conn);
        } catch (SQLException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed writing histogram!", ex);
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private void writeStats(int detectorid, int runid, long[] binValues, Connection conn) throws SQLException {
        deleteExisting(detectorid, runid, conn);
        try (PreparedStatement stmt = conn.prepareStatement("insert into det_stat_histogram values(?,?,?,?)")) {
            stmt.setInt(1, detectorid);
            stmt.setInt(2, runid);
            for (int j = 0; j < binValues.length; ++j) {
                stmt.setInt(3, j);
                stmt.setLong(4, binValues[j]);
                stmt.execute();
            }
            conn.commit();
        }

    }
    

    private void deleteExisting(int detectorid, int runid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from det_stat_histogram where detectorid = ? and runid = ?")) {
            stmt.setInt(1, detectorid);
            stmt.setInt(2, runid);
            stmt.execute();
        }
    }

    public HistogramData getHistogramData(int detectorid, int runid) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            return getHistogramData(detectorid, runid, conn);
        } catch (SQLException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed reading histogram!", ex);
            return null;
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private HistogramData getHistogramData(int detectorid, int runid, Connection conn) throws SQLException {
        String sql = "select bin_num, value from det_stat_histogram where detectorid = ? and runid = ? order by bin_num";
        ArrayList<PairT<Float, Float>> tmp = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, detectorid);
            stmt.setInt(2, runid);
            rs = stmt.executeQuery();
            float sum = 0;
            while (rs.next()) {
                float v1 = rs.getFloat(1);
                float v2 = rs.getFloat(2);
                sum += v2;
                tmp.add(new PairT<>(v1, v2));
            }
            if (tmp.isEmpty()) {
                return null;
            }
            float[] bins = new float[tmp.size()];
            float[] values = new float[tmp.size()];
            float binWidth = 1.0f / bins.length;
            for (int j = 0; j < tmp.size(); ++j) {
                float v1 = tmp.get(j).getFirst();
                float v2 = tmp.get(j).getSecond();
                bins[j] = v1 * binWidth + binWidth / 2;
                values[j] = v2 / sum * bins.length;
            }
            return new HistogramData(bins, values);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    
    
    private static class SubspaceDetectorDAOHolder {

        private static final SubspaceDetectorDAO INSTANCE = new SubspaceDetectorDAO();
    }

    /**
     * This method is used by the framework to retrieve all the subspace
     * detectors for a stream. It is called near the beginning of program
     * execution to populate the collection of detectors for the stream.
     *
     * @param processor
     * @return
     * @throws Exception
     */
    public Collection<? extends Detector> retrieveSubspaceDetectors(ConcreteStreamProcessor processor) throws Exception {
        return DetectorDAO.getInstance().retrieveSubspaceDetectors(processor, TemplateNormalization.full_array, DetectorType.SUBSPACE);
    }

    private SubspaceDetector createAndSaveSubspaceDetector(ConcreteStreamProcessor processor,
            SubspaceTemplate template,
            SubspaceSpecification spec) throws Exception {
        int streamid = processor.getStreamId();

        int detectorid = DetectorDAO.getInstance().getNewDetectorid();
        PreprocessorParams pparams = processor.getParams();
        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();
        double decimatedSampleRate = pparams.getPreprocessorParams().getDecimatedSampleRate();
        SubspaceDetector detector = new SubspaceDetector(detectorid, template, decimatedSampleRate, processor.getStreamName(), processor.getFFTSize(), decimatedBlockSize);
        saveSubspaceDetector(spec, detector, streamid, "Created From Stream");
        return detector;
    }

    /**
     * This method is called by the DetectorFactory to create a subspace
     * detector from a user-specified set of SAC files.
     *
     * @param spec
     * @param params
     * @param ssDetectors
     * @param streamid
     * @param streamName
     * @param sampleRate
     * @return
     * @throws IOException
     * @throws JampackException
     * @throws Exception
     */
    public Collection<Detector> buildFromExternalTemplate(DetectorSpecification spec,
            ConcreteStreamProcessor processor,
            //           PreprocessorParams params,
            Collection<Detector> ssDetectors
    //           int streamid,
    //           String streamName,
    //           double sampleRate
    ) throws IOException, JampackException, Exception {
        Collection<Detector> result = new ArrayList<>();
        int detectorid = DetectorDAO.getInstance().getNewDetectorid();
        int streamid = processor.getStreamId();
        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();

        Detector tmpDetector = FileBasedDetectorFactory.createDetectorFromFiles(spec, detectorid, processor, decimatedBlockSize);

        SubspaceDetector sd = (SubspaceDetector) tmpDetector;
        SubspaceTemplate subspaceTemplate = sd.getTemplate();

        ArrayList<float[][]> template = subspaceTemplate.getRepresentation();

        int numChannels = sd.getNumChannels();

        if (isDuplicateTemplate(template, subspaceTemplate.getStaChanList(), ssDetectors)) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("Duplicate template. Skipping."));
        } else {
            String source = spec.toString();
            saveSubspaceDetector(spec, sd, streamid, source);
            result.add(sd);
            ApplicationLogger.getInstance().log(Level.INFO, String.format("Saved new detector: %d.", sd.getdetectorid()));
        }
        return result;
    }

    private boolean isDuplicateTemplate(ArrayList<float[][]> newTemplateRepresentation, ArrayList< StreamKey> chanIDs, Collection<Detector> ssDetectors) throws JampackException {
        for (Detector detector : ssDetectors) {
            if (detector instanceof SubspaceDetector) {
                SubspaceDetector ssd = (SubspaceDetector) detector;
                SubspaceTemplate existingTemplate = ssd.getTemplate();
                if (newTemplateRepresentation.get(0)[0].length == existingTemplate.getTemplateLength()) {
                    Projection projection = new Projection(existingTemplate, newTemplateRepresentation, chanIDs, SUBSPACE_OFFSET_RANGE);
                    double v = projection.getProjectionValue();
                    if (v >= DUPLICATE_TEMPLATE_THRESHOLD) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    private void saveSubspaceDetector(DetectorSpecification spec, SubspaceDetector detector, int streamid, String source) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        DetectorType type = detector.getDetectorType();
        int runid = RunInfo.getInstance().getRunid();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("insert into detector select ?, ?, ?, ?, ?, ?, ?, 'n', sysdate from dual");
            stmt.setInt(1, detector.getdetectorid());
            stmt.setInt(2, streamid);
            stmt.setString(3, type.toString());
            stmt.setDouble(4, spec.getThreshold());
            stmt.setDouble(5, spec.getBlackoutPeriod());
            if (runid > 0) {
                stmt.setInt(6, runid);
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, source);
            stmt.execute();
            int detectorid = detector.getdetectorid();
            SubspaceTemplate template = detector.getTemplate();
            insertSubspaceParameterRow(detectorid, template.getnchannels(), template.getdimension(), conn);
            DetectorDAO.getInstance().writeDetectorChannels(conn, detectorid, template.getStaChanList());
            SubspaceTemplateDAO.getInstance().saveTemplateData(detectorid, template, TemplateNormalization.full_array, conn);
            conn.commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }

    }

    private void insertSubspaceParameterRow(int detectorid, int numChannels, int rank, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into subspace_detector_params (detectorid, num_channels,rank,normalization) values (?,?,?,?)")) {
            stmt.setInt(1, detectorid);
            stmt.setInt(2, numChannels);
            stmt.setInt(3, rank);
            stmt.setString(4, TemplateNormalization.full_array.toString());
            stmt.execute();
        }
    }
}
