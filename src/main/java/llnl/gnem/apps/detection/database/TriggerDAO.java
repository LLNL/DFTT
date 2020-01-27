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
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.Trigger;
import llnl.gnem.apps.detection.core.framework.FKScreenResults;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.util.SubstitutionReason;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.classify.LabeledFeature;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.triggerProcessing.EvaluatedTrigger;
import llnl.gnem.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.core.database.ConnectionManager;

/**
 *
 * @author dodge1
 */
public class TriggerDAO {

    private TriggerDAO() {
    }

    public synchronized static TriggerDAO getInstance() {
        return TriggerDAOHolder.INSTANCE;
    }

    void deleteTrigger(int triggerid, Connection conn) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement("delete from trigger_record where triggerid = ?")) {
            stmt.setInt(1, triggerid);
            stmt.execute();
            conn.commit();
        }
    }

    private void insertFKResults(int triggerid, FKScreenResults fkResults, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "insert into trigger_fk_data values (?,?,?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, triggerid);

            stmt.setDouble(2, fkResults.getQuality());
            stmt.setDouble(3, fkResults.getAzimuth());
            stmt.setDouble(4, fkResults.getVelocity());
            stmt.setDouble(5, fkResults.getSx());
            stmt.setDouble(6, fkResults.getSy());
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public Trigger writeNewTrigger(EvaluatedTrigger evaluatedTrigger) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            int runid = RunInfo.getInstance().getRunid();
            conn = ConnectionManager.getInstance().checkOut();

            DetectorSpecification spec = evaluatedTrigger.getTriggerData().getDetectorInfo().getSpecification();
            int detectorid = evaluatedTrigger.getTriggerData().getDetectorInfo().getDetectorid();
            double tmp = Math.min(evaluatedTrigger.getSignalDuration(), evaluatedTrigger.getFixedDurationSeconds());
            double signalDuration = evaluatedTrigger.isForceFixedDuration() ? evaluatedTrigger.getFixedDurationSeconds() : tmp;
            if (spec instanceof SubspaceSpecification) {
                SubspaceSpecification sspec = (SubspaceSpecification) spec;
                signalDuration = Math.min(signalDuration, sspec.getWindowDurationSeconds());
            }

            double triggerTime = evaluatedTrigger.getTriggerData().getTriggerTime().epochAsDouble();
            double maxDetStat = evaluatedTrigger.getTriggerData().getStatistic();
            int rawTriggerIndex = evaluatedTrigger.getTriggerData().getIndex();
            DetectorType type = evaluatedTrigger.getTriggerData().getDetectorInfo().getDetectorType();
            stmt = conn.prepareStatement("insert into trigger_record values( triggerid.nextval, ?, ?, ?, ?, 'n','n', ?, ?, ?, ? )");
            SubstitutionReason reason = getNoSubStitutionReason(type);
            stmt.setInt(1, runid);
            stmt.setInt(2, detectorid);
            stmt.setDouble(3, triggerTime);
            stmt.setDouble(4, maxDetStat);
            stmt.setInt(5, detectorid);
            stmt.setNull(6, Types.INTEGER);
            stmt.setString(7, reason.toString());
            stmt.setDouble(8, signalDuration);
            stmt.execute();
            int triggerid = (int) OracleDBUtil.getIdCurrVal(conn, "triggerid");
            double relativeAmplitude = evaluatedTrigger.getRelativeAmplitude();

            LabeledFeature feature = evaluatedTrigger.getFeatures();
            Trigger trigger = new Trigger(triggerid, runid, detectorid,
                    type, (float) maxDetStat, evaluatedTrigger.getTriggerData().getTriggerTime(), false,
                    false, detectorid, -1, reason, signalDuration, rawTriggerIndex,
                    feature.getTimeCentroid());
            writeTriggerFeatures(trigger.getTriggerid(), feature, relativeAmplitude, conn);

            FKScreenResults fkResults = evaluatedTrigger.getFKScreenResults();
            if (fkResults != null) {
                insertFKResults(trigger.getTriggerid(), fkResults, conn);
            }

            if (!evaluatedTrigger.isUsable()) {
                setTriggerProcessingStatus(trigger.getTriggerid(), true, true, conn);
            }
            conn.commit();
            return trigger;

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public void removeAnyDuplicates(int runid) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            conn = ConnectionManager.getInstance().checkOut();
            conn.prepareStatement("delete from trigger_record where triggerid in ("
                    + "select a.triggerid from trigger_record a, trigger_record b where a.runid = ? "
                    + "and a.runid = b.runid "
                    + "and a.detectorid = b.detectorid "
                    + "and a.time = b.time "
                    + "and a.triggerid < b.triggerid)");
            stmt.setInt(1, runid);

            stmt.execute();
            conn.commit();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);

        }
    }

    private static class TriggerDAOHolder {

        private static final TriggerDAO INSTANCE = new TriggerDAO();
    }

    private SubstitutionReason getNoSubStitutionReason(DetectorType type) {
        if (type == DetectorType.SUBSPACE) {
            return SubstitutionReason.NOT_SUBSTITUTED;
        } else {
            return SubstitutionReason.PRIMARY_DETECTOR;
        }
    }

    public void deleteTrigger(Trigger trigger) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("delete from trigger_record where triggerid = ?");
            stmt.setInt(1, trigger.getTriggerid());
            stmt.execute();
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

    public double getTriggerTime(int triggerid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select  time from trigger_record where triggerid = ?");
            stmt.setInt(1, triggerid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getDouble(1);
            }
            throw new IllegalStateException("No trigger_record for triggerid: " + triggerid);
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

    public void markAsCoincident(Trigger trigger) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("update trigger_record set processed = 'y', rejected = 'y' where triggerid = ?");
            stmt.setInt(1, trigger.getTriggerid());
            stmt.execute();
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

    public void setTriggerProcessingStatus(int triggerid, boolean processed,
            boolean rejected, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("update trigger_record set processed = ?, rejected = ? where triggerid = ?")) {
            stmt.setString(1, processed ? "y" : "n");
            stmt.setString(2, rejected ? "y" : "n");
            stmt.setInt(3, triggerid);
            stmt.execute();
            conn.commit();
        }
    }

    public void writeTriggerFeatures(int triggerid, LabeledFeature fc, double relativeAmplitude, Connection conn) throws SQLException {

        String sql = String.format("insert into trigger_data_feature values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, triggerid);
            stmt.setDouble(idx++, screenDouble(fc.getSnr()));
            stmt.setDouble(idx++, screenDouble(fc.getAmplitude()));
            stmt.setDouble(idx++, screenDouble(fc.getTimeCentroid()));
            stmt.setDouble(idx++, screenDouble(fc.getTimeSigma()));
            stmt.setDouble(idx++, screenDouble(fc.getTemporalSkewness()));
            stmt.setDouble(idx++, screenDouble(fc.getTemporalKurtosis()));
            stmt.setDouble(idx++, screenDouble(fc.getFreqSigma()));
            stmt.setDouble(idx++, screenDouble(fc.getTbp()));
            stmt.setDouble(idx++, screenDouble(fc.getSkewness()));
            stmt.setDouble(idx++, screenDouble(fc.getKurtosis()));
            stmt.setDouble(idx++, screenDouble(fc.getRawSkewness()));
            stmt.setDouble(idx++, screenDouble(fc.getRawKurtosis()));
            stmt.setDouble(idx++, screenDouble(fc.getFreqCentroid()));
            stmt.setDouble(idx++, screenDouble(relativeAmplitude));
            stmt.execute();

        }

    }

    private Double screenDouble(Double value) {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return -9999999.99;
        } else {
            return value;
        }
    }

    public int createTriggerRecord(Connection conn,
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
        try (PreparedStatement stmt = conn.prepareStatement("insert into trigger_record select triggerid.nextval, ?,?,?,?,?,?,?,?,?,? from dual")) {
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
            return (int) OracleDBUtil.getIdCurrVal(conn, "triggerid");
        }
    }

    public double getAverageSignalDuration(int runid, int detectorid, Connection conn) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement("select /*+ parallel(24) */ avg(signal_duration) from trigger_record where runid = ? and detectorid = ? and rejected = 'n'")) {

            stmt.setInt(1, runid);
            stmt.setInt(2, detectorid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return rs.getDouble(1);
                }
                return -1;
            }
        }
    }

    public Collection<Double> getFeatureValues(int runid, String columnName) throws SQLException {
        Collection<Double> result = new ArrayList<>();
        String sql = String.format("select %s from trigger_record a, "
                + "trigger_data_feature b where runid = ? and a.triggerid = b.triggerid", columnName);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, runid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getDouble(1));
            }
            return result;
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

}
