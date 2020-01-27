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

import com.oregondsp.util.TimeStamp;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.Trigger;
import llnl.gnem.apps.detection.util.DetectorSubstitution;
import llnl.gnem.apps.detection.util.SubstitutionReason;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.core.dataObjects.DetectionObjects;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.apps.detection.core.signalProcessing.FKMeasurement;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.ClassifiedDetection;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.PairT;

/**
 *
 * @author dodge1
 */
public class DetectionDAO {

    private static final String TRIGGER_FK_DATA_TABLE = "trigger_fk_data";
    private static final String TRIGGER_DATA_FEATURE_TABLE = "trigger_data_feature";

    private void removeExistingStats(int detectionid, Connection conn) throws SQLException {
        String sql = String.format("delete from %s where detectionid = ?", "detection_fk_measurement");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectionid);
            stmt.execute();

        }
    }

    private void writeNewStats(int detectionid, double time, double winlen, FKMeasurement measurement, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?,?,?,?,?)", "detection_fk_measurement");

        float[] slow = measurement.getSlownessEstimate();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int jdx = 1;
            stmt.setInt(jdx++, detectionid);
            stmt.setDouble(jdx++, time);
            stmt.setDouble(jdx++, winlen);
            stmt.setDouble(jdx++, measurement.getQuality());
            stmt.setDouble(jdx++, measurement.getAzimuth());
            stmt.setDouble(jdx++, measurement.getVelocity());
            stmt.setDouble(jdx++, slow[0]);
            stmt.setDouble(jdx++, slow[1]);
            stmt.execute();

        }
    }

    private static class DetectionDAOHolder {

        private static final DetectionDAO INSTANCE = new DetectionDAO();
    }

    private DetectionDAO() {
    }

    public static DetectionDAO getInstance() {
        return DetectionDAOHolder.INSTANCE;
    }

    public void deleteDetections(ArrayList<Integer> detectionIdValues) throws SQLException{
        Connection conn = null;
        
        String sql = String.format("delete from trigger_record where triggerid in "
                + "(select triggerid from detection where detectionid = ?)");
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                for(Integer detectionid : detectionIdValues){
                    stmt.setInt(1, detectionid);
                    stmt.execute();
                }
            }
            conn.commit();

        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }
    
    public void deleteDetection(Detection detection) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();

            stmt = conn.prepareStatement("delete from detection where detectionid = ?");
            stmt.setInt(1, detection.getDetectionid());

            stmt.execute();
            TriggerDAO.getInstance().deleteTrigger(detection.getTriggerid(), conn);
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

    public void writeDetectionFKStats(int detectionid, double time, double winlen, FKMeasurement measurement) throws SQLException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getInstance().checkOut();
            removeExistingStats(detectionid, conn);
            writeNewStats(detectionid, time, winlen, measurement, conn);
            conn.commit();

        } finally {

            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private void copyTriggerChildRecords(Connection conn, int oldTriggerid, int newTriggerid) throws SQLException {
        copyTriggerDataFeatureRow(conn, oldTriggerid, newTriggerid);
        copyTriggerFkDataRow(conn, oldTriggerid, newTriggerid);

    }

    private void copyTriggerDataFeatureRow(Connection conn, int oldTriggerid, int newTriggerid) throws SQLException {
        String sql = String.format("insert into %s select ?, SNR,AMPLITUDE,TIME_CENTROID,TIME_SIGMA,TEMPORAL_SKEWNESS,TEMPORAL_KURTOSIS,"
                + "FREQ_SIGMA,\n"
                + "TBP, SKEWNESS, KURTOSIS,RAW_SKEWNESS,RAW_KURTOSIS, FREQ_CENTROID, RELATIVE_AMPLITUDE from %s where triggerid = ?",
                TRIGGER_DATA_FEATURE_TABLE, TRIGGER_DATA_FEATURE_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newTriggerid);
            stmt.setInt(2, oldTriggerid);
            stmt.execute();
        }
    }

    private void copyTriggerFkDataRow(Connection conn, int oldTriggerid, int newTriggerid) throws SQLException {
        String sql = String.format("insert into %s select ?, fk_qual, back_azimuth, velocity, sx, sy from %s where triggerid = ?",
                TRIGGER_FK_DATA_TABLE, TRIGGER_FK_DATA_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newTriggerid);
            stmt.setInt(2, oldTriggerid);
            stmt.execute();
        }
    }

    public void reassignDetection(Detection detection, DetectorSubstitution substitute)
            throws SQLException {
        int oldTriggerid = detection.getTriggerid();
        int detectorid = substitute.getDetector().getdetectorid();
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            TimeStamp newTriggerTime = new TimeStamp(detection.getTriggerTime().epochAsDouble() + substitute.getShift());
            int newTriggerid = TriggerDAO.getInstance().createTriggerRecord(conn, detection.getRunid(),
                    detectorid, newTriggerTime, substitute.getStatisticValue(), true, false, substitute.getSrcDetectorid(), oldTriggerid, substitute.getSubstitutionReason(), detection.getSignalDuration());
            setOldTriggerRejected(conn, oldTriggerid);
            updateDetection(conn, detection.getDetectionid(), newTriggerid,
                    detectorid);
            copyTriggerChildRecords(conn, oldTriggerid, newTriggerid);
            conn.commit();
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private static void updateDetection(Connection conn, int detectionid,
            int newTriggerid, int detectorid) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("update detection set triggerid = ?, detectorid = ? where detectionid = ?")) {
            stmt.setInt(1, newTriggerid);
            stmt.setInt(2, detectorid);
            stmt.setInt(3, detectionid);
            stmt.execute();
        }
    }

    private Detection getExistingDetection(Trigger trigger, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select detectionid from detection where triggerid = ?");
            stmt.setInt(1, trigger.getTriggerid());
            rs = stmt.executeQuery();
            while (rs.next()) {
                int detectionid = rs.getInt(1);
                return new Detection(detectionid, trigger);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public Detection detectionFromTrigger(Trigger trigger) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            Detection existing = getExistingDetection(trigger, conn);
            if (existing != null) {
                return existing;
            } else {
                stmt = conn.prepareStatement("insert into detection select detectionid.nextval, ?, ?, ? from dual");
                stmt.setInt(1, trigger.getRunid());
                stmt.setInt(2, trigger.getTriggerid());
                stmt.setInt(3, trigger.getDetectorid());
                stmt.execute();
                TriggerDAO.getInstance().setTriggerProcessingStatus(trigger.getTriggerid(), true, false, conn);
                conn.commit();
                int detectionid = (int) OracleDBUtil.getIdCurrVal(conn, "detectionid");
                return new Detection(detectionid, trigger);
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private static void setOldTriggerRejected(Connection conn, int triggerid)
            throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("update trigger_record set rejected = 'y' where triggerid = ?")) {
            stmt.setInt(1, triggerid);
            stmt.execute();
        }

    }

    public Collection<ClassifiedDetection> getDetections(int runid, int detectorid) throws SQLException {
        Collection<ClassifiedDetection> result = new ArrayList<>();
        Connection conn = null;

        int rawTriggerIndex = -1; // For now assuming this is unimportant in this context.
        String sql = "select a.triggerid,  \n"
                + "c.detectortype, \n"
                + "a.detection_statistic,\n"
                + "a.time, \n"
                + "a.processed, \n"
                + "a.rejected,\n"
                + "a.src_detectorid, \n"
                + "a.src_triggerid, \n"
                + "a.substitution_reason, \n"
                + "a.signal_duration, \n"
                + "b.detectionid, \n"
                + "d.artifact_status, \n"
                + "d.usability_status from \n"
                + "trigger_record a, \n"
                + "detection b, \n"
                + "detector c ,\n"
                + "trigger_classification d\n"
                + "where a.runid = ? \n"
                + "and a.detectorid = ? \n"
                + "and a.triggerid = b.triggerid \n"
                + "and a.detectorid = c.detectorid \n"
                + "and a.triggerid = d.triggerid(+)\n"
                + "order by time";
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, runid);
                stmt.setInt(2, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int col = 1;
                        int triggerid = rs.getInt(col++);
                        DetectorType type = DetectorType.valueOf(rs.getString(col++));
                        double statistic = rs.getDouble(col++);
                        double time = rs.getDouble(col++);
                        String processedString = rs.getString(col++);
                        boolean processed = processedString.equals("y");
                        String rejectedString = rs.getString(col++);
                        boolean rejected = rejectedString.equals("y");
                        int srcDetectorid = rs.getInt(col++);
                        int srcTriggerid = rs.getInt(col++);
                        if (rs.wasNull()) {
                            srcTriggerid = -1;
                        }
                        SubstitutionReason reason = SubstitutionReason.valueOf(rs.getString(col++));
                        double duration = rs.getDouble(col++);
                        int detectionid = rs.getInt(col++);
                        String artifactStatus = rs.getString(col++);
                        if (rs.wasNull()) {
                            artifactStatus = "unset";
                        }
                        String usabilityStatus = rs.getString(col++);
                        if (rs.wasNull()) {
                            usabilityStatus = "unset";
                        }
                        Trigger trigger = new Trigger(triggerid,
                                runid,
                                detectorid,
                                type,
                                (float) statistic,
                                new TimeStamp(time),
                                processed,
                                rejected,
                                srcDetectorid,
                                srcTriggerid,
                                reason,
                                duration,
                                rawTriggerIndex, 0);
                        result.add(new ClassifiedDetection(detectionid, trigger, artifactStatus, usabilityStatus));
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

    public Detection getSingleDetection(int detectionid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int rawTriggerIndex = -1; // For now assuming this is unimportant in this context.
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select a.triggerid,  "
                    + "c.detectortype, a.detection_statistic,a.time, a.processed, "
                    + "a.rejected,a.src_detectorid, a.src_triggerid, a.substitution_reason, "
                    + "a.signal_duration, a.runid, a.detectorid from trigger_record a, detection b, detector c "
                    + "where b.detectionid = ? "
                    + "and a.triggerid = b.triggerid "
                    + "and a.detectorid = c.detectorid");
            stmt.setInt(1, detectionid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int col = 1;
                int triggerid = rs.getInt(col++);
                DetectorType type = DetectorType.valueOf(rs.getString(col++));
                double statistic = rs.getDouble(col++);
                double time = rs.getDouble(col++);
                String processedString = rs.getString(col++);
                boolean processed = processedString.equals("y");
                String rejectedString = rs.getString(col++);
                boolean rejected = rejectedString.equals("y");
                int srcDetectorid = rs.getInt(col++);
                int srcTriggerid = rs.getInt(col++);
                if (rs.wasNull()) {
                    srcTriggerid = -1;
                }
                SubstitutionReason reason = SubstitutionReason.valueOf(rs.getString(col++));
                double duration = rs.getDouble(col++);
                int runid = rs.getInt(col++);
                int detectorid = rs.getInt(col++);
                Trigger trigger = new Trigger(triggerid,
                        runid,
                        detectorid,
                        type,
                        (float) statistic,
                        new TimeStamp(time),
                        processed,
                        rejected,
                        srcDetectorid,
                        srcTriggerid,
                        reason,
                        duration,
                        rawTriggerIndex, 0);
                return new Detection(detectionid, trigger);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public TriggerDataFeatures getTriggerDataFeatures(int detectionid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select SNR,"
                    + "AMPLITUDE,"
                    + "TIME_CENTROID,"
                    + "TIME_SIGMA,"
                    + "TEMPORAL_SKEWNESS,"
                    + "TEMPORAL_KURTOSIS,"
                    + "TEMPORAL_HYPER_KURTOSIS,"
                    + "TEMPORAL_HYPER_FLATNESS,"
                    + "FREQ_SIGMA,"
                    + "TBP,"
                    + "SKEWNESS,"
                    + "KURTOSIS,"
                    + "RAW_SKEWNESS,"
                    + "RAW_KURTOSIS, "
                    + "FREQ_CENTROID from detection a, trigger_data_feature b"
                    + " where detectionid = ? and a.triggerid = b.triggerid");
            stmt.setInt(1, detectionid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                double snr = rs.getDouble(jdx++);
                double amplitude = rs.getDouble(jdx++);
                double timeCentroid = rs.getDouble(jdx++);
                double timeSigma = rs.getDouble(jdx++);
                double tempSkewness = rs.getDouble(jdx++);
                double tempKurtosis = rs.getDouble(jdx++);
                double tempHyperKurt = rs.getDouble(jdx++);
                double tempHyperFlat = rs.getDouble(jdx++);
                double freqSigma = rs.getDouble(jdx++);
                double tbp = rs.getDouble(jdx++);
                double skewness = rs.getDouble(jdx++);
                double kurtosis = rs.getDouble(jdx++);
                double rawSkew = rs.getDouble(jdx++);
                double rawKurt = rs.getDouble(jdx++);
                double freqCentroid = rs.getDouble(jdx++);
                return new TriggerDataFeatures(snr, amplitude, timeCentroid,
                        timeSigma, tempSkewness, tempKurtosis, tempHyperKurt,
                        tempHyperFlat, freqSigma, tbp, skewness, kurtosis, rawKurt,
                        rawSkew, freqCentroid);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public DetectionObjects getDetectionObjects(int runid, int detectorid, boolean retrieveByBlocks, int blockSize, int lastRetrievedDetectionId) throws SQLException {
        Map<Integer, TriggerClassification> triggerClassificationMap = new HashMap<>();
        Collection<PairT<Integer, Double>> detTimes = new ArrayList<>();
        Collection<Double> U = new ArrayList<>();
        Connection conn = null;
        String restrictionClause = retrieveByBlocks ? " and detectionid > ? " : "";
        int rowsRetrieved = 0;
        int maxDetectionId = 0;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            String sql = String.format("select /*+ parallel(12) ordered use_nl(b,c) */ distinct "
                    + "detectionid, "
                    + "time, "
                    + "artifact_status, "
                    + "usability_status "
                    + "from detection a, trigger_record b, trigger_classification c "
                    + "where a.runid = ? "
                    + "and a.detectorid = ? %s"
                    + "and a.triggerid = b.triggerid and a.triggerid = c.triggerid(+)"
                    + "and detection_statistic between ? and ? order by detectionid", restrictionClause);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int j = 1;
                stmt.setInt(j++, runid);
                stmt.setInt(j++, detectorid);
                if (retrieveByBlocks) {
                    stmt.setInt(j++, lastRetrievedDetectionId);
                }
                stmt.setDouble(j++, ParameterModel.getInstance().getMinDetStatThreshold());
                stmt.setDouble(j++, ParameterModel.getInstance().getMaxDetStatThreshold());
                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        int jdx = 1;
                        int detectionid = rs.getInt(jdx++);
                        maxDetectionId = detectionid;
                        double time = rs.getDouble(jdx++);
                        String artifactStatus = rs.getString(jdx++);
                        if (rs.wasNull()) {
                            artifactStatus = "unset";
                        }
                        String usabilityStatus = rs.getString(jdx++);
                        if (rs.wasNull()) {
                            usabilityStatus = "unset";
                        }
                        TriggerClassification tc = TriggerClassification.createFromStatusStrings(artifactStatus, usabilityStatus);
                        triggerClassificationMap.put(detectionid, tc);
                        detTimes.add(new PairT<>(detectionid, time));
                        U.add(time);
                        ++rowsRetrieved;

                        if (retrieveByBlocks && rowsRetrieved >= blockSize) {
                            
                            break;
                        }
                    }
                }
                return new DetectionObjects(triggerClassificationMap, detTimes, U, maxDetectionId);
            }
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

}
