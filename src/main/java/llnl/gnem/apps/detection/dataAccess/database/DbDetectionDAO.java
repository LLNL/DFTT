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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.DetectionObjects;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Trigger;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ShortDetectionSummary;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectionDAO;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.ClassifiedDetection;
import llnl.gnem.apps.detection.util.DetectorSubstitution;
import llnl.gnem.apps.detection.util.TimeStamp;
import llnl.gnem.apps.detection.dataAccess.dataobjects.SubstitutionReason;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.PairT;

public abstract class DbDetectionDAO implements DetectionDAO {

    @Override
    public abstract Detection detectionFromTrigger(Trigger trigger) throws DataAccessException;

    @Override
    public abstract void reassignDetection(Detection detection, DetectorSubstitution substitute) throws DataAccessException;

    @Override
    public Collection<ShortDetectionSummary> getDetectionsInTimeInterval(int configid, Epoch epoch) throws DataAccessException {
        try {
            return getDetectionsInTimeIntervalP(configid, epoch);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<ClassifiedDetection> getDetections(int runid, int detectorid) throws DataAccessException {
        try {
            return getDetectionsP(runid, detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Detection getSingleDetection(int detectionid) throws DataAccessException {
        try {
            return getSingleDetectionP(detectionid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public DetectionObjects getDetectionObjects(int runid, int detectorid, boolean retrieveByBlocks, int blockSize, int lastRetrievedDetectionId) throws DataAccessException {
        try {
            return getDetectionObjectsP(runid, detectorid, retrieveByBlocks, blockSize, lastRetrievedDetectionId);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public TriggerDataFeatures getTriggerDataFeatures(int detectionid) throws DataAccessException {
        try {
            return getTriggerDataFeaturesP(detectionid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void deleteDetection(Detection detection) throws DataAccessException {
        try {
            deleteDetectionP(detection);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void deleteDetections(ArrayList<Integer> detectionIdValues) throws DataAccessException {
        try {
            deleteDetectionsP(detectionIdValues);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<String> reportAllDetections(int runid) throws DataAccessException {
        try {
            return reportAllDetectionsP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<String> reportDetectionSummary(int runid)throws DataAccessException {
        try {
            return reportDetectionSummaryP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    
    private Collection<String> reportDetectionSummaryP(int runid) throws SQLException {
        Connection conn = null;
        Collection<String> result = new ArrayList<>();
        String sql = String.format("select detectorid, count(*) from %s where runid = ? group by detectorid order by count(*)",
                TableNames.getDetectionTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, runid);
            try(ResultSet rs = stmt.executeQuery()){
            while (rs.next()) {
                int detectorid = rs.getInt(1);
                int count = rs.getInt(2);
                String statsLine = String.format(" Detectorid=%d, Detection count = %d", detectorid, count);
                result.add(statsLine);
            }
            }  }} finally {
           
                DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
            
        }
return result;
    }

    private Collection<String> reportAllDetectionsP(int runid) throws SQLException {
        Connection conn = null;

        Collection<String> result = new ArrayList<>();
        String sql = String.format("select a.detectionid, a.detectorid,c.detectortype,b.time,b.detection_statistic "
                + "from %s a, %s b, %s c where a.runid = ? and a.\n"
                + "triggerid = b.triggerid and b.runid = a.runid and a.detectorid = c.detectorid order by time",
                TableNames.getDetectionTable(),
                TableNames.getTriggerRecordTable(),
                TableNames.getDetectorTable());
        try {

            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int detectionid = rs.getInt(1);
                        int detectorid = rs.getInt(2);
                        DetectorType type = DetectorType.valueOf(rs.getString(3));
                        double time = rs.getDouble(4);
                        String timeString = new TimeStamp(time).toString();
                        float statistic = rs.getFloat(5);
                        String statsLine = String.format("Detectionid=%d, Detectorid=%d, detector Type = %s, Detection Time = %s, Detection Statistic = %10.4f",
                                detectionid, detectorid, type, timeString, statistic);
                        result.add(statsLine);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return result;
    }

    private Collection<ShortDetectionSummary> getDetectionsInTimeIntervalP(int configid, Epoch epoch) throws SQLException {
        Collection<ShortDetectionSummary> result = new ArrayList<>();
        String sql = String.format("select a.detectorid, a.detectionid, time, detection_statistic\n"
                + "  from %s a, %s b\n"
                + " where a.detectionid in\n"
                + "       (select detectionid\n"
                + "          from %s a, %s b, %s c\n"
                + "         where configid = ?\n"
                + "           and a.runid = b.runid\n"
                + "           and b.triggerid = c.triggerid\n"
                + "           and time between ? and ?)\n"
                + "   and a.triggerid = b.triggerid",
                TableNames.getDetectionTable(),
                TableNames.getTriggerRecordTable(),
                TableNames.getFrameworkRunTable(),
                TableNames.getDetectionTable(),
                TableNames.getTriggerRecordTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                stmt.setDouble(2, epoch.getStart());
                stmt.setDouble(3, epoch.getEnd());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int detectorid = rs.getInt(jdx++);
                        int detectionid = rs.getInt(jdx++);
                        double time = rs.getDouble(jdx++);
                        double detectionStatistic = rs.getDouble(jdx++);
                        result.add(new ShortDetectionSummary(detectorid, detectionid, time, detectionStatistic));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<ClassifiedDetection> getDetectionsP(int runid, int detectorid) throws SQLException {
        Collection<ClassifiedDetection> result = new ArrayList<>();
        Connection conn = null;

        int rawTriggerIndex = -1; // For now assuming this is unimportant in this context.
        String sql = String.format("select a.triggerid,  \n"
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
                + "%s a, \n"
                + "%s b, \n"
                + "%s c ,\n"
                + "%s d\n"
                + "where a.runid = ? \n"
                + "and a.detectorid = ? \n"
                + "and a.triggerid = b.triggerid \n"
                + "and a.detectorid = c.detectorid \n"
                + "and a.triggerid = d.triggerid(+)\n"
                + "order by time",
                TableNames.getTriggerRecordTable(),
                TableNames.getDetectionTable(),
                TableNames.getDetectorTable(),
                TableNames.getTriggerClassificationTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
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
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Detection getSingleDetectionP(int detectionid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select a.triggerid,  "
                + "c.detectortype, a.detection_statistic,a.time, a.processed, "
                + "a.rejected,a.src_detectorid, a.src_triggerid, a.substitution_reason, "
                + "a.signal_duration, a.runid, a.detectorid from %s a, %s b, %s c "
                + "where b.detectionid = ? "
                + "and a.triggerid = b.triggerid "
                + "and a.detectorid = c.detectorid",
                TableNames.getTriggerRecordTable(),
                TableNames.getDetectionTable(),
                TableNames.getDetectorTable());
        int rawTriggerIndex = -1; // For now assuming this is unimportant in this context.
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectionid);
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
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
        return null;
    }

    private DetectionObjects getDetectionObjectsP(int runid, int detectorid, boolean retrieveByBlocks, int blockSize, int lastRetrievedDetectionId) throws SQLException {
        Map<Integer, TriggerClassification> triggerClassificationMap = new HashMap<>();
        Collection<PairT<Integer, Double>> detTimes = new ArrayList<>();
        Collection<Double> U = new ArrayList<>();
        Connection conn = null;
        String restrictionClause = retrieveByBlocks ? " and detectionid > ? " : "";
        int rowsRetrieved = 0;
        int maxDetectionId = 0;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("select /*+ parallel(12) ordered use_nl(b,c) */ distinct "
                    + "detectionid, "
                    + "time, "
                    + "artifact_status, "
                    + "usability_status "
                    + "from %s a, %s b, %s c "
                    + "where a.runid = ? "
                    + "and a.detectorid = ? %s"
                    + "and a.triggerid = b.triggerid and a.triggerid = c.triggerid(+)"
                    + "and detection_statistic between ? and ? order by detectionid",
                    TableNames.getDetectionTable(),
                    TableNames.getTriggerRecordTable(),
                    TableNames.getTriggerClassificationTable(),
                    restrictionClause);
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
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private TriggerDataFeatures getTriggerDataFeaturesP(int detectionid) throws SQLException {
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("select SNR,"
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
                    + "FREQ_CENTROID from %s a, %s b"
                    + " where detectionid = ? and a.triggerid = b.triggerid",
                    TableNames.getDetectionTable(),
                    TableNames.getTriggerDataFeatureTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectionid);
                try (ResultSet rs = stmt.executeQuery()) {
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
                }
            }
            return null;
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void deleteDetectionP(Detection detection) throws SQLException {
        Connection conn = null;
        String sql = String.format("delete from %s where detectionid = ?",
                TableNames.getDetectionTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detection.getDetectionid());

                stmt.execute();
                deleteTrigger(detection.getTriggerid(), conn);
                conn.commit();
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private void deleteTrigger(int triggerid, Connection conn) throws SQLException {
        String sql = String.format("delete from %s where triggerid = ?", TableNames.getTriggerRecordTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, triggerid);
            stmt.execute();
        }
    }

    private void deleteDetectionsP(ArrayList<Integer> detectionIdValues) throws SQLException {
        Connection conn = null;

        String sql = String.format("delete from %s where triggerid in "
                + "(select triggerid from %s where detectionid = ?)",
                TableNames.getTriggerRecordTable(),
                TableNames.getDetectionTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Integer detectionid : detectionIdValues) {
                    stmt.setInt(1, detectionid);
                    stmt.execute();
                }
            }
            conn.commit();

        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

}
