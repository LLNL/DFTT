/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.database;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.TemplateNormalization;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationDetector;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.util.SubspaceThreshold;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DetectorStats;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class DetectorDAO {

    private DetectorDAO() {
    }

    public static DetectorDAO getInstance() {
        return DetectorDAOHolder.INSTANCE;
    }

    private void deleteDetections(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from detection where detectorid = ?")) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private void deleteTriggers(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from trigger_record where detectorid = ?")) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private static class DetectorDAOHolder {

        private static final DetectorDAO INSTANCE = new DetectorDAO();
    }

    /**
     * Replaces the template of a subspace detector with a compatible template
     * from another detector (assumed to be for same signal) but with a possible
     * shift and improved SNR from stacking or other enhancement. Also updates
     * the times and signal_durations in trigger_record if the offset is
     * non-zero. Does not update any other statistics. The source detector is
     * deleted upon completion.
     *
     * @param targetDetectorid The detector whose template will be replaced
     * @param sourceDetectorid The detector whos template will be used
     * @param templateOffset The offset in seconds of the new template start
     * relative to the old
     * @param templateDuration The duration of the new template
     * @throws java.sql.SQLException
     */
    public void replaceSubspaceTemplate(int targetDetectorid,
            int sourceDetectorid,
            double templateOffset,
            double templateDuration) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            removeExistingTemplate(targetDetectorid,  conn);
            String sql = "update subspace_template set detectorid = ? where detectorid = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, targetDetectorid);
                stmt.setInt(2, sourceDetectorid);
                stmt.execute();
            }
            sql = "update trigger_record set time = round(time + ?,3), signal_duration = round(?,3) where detectorid = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setDouble(1, templateOffset);
                stmt.setDouble(2, templateDuration);
                stmt.setInt(3, targetDetectorid);
                stmt.execute();
            }
            sql = "delete from detector where detectorid = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, sourceDetectorid);
                stmt.execute();
            }
            conn.commit();
             } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }
        private void removeExistingTemplate(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from subspace_template where detectorid = ?")) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }


    public void deleteDetector(int detectorid) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            deleteDetections(detectorid, conn);
            deleteTriggers(detectorid, conn);
            try (PreparedStatement stmt = conn.prepareStatement("delete from detector where detectorid = ?")) {
                stmt.setInt(1, detectorid);
                stmt.execute();
                conn.commit();
            }
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public ArrayList<StreamKey> getDetectorChannels(Connection conn, int detectorid) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select sta, chan from detector_channel where detectorid = ? order by position");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String sta = rs.getString(1);
                String chan = rs.getString(2);
                result.add(new StreamKey(sta, chan));
            }
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

    }

    public ArrayList<Integer> getSubspaceDetectorIDsWithDetections(int runid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Integer> result = new ArrayList<>();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select /*+parallel(12) */  detectorid from detection where runid = ? "
                    + " minus"
                    + " select /*+parallel(12) */ detectorid from detector_training_data where status in ('b','u')"
                    + " minus"
                    + " SELECT /*+ parallel(12) */ detectorid from trigger_classification a, detection b\n"
                    + " where a.triggerid = b.triggerid\n"
                    + " and (artifact_status = 'invalid' or usability_status = 'invalid') ");
            stmt.setInt(1, runid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt(1));
            }
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    public String getDetectorSourceInfo(int detectorid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select source_info from detector where detectorid = ?");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();

            while (rs.next()) {
                return rs.getString(1);
            }
            return "-";
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }

    }

    public void writeDetectorChannels(Connection conn, int detectorid, Collection<? extends StreamKey> channels) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into detector_channel values (?,?,?,?)")) {
            int j = 0;
            for (StreamKey sck : channels) {
                stmt.setInt(1, detectorid);
                stmt.setString(2, sck.getSta());
                stmt.setString(3, sck.getChan());
                stmt.setInt(4, j++);
                stmt.execute();
            }
        }
    }

    public Collection<? extends Detector> retrieveSubspaceDetectors(ConcreteStreamProcessor processor,
            TemplateNormalization normalizationType, DetectorType type) throws Exception {
        Collection<Detector> result = new ArrayList<>();
        Connection conn = null;
        String sql = "select /*+ parallel(12) */\n"
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
                + "          from detector\n"
                + "         where streamid = ?\n"
                + "        minus\n"
                + "        select /*+ parallel(12) */\n"
                + "         detectorid\n"
                + "          from detector_training_data\n"
                + "         where status in ('b','u')"
                + "         minus SELECT /*+ parallel(12) */ detectorid from trigger_classification a, "
                + "                detection b   where a.triggerid = b.triggerid "
                + "          and (artifact_status = 'invalid' or usability_status = 'invalid')) aa,\n"
                + "       detector a,\n"
                + "       subspace_detector_params b\n"
                + "where aa.detectorid = a.detectorid\n"
                + "   and streamid = ?\n"
                + "   and is_retired = 'n'\n"
                + "   and a.detectorid = b.detectorid\n"
                + "   and normalization = ?\n"
                + "   and detectortype = ?";
        try {
            conn = ConnectionManager.getInstance().checkOut();
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
                            ArrayList< StreamKey> channels = DetectorDAO.getInstance().getDetectorChannels(conn, detectorid);
                            if (channels.size() != numChannels) {
                                throw new IllegalStateException(String.format("Number of channels retrieved from "
                                        + "detector_channel (%d) does not match number in subspace_detector_params "
                                        + "(%d) for detectorid %d", channels.size(), numChannels, detectorid));
                            }
                            double decimatedSampleRate = params.getPreprocessorParams().getDecimatedSampleRate();
                            int decimatedBlockSize = params.getDecimatedDataBlockSize();
                            try {
                                if (normalizationType == TemplateNormalization.full_array) {
                                    SubspaceTemplate subspaceTemplate = SubspaceTemplateDAO.getInstance().getSubspaceTemplate(conn, detectorid);
                                    if (!params.getPreprocessorParams().consistentWith(subspaceTemplate.getProcessingParameters())) {
                                        throw new IllegalStateException("Subspace template is inconsistent with parameters for stream!");
                                    }

                                    SubspaceDetector detector = new SubspaceDetector(detectorid, subspaceTemplate, decimatedSampleRate, processor.getStreamName(), processor.getFFTSize(), decimatedBlockSize);
                                    detector.getSpecification().setThreshold((float) threshold);
                                    result.add(detector);

                                } else if (normalizationType == TemplateNormalization.single_channel) {
                                    ArrayCorrelationTemplate template = SubspaceTemplateDAO.getInstance().getArrayCorrelationTemplate(conn, detectorid);
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
                            } catch (Exception ex) {
                                ApplicationLogger.getInstance().log(Level.WARNING, "Failed creating ArrayCorrelationDetector!", ex);
                            }
                        }
                    }
                }
            }
            return result;
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private boolean useThisRunid(int creationalRunid) {
        return !ProcessingPrescription.getInstance().isLoadOnlyDetectorsFromSpecifiedRunid() || creationalRunid == ProcessingPrescription.getInstance().getRunidForDetectorRetrieval();
    }

    public Collection<DetectorStats> getDetectorStats(int runid, boolean suppressBadDetectors) throws SQLException {
        Collection<DetectorStats> result = new ArrayList<>();

        String minusClause = suppressBadDetectors ? "          minus\n"
                + "                select /*+ parallel(12) */\n"
                + "                 detectorid\n"
                + "                  from detector_training_data\n"
                + "                 where status in ('b','u')"
                + "                minus SELECT /*+ parallel(12) */ detectorid from trigger_classification a, "
                + "                detection b   where a.triggerid = b.triggerid "
                + "          and (artifact_status = 'invalid' or usability_status = 'invalid') " : "";

        Connection conn = null;
        String sql = String.format("select /*+ parallel(12) */\n"
                + "detectorid, detectortype, status, rank, count(*) cnt\n"
                + "  from (select a.detectorid,\n"
                + "               b.DETECTORTYPE,\n"
                + "               case b.source_info\n"
                + "                 when 'Created From Stream' then\n"
                + "                  'S'\n"
                + "                 when 'Created by Builder' then\n"
                + "                  'B'\n"
                + "                 else\n"
                + "                  'O'\n"
                + "               end as status,\n"
                + "               c.rank\n"
                + "          from (select /*+ parallel(12) */\n"
                + "                distinct  detectorid\n"
                + "                  from detection\n"
                + "                 where runid = ? %s\n"
                + "                ) aa,\n"
                + "               detection a,\n"
                + "               detector b,\n"
                + "               subspace_detector_params c\n"
                + "         where a.runid = ?\n"
                + "           and aa.detectorid = a.detectorid\n"
                + "           and a.detectorid = b.detectorid\n"
                + "           and a.detectorid = c.detectorid(+))\n"
                + "having count(*) > ? "
                + "group by detectorid, detectortype, status, rank\n"
                + "           order by cnt desc", minusClause);
        try {
            conn = ConnectionManager.getInstance().checkOut();
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
                        result.add(new DetectorStats(runid, detectorid, type, creationType, rank, count));
                    }
                    return result;
                }
            }
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public int getNewDetectorid() throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            return (int) OracleDBUtil.getNextId(conn, "detectorid");
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

}
