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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import llnl.gnem.apps.detection.core.dataObjects.ArrayCorrelationParams;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.framework.FileBasedDetectorFactory;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.TemplateNormalization;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationDetector;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.dataAccess.interfaces.ArrayCorrelationDetectorDAO;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.database.ConnectionManager;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public abstract class DbArrayCorrelationDetectorDAO implements ArrayCorrelationDetectorDAO {

    @Override
    public ArrayCorrelationDetector createAndSaveArrayCorrelationDetector(ConcreteStreamProcessor processor, Collection<StreamSegment> eventSegments, double prepickSeconds,
            double correlationWindowLength, ArrayCorrelationParams params, String creationInfo) throws DataAccessException {
        try {
            return createAndSaveArrayCorrelationDetectorP(processor, eventSegments, prepickSeconds, correlationWindowLength, params, creationInfo);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<? extends Detector> buildFromExternalTemplate(DetectorSpecification spec, ConcreteStreamProcessor processor, Collection<Detector> retrievedDetectors) throws DataAccessException {
        try {
            return buildFromExternalTemplateP(spec, processor, retrievedDetectors);
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        } catch (UnsupportedOperationException ex) {
            throw new DataAccessException(ex.getMessage());
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<? extends Detector> retrieveStoredDetectors(ConcreteStreamProcessor processor) throws DataAccessException {
        try {
            return retrieveStoredDetectorsP(processor);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void saveDetector(DetectorSpecification spec, ArrayCorrelationDetector detector, int streamid, String source) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        int runid = RunInfo.getInstance().getRunid();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("insert into detector select ?, ?, ?, ?, ?, ?, ?, 'n', sysdate from dual");
            stmt.setInt(1, detector.getdetectorid());
            stmt.setInt(2, streamid);
            stmt.setString(3, DetectorType.ARRAY_CORRELATION.toString());
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
            ArrayCorrelationTemplate template = detector.getTemplate();
            double staDuration = detector.getStaDuration();
            double ltaDuration = detector.getLtaDuration();
            double gapDuration = detector.getGapDuration();
            insertSubspaceParameterRow(detectorid, template.getnchannels(), template.getdimension(), staDuration, ltaDuration, gapDuration, conn);
            DetectorUtil.writeDetectorChannels(conn, detectorid, template.getStaChanList());
            DetectionDAOFactory.getInstance().getSubspaceTemplateDAO().saveTemplateData(detectorid, template, TemplateNormalization.single_channel);
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

    private void insertSubspaceParameterRow(int detectorid, int numChannels, int rank, double staDuration, double ltaDuration, double gapDuration, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into subspace_detector_params values (?,?,?,?,?,?,?)")) {
            int idx = 1;
            stmt.setInt(idx++, detectorid);
            stmt.setInt(idx++, numChannels);
            stmt.setInt(idx++, rank);
            stmt.setDouble(idx++, staDuration);
            stmt.setDouble(idx++, ltaDuration);
            stmt.setDouble(idx++, gapDuration);
            stmt.setString(idx++, TemplateNormalization.single_channel.toString());
            stmt.execute();
        }
    }

    private boolean isDuplicateTemplate(ArrayCorrelationTemplate template, Collection<Detector> retrievedDetectors) {
        for (Detector detector : retrievedDetectors) {
            ArrayCorrelationDetector acd = (ArrayCorrelationDetector) detector;
            if (acd.getTemplate().equals(template)) {
                return true;
            }
        }
        return false;
    }

    private Collection<? extends Detector> retrieveStoredDetectorsP(ConcreteStreamProcessor processor) throws Exception {
        return DetectionDAOFactory.getInstance().getDetectorDAO().retrieveSubspaceDetectors(processor, TemplateNormalization.single_channel, DetectorType.ARRAY_CORRELATION);
    }

    private ArrayCorrelationDetector createAndSaveArrayCorrelationDetectorP(ConcreteStreamProcessor processor, Collection<StreamSegment> eventSegments, double prepickSeconds,
            double correlationWindowLength, ArrayCorrelationParams params, String creationInfo) throws Exception {

        ArrayList<StreamKey> streamChannels = new ArrayList<>(processor.getChannels());

        PreprocessorParams pparams = processor.getParams();
        double decimatedSampleRate = pparams.getPreprocessorParams().getDecimatedSampleRate();
        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();

        ArrayList<float[][]> eventData = DetectorUtil.extractFromTemplates(pparams, eventSegments, decimatedSampleRate, streamChannels);

        ArrayCorrelationSpecification spec = ConcreteStreamProcessor.createArrayCorrelationSpecification(processor, params, eventSegments, prepickSeconds, correlationWindowLength);

        ArrayCorrelationTemplate template = new ArrayCorrelationTemplate(eventData, spec, pparams);
        int detectorid = DetectionDAOFactory.getInstance().getDetectorDAO().getNewDetectorid();
        ArrayCorrelationDetector detector = new ArrayCorrelationDetector(detectorid, template, pparams, decimatedSampleRate, processor.getStreamName(), decimatedBlockSize);
        saveDetector(spec, detector, processor.getStreamId(), creationInfo);
        return detector;
    }

    private Collection<? extends Detector> buildFromExternalTemplateP(DetectorSpecification spec, ConcreteStreamProcessor processor, Collection<Detector> retrievedDetectors)
            throws SQLException, IOException, UnsupportedOperationException, Exception {
        Collection<Detector> result = new ArrayList<>();

        int detectorid = DetectionDAOFactory.getInstance().getDetectorDAO().getNewDetectorid();
        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();
        int streamid = processor.getStreamId();

        Detector tmpDetector = FileBasedDetectorFactory.createDetectorFromFiles(spec, detectorid, processor, decimatedBlockSize);

        ArrayCorrelationDetector sd = (ArrayCorrelationDetector) tmpDetector;

        if (isDuplicateTemplate(sd.getTemplate(), retrievedDetectors)) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("Duplicate template. Skipping."));
        } else {
            String source = spec.toString();
            saveDetector(spec, sd, streamid, source);
            result.add(sd);
        }
        return result;

    }

}
