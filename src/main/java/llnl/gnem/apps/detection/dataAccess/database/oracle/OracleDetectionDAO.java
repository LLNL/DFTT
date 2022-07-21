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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.database.DbDetectionDAO;
import llnl.gnem.apps.detection.dataAccess.database.SequenceNames;
import llnl.gnem.apps.detection.dataAccess.database.TableNames;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectionSummary;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.dataAccess.dataobjects.SubstitutionReason;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Trigger;

import llnl.gnem.apps.detection.util.DetectorSubstitution;
import llnl.gnem.apps.detection.util.TimeStamp;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;

/**
 *
 * @author dodge1
 */
public class OracleDetectionDAO extends DbDetectionDAO {

    private OracleDetectionDAO() {
    }

    public static OracleDetectionDAO getInstance() {
        return OracleDetectionDAOHolder.INSTANCE;
    }

    private static class OracleDetectionDAOHolder {

        private static final OracleDetectionDAO INSTANCE = new OracleDetectionDAO();
    }

    @Override
    public Detection detectionFromTrigger(Trigger trigger) throws DataAccessException {
        try {
            return detectionFromTriggerP(trigger);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public void reassignDetection(Detection detection, DetectorSubstitution substitute) throws DataAccessException {
        try {
            reassignDetectionP(detection, substitute);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public Collection<DetectionSummary> getDetectionSummaries(int runid, int detectorid, double detStatThreshold) throws DataAccessException {
        try {
            return getDetectionSummariesP(runid, detectorid, detStatThreshold);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<DetectionSummary> getDetectionSummariesP(int runid, int detectorid, double detStatThreshold) throws SQLException {
        Connection conn = null;
        Collection<DetectionSummary> result = new ArrayList<>();
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();

            String sql = String.format("select a.detectionid,\n"
                    + "       a.triggerid,\n"
                    + "       b.detection_statistic,\n"
                    + "       b.time,\n"
                    + "       b.signal_duration,\n"
                    + "       c.detectortype\n"
                    + "  from %s a, %s b, %s c\n"
                    + " where a.runid = ?\n"
                    + " and a.detectorid = ?\n"
                    + "   and a.triggerid = b.triggerid\n"
                    + "   and a.detectorid = c.detectorid\n"
                    + "   and detection_statistic >= ?\n"
                    + "   order by time",
                    TableNames.getDetectionTable(),
                    TableNames.getTriggerRecordTable(),
                    TableNames.getDetectorTable());

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                stmt.setInt(2, detectorid);
                stmt.setDouble(3, detStatThreshold);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int detectionid = rs.getInt(jdx++);
                        int triggerid = rs.getInt(jdx++);
                        double detectionStatistic = rs.getDouble(jdx++);
                        double time = rs.getDouble(jdx++);
                        double duration = rs.getDouble(jdx++);
                        String tmp = rs.getString(jdx++);
                        DetectorType detType = DetectorType.valueOf(tmp);
                        result.add(new DetectionSummary(detectionid, triggerid, runid, detectorid, detectionStatistic, time, duration, detType));
                    }
                }

            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return result;
    }

    private Detection detectionFromTriggerP(Trigger trigger) throws SQLException {
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            Detection existing = getExistingDetection(trigger, conn);
            if (existing != null) {
                return existing;
            } else {
                String sql = String.format("insert into %s select %s.nextval, ?, ?, ? from dual",
                        TableNames.getDetectionTable(),
                        SequenceNames.getDetectionidSequenceName());

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, trigger.getRunid());
                    stmt.setInt(2, trigger.getTriggerid());
                    stmt.setInt(3, trigger.getDetectorid());
                    stmt.execute();
                    setTriggerProcessingStatus(trigger.getTriggerid(), true, false, conn);
                    conn.commit();
                    int detectionid = (int) OracleDBUtil.getIdCurrVal(conn, SequenceNames.getDetectionidSequenceName());
                    return new Detection(detectionid, trigger);
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void setTriggerProcessingStatus(int triggerid, boolean processed,
            boolean rejected, Connection conn) throws SQLException {
        String sql = String.format("update %s set processed = ?, rejected = ? where triggerid = ?",
                TableNames.getTriggerRecordTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, processed ? "y" : "n");
            stmt.setString(2, rejected ? "y" : "n");
            stmt.setInt(3, triggerid);
            stmt.execute();
        }
    }

    private Detection getExistingDetection(Trigger trigger, Connection conn) throws SQLException {
        String sql = String.format("select detectionid from %s where triggerid = ?",
                TableNames.getDetectionTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trigger.getTriggerid());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int detectionid = rs.getInt(1);
                    return new Detection(detectionid, trigger);
                }
                return null;
            }
        }
    }

    private void reassignDetectionP(Detection detection, DetectorSubstitution substitute)
            throws SQLException {
        int oldTriggerid = detection.getTriggerid();
        int detectorid = substitute.getDetector().getdetectorid();
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            TimeStamp newTriggerTime = new TimeStamp(detection.getTriggerTime().epochAsDouble() + substitute.getShift());
            int newTriggerid = createTriggerRecord(conn, detection.getRunid(),
                    detectorid, newTriggerTime, substitute.getStatisticValue(), true, false, substitute.getSrcDetectorid(), oldTriggerid, substitute.getSubstitutionReason(), detection.getSignalDuration());
            setOldTriggerRejected(conn, oldTriggerid);
            updateDetection(conn, detection.getDetectionid(), newTriggerid,
                    detectorid);
            copyTriggerChildRecords(conn, oldTriggerid, newTriggerid);
            conn.commit();
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

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
                TableNames.getTriggerDataFeatureTable(), TableNames.getTriggerDataFeatureTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newTriggerid);
            stmt.setInt(2, oldTriggerid);
            stmt.execute();
        }
    }

    private void copyTriggerFkDataRow(Connection conn, int oldTriggerid, int newTriggerid) throws SQLException {
        String sql = String.format("insert into %s select ?, fk_qual, back_azimuth, velocity, sx, sy from %s where triggerid = ?",
                TableNames.getTriggerFkDataTable(), TableNames.getTriggerFkDataTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newTriggerid);
            stmt.setInt(2, oldTriggerid);
            stmt.execute();
        }
    }

    private static void updateDetection(Connection conn, int detectionid,
            int newTriggerid, int detectorid) throws SQLException {
        String sql = String.format("update %s set triggerid = ?, detectorid = ? where detectionid = ?",
                TableNames.getDetectionTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newTriggerid);
            stmt.setInt(2, detectorid);
            stmt.setInt(3, detectionid);
            stmt.execute();
        }
    }

    private static void setOldTriggerRejected(Connection conn, int triggerid)
            throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("update trigger_record set rejected = 'y' where triggerid = ?")) {
            stmt.setInt(1, triggerid);
            stmt.execute();
        }

    }

    private int createTriggerRecord(Connection conn,
            int runid,
            int detectorid,
            TimeStamp triggerTime,
            double detectionStatistic,
            boolean processed,
            boolean rejected,
            int srcDetectorid,
            int srcTriggerid,
            SubstitutionReason reason,
            double signalDuration) throws SQLException {
        String sql = String.format("insert into %s select %s.nextval, ?,?,?,?,?,?,?,?,?,? from dual",
                TableNames.getTriggerRecordTable(),
                SequenceNames.getTriggeridSequenceName());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, runid);
            stmt.setInt(2, detectorid);
            stmt.setDouble(3, triggerTime.epochAsDouble());
            stmt.setDouble(4, detectionStatistic);
            stmt.setString(5, processed ? "y" : "n");
            stmt.setString(6, rejected ? "y" : "n");
            stmt.setInt(7, srcDetectorid);
            stmt.setInt(8, srcTriggerid);
            stmt.setString(9, reason.toString());
            stmt.setDouble(10, signalDuration);
            stmt.execute();
            return (int) OracleDBUtil.getIdCurrVal(conn, SequenceNames.getTriggeridSequenceName());
        }
    }
}
