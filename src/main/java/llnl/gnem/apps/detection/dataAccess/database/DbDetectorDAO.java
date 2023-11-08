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
package llnl.gnem.apps.detection.dataAccess.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.TemplateNormalization;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationDetector;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectorDAO;

import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DetectorStats;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.DetectoridRestriction;
import llnl.gnem.apps.detection.util.SubspaceThreshold;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;

import llnl.gnem.dftt.core.database.Connections;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;

public abstract class DbDetectorDAO implements DetectorDAO {

    @Override
    public List<StreamKey> getDetectorChannels(long detectorid) throws DataAccessException {
        try {
            return getDetectorChannelsP(detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void replaceSubspaceTemplate(int oldDetectorid,
            int newDetectorid,
            double templateOffset,
            double templateDuration,
            String sourceInfo) throws DataAccessException {
        try {
            replaceSubspaceTemplateP(oldDetectorid, newDetectorid, templateOffset, templateDuration, sourceInfo);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void deleteDetector(int detectorid) throws DataAccessException {
        try {
            deleteDetectorP(detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public ArrayList<Integer> getSubspaceDetectorIDsWithDetections(int runid) throws DataAccessException {
        try {
            return getSubspaceDetectorIDsWithDetectionsP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<DetectorStats> getDetectorStats(int runid, boolean suppressBadDetectors) throws DataAccessException {
        try {
            return getDetectorStatsP(runid, suppressBadDetectors);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int getNewDetectorid() throws DataAccessException {
        try {
            return getNewDetectoridP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<? extends Detector> retrieveSubspaceDetectors(ConcreteStreamProcessor processor,
            TemplateNormalization normalizationType, DetectorType type) throws DataAccessException {
        try {
            return retrieveSubspaceDetectorsP(processor, normalizationType, type);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<Integer> getBootDetectorIds(int streamid) throws DataAccessException {
        try {
            return getBootDetectorIdsP(streamid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    
    private Collection<Integer> getBootDetectorIdsP(int streamid)
            throws SQLException {
        Collection<Integer> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select a.detectorid\n"
                + "  from %s a, %s b\n"
                + " where streamid = ? and is_retired = 'n' \n"
                + "   and a.detectorid = b.detectorid\n"
                + "   union\n"
                + "select a.detectorid\n"
                + "  from %s a, %s b\n"
                + " where streamid = ? and is_retired = 'n' \n"
                + "   and a.detectorid = b.detectorid "
                + "   union\n"
                + "select a.detectorid\n"
                + "  from %s a, %s b\n"
                + " where streamid = ? and is_retired = 'n' \n"
                + "   and a.detectorid = b.detectorid",
                TableNames.getDetectorTable(),
                TableNames.getSTALTADetectorParamsTable(),
                TableNames.getDetectorTable(),
                TableNames.getArrayDetectorParamsTable(),
                TableNames.getDetectorTable(),
                TableNames.getBulletinDetectorSpecTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, streamid);
                stmt.setInt(2, streamid);
                stmt.setInt(3, streamid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getInt(1));
                    }
                    return result;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private Collection<? extends Detector> retrieveSubspaceDetectorsP(ConcreteStreamProcessor processor,
            TemplateNormalization normalizationType, DetectorType type) throws Exception {
        Collection<Detector> result = new ArrayList<>();
        Connection conn = null;
        String tmp = String.format("minus\n"
                + "        select /*+ parallel(12) */\n"
                + "         detectorid\n"
                + "          from %s\n"
                + "         where status in ('b','u')"
                + "         minus SELECT /*+ parallel(12) */ detectorid from %s a, "
                + "                %s b   where a.triggerid = b.triggerid "
                + "          and (artifact_status = 'invalid' or usability_status = 'invalid')",
                TableNames.getDetectorTrainingDataTable(),
                TableNames.getTriggerClassificationTable(),
                TableNames.getDetectionTable());
        String minusClause = DetectoridRestriction.getInstance().isIgnoreDetectorClassification() ? "" : tmp;

        String sql = String.format("select /*+ parallel(12) */\n"
                + "b.detectorid,\n"
                + "threshold,\n"
                + "blackout_seconds,\n"
                + "num_channels,\n"
                + "creation_runid,\n"
                + "sta_duration,\n"
                + "lta_duration,\n"
                + "gap_duration\n"
                + "  from (select /*+ parallel(12) */\n"
                + "         detectorid\n"
                + "          from %s\n"
                + "         where streamid = ?\n  "
                + minusClause
                + ") aa,\n"
                + "       %s a,\n"
                + "       %s b\n"
                + "where aa.detectorid = a.detectorid\n"
                + "   and streamid = ?\n"
                + "   and is_retired = 'n'\n"
                + "   and a.detectorid = b.detectorid\n"
                + "   and normalization = ?\n"
                + "   and detectortype = ?",
                TableNames.getDetectorTable(),
                TableNames.getDetectorTable(),
                TableNames.getSubspaceDetectorParamsTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, processor.getStreamId());
                stmt.setInt(2, processor.getStreamId());
                stmt.setString(3, normalizationType.toString());
                stmt.setString(4, type.toString());
                try (ResultSet rs = stmt.executeQuery()) {

                    PreprocessorParams params = processor.getParams();
                    while (rs.next()) {
                        int idx = 1;
                        int detectorid = rs.getInt(idx++);
                        SubspaceThreshold.getInstance().setThresholdFromDb(rs.getDouble(idx++));
                        double threshold = SubspaceThreshold.getInstance().getDesiredThreshold(processor.getStreamName());
                        double blackoutSeconds = rs.getDouble(idx++);
                        int numChannels = rs.getInt(idx++);
                        int creationalRunid = rs.getInt(idx++);
                        double staDuration = rs.getDouble(idx++);
                        double ltaDuration = rs.getDouble(idx++);
                        double gapDuration = rs.getDouble(idx++);
                        if (useThisRunid(creationalRunid)) {
                            ArrayList< StreamKey> channels = DetectorUtil.getDetectorChannels(conn, detectorid);
                            if (channels.size() != numChannels) {
                                throw new IllegalStateException(String.format("Number of channels retrieved from "
                                        + "detector_channel (%d) does not match number in subspace_detector_params "
                                        + "(%d) for detectorid %d", channels.size(), numChannels, detectorid));
                            }
                            double decimatedSampleRate = params.getPreprocessorParams().getDecimatedSampleRate();
                            int decimatedBlockSize = params.getDecimatedDataBlockSize();
                            try {
                                if (normalizationType == TemplateNormalization.full_array) {
                                    SubspaceTemplate subspaceTemplate = DetectionDAOFactory.getInstance().getSubspaceTemplateDAO().getSubspaceTemplate(detectorid);
                                    if (!params.getPreprocessorParams().consistentWith(subspaceTemplate.getProcessingParameters())) {
                                        throw new IllegalStateException("Subspace template is inconsistent with parameters for stream!");
                                    }

                                    SubspaceDetector detector = new SubspaceDetector(detectorid, subspaceTemplate, decimatedSampleRate, processor.getStreamName(), processor.getFFTSize(), decimatedBlockSize);
                                    detector.getSpecification().setThreshold((float) threshold);
                                    result.add(detector);

                                } else if (normalizationType == TemplateNormalization.single_channel) {
                                    ArrayCorrelationTemplate template = DetectionDAOFactory.getInstance().getSubspaceTemplateDAO().getArrayCorrelationTemplate(detectorid);
                                    ArrayCorrelationSpecification spec = new ArrayCorrelationSpecification(
                                            (float) threshold,
                                            (float) blackoutSeconds,
                                            -1,
                                            -1,
                                            -1,
                                            (float) staDuration,
                                            (float) ltaDuration,
                                            (float) gapDuration,
                                            channels);
                                    result.add(new ArrayCorrelationDetector(detectorid, template, params, decimatedSampleRate, processor.getStreamName(), decimatedBlockSize));
                                }
                            } catch (IOException | IllegalStateException | DataAccessException ex) {
                                ApplicationLogger.getInstance().log(Level.WARNING, "Failed creating ArrayCorrelationDetector!", ex);
                            }
                        }
                    }
                }
            }
            return result;
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private boolean useThisRunid(int creationalRunid) {
        return !ProcessingPrescription.getInstance().isLoadOnlyDetectorsFromSpecifiedRunid() || creationalRunid == ProcessingPrescription.getInstance().getRunidForDetectorRetrieval();
    }

    private int getNewDetectoridP() throws SQLException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            return (int) OracleDBUtil.getNextId(conn, SequenceNames.getDetectoridSequenceName());
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private List<StreamKey> getDetectorChannelsP(long detectorid) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        String sql = String.format("select sta, chan from detector_channel where detectorid = ? order by position", TableNames.getDetectorChannelTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String sta = rs.getString(1);
                        String chan = rs.getString(2);
                        result.add(new StreamKey(sta, chan));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    @Override
    public List<StreamKey> getDetectorChannelsFromConfig(long configid) throws DataAccessException {
        try {
            return getDetectorChannelsPFromConfigP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public String getDetectorSourceInfo(int detectorid) throws DataAccessException {
        try {
            return getDetectorSourceInfoP(detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private List<StreamKey> getDetectorChannelsPFromConfigP(long configid) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        String sql = String.format("select station_code, chan from %s where streamid in (\n"
                + "select streamid from stream where configid = ?)",
                TableNames.getStreamChannelTable(),
                TableNames.getStreamTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, configid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String sta = rs.getString(1);
                        String chan = rs.getString(2);
                        result.add(new StreamKey(sta, chan));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    @Override
    public void writeChangedThreshold(int runid, int detectorid, TimeT streamTime, double newThreshold) throws DataAccessException {
        try {
            writeChangedThresholdP(runid, detectorid, streamTime, newThreshold);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void writeChangedThresholdP(int runid, int detectorid, TimeT streamTime, double newThreshold) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?)", TableNames.getDetectorThresholdHistoryTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                stmt.setInt(2, detectorid);
                stmt.setDouble(3, streamTime.getEpochTime());
                stmt.setDouble(4, newThreshold);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private void replaceSubspaceTemplateP(int oldDetectorid, int newDetectorid, double templateOffset, double templateDuration, String sourceInfo) throws SQLException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            removeExistingTemplate(oldDetectorid, conn);
            String sql = String.format("update %s set detectorid = ? where detectorid = ?",
                    TableNames.getSubspaceTemplateTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, oldDetectorid);
                stmt.setInt(2, newDetectorid);
                stmt.execute();
            }
            removeExistingParams(oldDetectorid, conn);
            sql = String.format("update %s set detectorid = ?  where detectorid = ?",
                    TableNames.getSubspaceDetectorParamsTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, oldDetectorid);
                stmt.setInt(2, newDetectorid);
                stmt.execute();
            }
            sql = String.format("update %s set time = round(time + ?,3), signal_duration = round(?,3) where detectorid = ?",
                    TableNames.getTriggerRecordTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, templateOffset);
                stmt.setDouble(2, templateDuration);
                stmt.setInt(3, oldDetectorid);
                stmt.execute();
            }
            sql = String.format("update detector set creation_runid = null, source_info = ?  where detectorid = ?",
                    TableNames.getDetectorTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, sourceInfo);
                stmt.setInt(2, oldDetectorid);
                stmt.execute();
            }
            sql = String.format("delete from %s where detectorid = ?",
                    TableNames.getDetectorTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newDetectorid);
                stmt.execute();
            }
            conn.commit();

        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void removeExistingTemplate(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(String.format("delete from %s where detectorid = ?",
                TableNames.getSubspaceTemplateTable()))) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private void removeExistingParams(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(String.format("delete from %s where detectorid = ?",
                TableNames.getSubspaceDetectorParamsTable()))) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private void deleteDetectorP(int detectorid) throws SQLException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            deleteDetections(detectorid, conn);
            deleteTriggers(detectorid, conn);
            try (PreparedStatement stmt = conn.prepareStatement(String.format("delete from %s where detectorid = ?",
                    TableNames.getDetectorTable()))) {
                stmt.setInt(1, detectorid);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void deleteDetections(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(String.format("delete from %s where detectorid = ?",
                TableNames.getDetectionTable()))) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private void deleteTriggers(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(String.format("delete from %s where detectorid = ?",
                TableNames.getTriggerRecordTable()))) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private ArrayList<Integer> getSubspaceDetectorIDsWithDetectionsP(int runid) throws SQLException {
        ArrayList<Integer> result = new ArrayList<>();
        Connection conn = null;
        String sql = String.format("select /*+parallel(12) */  detectorid from %s where runid = ? "
                + " minus"
                + " select /*+parallel(12) */ detectorid from %s where status in ('b','u')"
                + " minus"
                + " SELECT /*+ parallel(12) */ detectorid from trigger_classification a, detection b\n"
                + " where a.triggerid = b.triggerid\n"
                + " and (artifact_status = 'invalid' or usability_status = 'invalid') ",
                TableNames.getDetectionTable(),
                TableNames.getDetectorTrainingDataTable(),
                TableNames.getTriggerClassificationTable());

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getInt(1));
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return result;
    }

    private String getDetectorSourceInfoP(int detectorid) throws SQLException {
        String sql = String.format("select source_info from %s where detectorid = ?", TableNames.getDetectorTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return rs.getString(1);
                    }
                    return "-";
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<DetectorStats> getDetectorStatsP(int runid, boolean suppressBadDetectors) throws SQLException {
        Collection<DetectorStats> result = new ArrayList<>();

        String minusClause = suppressBadDetectors ? String.format("          minus\n"
                + "                select /*+ parallel */\n"
                + "                 detectorid\n"
                + "                  from %s\n"
                + "                 where status in ('b','u')"
                + "                minus SELECT /*+ parallel */ detectorid from %s a, "
                + "                detection b   where a.triggerid = b.triggerid "
                + "          and (artifact_status = 'invalid' or usability_status = 'invalid') ",
                TableNames.getDetectorTrainingDataTable(),
                TableNames.getTriggerClassificationTable()) : "";

        String sql = String.format("select aa.*, decode(bb.status, null, '-', bb.status) class_status\n"
                + "  from (select /*+ parallel(24) */\n"
                + "         detectorid, detectortype, status, rank, count(*) cnt\n"
                + "          from (select  /*+ ordered use_nl(a,b,c) */ a.detectorid,\n"
                + "                       b.DETECTORTYPE,\n"
                + "                       case b.source_info\n"
                + "                         when 'Created From Stream' then\n"
                + "                          'S'\n"
                + "                         when 'Rewindowed In Framework' then\n"
                + "                          'R'\n"
                + "                         when 'Created by Builder' then\n"
                + "                          'B'\n"
                + "                         else\n"
                + "                          'O'\n"
                + "                       end as status,\n"
                + "                       c.rank\n"
                + "                  from (select  /*+ parallel(24) index (detection DETECTION_RUNID_IDX)*/ \n"
                + "                        distinct detectorid\n"
                + "                          from %s\n"
                + "                         where runid = ? %s) aa,\n"
                + "                       %s a,\n"
                + "                       %s b,\n"
                + "                       %s c\n"
                + "                 where a.runid = ?\n"
                + "                   and aa.detectorid = a.detectorid\n"
                + "                   and a.detectorid = b.detectorid\n"
                + "                   and a.detectorid = c.detectorid(+))\n"
                + "        having count(*) > ?\n"
                + "         group by detectorid, detectortype, status, rank) aa,\n"
                + "       detector_training_data bb\n"
                + " where aa.detectorid = bb.detectorid(+)\n"
                + " order by cnt desc", TableNames.getDetectionTable(),
                minusClause,
                TableNames.getDetectionTable(),
                TableNames.getDetectorTable(),
                TableNames.getSubspaceDetectorParamsTable(),
                TableNames.getDetectorTrainingDataTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                stmt.setInt(2, runid);
                stmt.setInt(3, ParameterModel.getInstance().getMinDetectionCountForRetrieval() - 1);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int detectorid = rs.getInt(jdx++);
                        DetectorType type = DetectorType.valueOf(rs.getString(jdx++));
                        String creationType = rs.getString(jdx++);
                        int rank = rs.getInt(jdx++);
                        int count = rs.getInt(jdx++);
                        String classStatus = rs.getString(jdx++);
                        TriggerClassification tc = TriggerClassification.createFromSingleStatusString(classStatus);
                        result.add(new DetectorStats(runid, detectorid, type, creationType, rank, count, tc));
                    }

                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return result;
    }
}
