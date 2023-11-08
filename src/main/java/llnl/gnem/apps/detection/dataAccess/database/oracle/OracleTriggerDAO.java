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
package llnl.gnem.apps.detection.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.classify.LabeledFeature;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Trigger;
import llnl.gnem.apps.detection.core.framework.FKScreenResults;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.database.DbTriggerDAO;
import llnl.gnem.apps.detection.dataAccess.database.SequenceNames;
import llnl.gnem.apps.detection.dataAccess.database.TableNames;
import llnl.gnem.apps.detection.triggerProcessing.EvaluatedTrigger;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.SubstitutionReason;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;

/**
 *
 * @author dodge1
 */
public class OracleTriggerDAO extends DbTriggerDAO {

    private OracleTriggerDAO() {
    }

    public static OracleTriggerDAO getInstance() {
        return OracleTriggerDAOHolder.INSTANCE;
    }

    @Override
    public void createSubstituteTrigger(int existingTriggerid, String substitutionReason, int newDetectorid, double windowCorrection, double newWindowLength) throws DataAccessException {
        try {
            createSubstituteTriggerP(existingTriggerid, substitutionReason, newDetectorid, windowCorrection, newWindowLength);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static class OracleTriggerDAOHolder {

        private static final OracleTriggerDAO INSTANCE = new OracleTriggerDAO();
    }

    @Override
    public Trigger writeNewTrigger(EvaluatedTrigger evaluatedTrigger) throws DataAccessException {
        try {
            return writeNewTriggerP(evaluatedTrigger);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void markAsCoincident(Trigger trigger) throws DataAccessException {
        try {
            markAsCoincidentP(trigger);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Trigger writeNewTriggerP(EvaluatedTrigger evaluatedTrigger) throws SQLException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            int runid = RunInfo.getInstance().getRunid();

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
            String sql = String.format("insert into %s values( %s.nextval, ?, ?, ?, ?, 'n','n', ?, ?, ?, ? )",
                    TableNames.getTriggerRecordTable(),
                    SequenceNames.getTriggeridSequenceName());

            SubstitutionReason reason = getNoSubStitutionReason(type);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int jdx = 1;
                stmt.setInt(jdx++, runid);
                stmt.setInt(jdx++, detectorid);
                stmt.setDouble(jdx++, triggerTime);
                stmt.setDouble(jdx++, maxDetStat);
                stmt.setInt(jdx++, detectorid);
                stmt.setNull(jdx++, Types.INTEGER);
                stmt.setString(jdx++, reason.toString());
                stmt.setDouble(jdx++, signalDuration);
                stmt.execute();
                int triggerid = (int) OracleDBUtil.getIdCurrVal(conn, SequenceNames.getTriggeridSequenceName());
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
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private SubstitutionReason getNoSubStitutionReason(DetectorType type) {
        if (type == DetectorType.SUBSPACE) {
            return SubstitutionReason.NOT_SUBSTITUTED;
        } else {
            return SubstitutionReason.PRIMARY_DETECTOR;
        }
    }

    private void writeTriggerFeatures(int triggerid, LabeledFeature fc, double relativeAmplitude, Connection conn) throws SQLException {

        String sql = String.format("insert into %s values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                TableNames.getTriggerDataFeatureTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            double snr = Math.sqrt(fc.getSnr());// 
            int idx = 1;
            stmt.setInt(idx++, triggerid);
            stmt.setDouble(idx++, screenDouble(snr));
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

    private void insertFKResults(int triggerid, FKScreenResults fkResults, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?,?,?)",
                TableNames.getTriggerFkDataTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, triggerid);

            stmt.setDouble(2, fkResults.getQuality());
            stmt.setDouble(3, fkResults.getAzimuth());
            stmt.setDouble(4, fkResults.getVelocity());
            stmt.setDouble(5, fkResults.getSx());
            stmt.setDouble(6, fkResults.getSy());
            stmt.execute();
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

    private void markAsCoincidentP(Trigger trigger) throws SQLException {
        String sql = String.format("update %s set processed = 'y', rejected = 'y' where triggerid = ?",
                TableNames.getTriggerRecordTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, trigger.getTriggerid());
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void createSubstituteTriggerP(int existingTriggerid, String substitutionReason, int newDetectorid, double windowCorrection, double newWindowLength) throws SQLException {
        String sql = String.format("update %s set rejected = 'y', substitution_reason = ? where triggerid = ?",
                TableNames.getTriggerRecordTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, substitutionReason);
                stmt.setInt(2, existingTriggerid);
                stmt.execute();
            }
            sql = String.format("insert into %s select %s.nextval, runid, %d, time + %f,detection_statistic,processed, 'n',detectorid,triggerid,'NOT_SUBSTITUTED',%f from %s where triggerid = ?",
                    TableNames.getTriggerRecordTable(),
                    SequenceNames.getTriggeridSequenceName(),
                    newDetectorid,
                    windowCorrection,
                    newWindowLength,
                    TableNames.getTriggerRecordTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, existingTriggerid);
                stmt.execute();
            }
            sql = String.format("update %s set triggerid = %s.currval, detectorid = ? where triggerid = ?",
                    TableNames.getDetectionTable(),
                    SequenceNames.getTriggeridSequenceName());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newDetectorid);
                stmt.setInt(2, existingTriggerid);
                stmt.execute();
            }
            conn.commit();
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

}
