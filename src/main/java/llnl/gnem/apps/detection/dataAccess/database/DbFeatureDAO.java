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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.classify.DbLabeledFeature;
import llnl.gnem.apps.detection.classify.LabeledFeature;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.FeatureDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.ConnectionManager;

/**
 *
 * @author dodge1
 */
public abstract class DbFeatureDAO implements FeatureDAO {

    @Override
    public void writeClassification(int triggerid,
            LabeledFeature.Status artifactStatus,
            LabeledFeature.Status usabilityStatus) throws DataAccessException {
        try {
            writeClassificationP(triggerid,
                    artifactStatus,
                    usabilityStatus);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void writeDetectorTrainingDataRow(int detectorid, String status) throws DataAccessException {
        try {
            writeDetectorTrainingDataRowP(detectorid, status);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public List<LabeledFeature> getTrainingDataForStream(int streamid, ClassificationType type) throws DataAccessException {
        try {
            return getTrainingDataForStreamP(streamid, type);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public List<LabeledFeature> getAllLabeledFeatures(int runid, ClassificationType type) throws DataAccessException {
        try {
            return getAllLabeledFeaturesP(runid, type);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public List<DbLabeledFeature> getAllUnLabeledFeatures(int runid) throws DataAccessException {
        try {
            return getAllUnLabeledFeaturesP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Map<Integer, TriggerClassification> getClassificationFromTrainingData(int runid) throws DataAccessException {
        try {
            return getClassificationFromTrainingDataP(runid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Map<Integer, TriggerClassification> getDetectorTrainingData(int streamid) throws DataAccessException {
        try {
            return getDetectorTrainingDataP(streamid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Map<Integer, TriggerClassification> getDetectorTrainingDataP(int streamid) throws SQLException {
        Map<Integer, TriggerClassification> result = new HashMap<>();
        String sql = String.format("select b.detectorid, status from %s a, %s b\n"
                + "where streamid = ?\n"
                + "and a.detectorid = b.detectorid",
                TableNames.getDetectorTable(),
                TableNames.getDetectorTrainingDataTable());
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, streamid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int detectorid = rs.getInt(jdx++);
                        String status = rs.getString(jdx++);
                        TriggerClassification tc = TriggerClassification.createFromSingleStatusString(status);
                        result.put(detectorid, tc);
                    }
                    return result;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void writeClassificationP(int triggerid,
            LabeledFeature.Status artifactStatus,
            LabeledFeature.Status usabilityStatus) throws SQLException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("merge into %s a using\n"
                    + "(select ? triggerid, ? artifact_status, ? usability_status from dual) b\n"
                    + "on(a.triggerid = b.triggerid) when matched "
                    + "then update set a.artifact_status = b.artifact_status, a.usability_status = b.usability_status \n"
                    + "when not matched then\n"
                    + "insert (triggerid,artifact_status, usability_status) "
                    + "values(b.triggerid,b.artifact_status,b.usability_status)",
                    TableNames.getTriggerClassificationTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, triggerid);
                stmt.setString(2, artifactStatus.name());
                stmt.setString(3, usabilityStatus.name());
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void writeDetectorTrainingDataRowP(int detectorid, String status) throws SQLException {
        Connection conn = null;
        String sql = String.format("merge into %s a using\n"
                + "(select ? detectorid, ? status from dual) b\n"
                + "on(a.detectorid = b.detectorid) when matched "
                + "then update set a.status = b.status \n"
                + "when not matched then\n"
                + "insert (detectorid,status) "
                + "values(b.detectorid,b.status)",
                TableNames.getDetectorTrainingDataTable());

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                stmt.setString(2, status);
                stmt.execute();
                conn.commit();
            }
            sql = String.format("merge into %s aa\n"
                    + "using (select triggerid,\n"
                    + "              decode(status, 'b', 'invalid', 'valid') artifact_status,\n"
                    + "              case status\n"
                    + "                when 'g' then\n"
                    + "                 'valid'\n"
                    + "                when 'b' then\n"
                    + "                 'invalid'\n"
                    + "                when 'l' then\n"
                    + "                 'local'\n"
                    + "                when 'r' then\n"
                    + "                 'regional'\n"
                    + "                when 'e' then\n"
                    + "                 'teleseismic'\n"
                    + "                when 'u' then\n"
                    + "                 'invalid'\n"
                    + "              end as usability_status\n"
                    + "         from %s a, %s b\n"
                    + "        where a.detectorid = ?\n"
                    + "          and a.detectorid = b.detectorid) bb\n"
                    + "on (aa.triggerid = bb.triggerid)\n"
                    + "when matched then\n"
                    + "  update\n"
                    + "     set aa.artifact_status  = bb.artifact_status,\n"
                    + "         aa.usability_status = bb.usability_status\n"
                    + "when not matched then\n"
                    + "  insert\n"
                    + "    (triggerid, artifact_status, usability_status)\n"
                    + "  values\n"
                    + "    (bb.triggerid, bb.artifact_status, bb.usability_status)",
                    TableNames.getTriggerClassificationTable(),
                    TableNames.getDetectorTrainingDataTable(),
                    TableNames.getTriggerRecordTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                stmt.execute();
                conn.commit();
            }
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private List<LabeledFeature> getTrainingDataForStreamP(int streamid, ClassificationType type) throws SQLException {
        ArrayList<LabeledFeature> result = new ArrayList<>();
        String statusClause = "('g','b')";
        if (type == ClassificationType.USABILITY_CLASSIFICATION) {
            statusClause = "('g','u')";
        }
        String sql = String.format("select /*+ ordered use_nl(a,b,c) */ \n"
                + "       snr,        \n"
                + "       AMPLITUDE, \n"
                + "       time_centroid,\n"
                + "       time_sigma,        \n"
                + "       TEMPORAL_SKEWNESS, \n"
                + "       TEMPORAL_KURTOSIS, \n"
                + "       freq_sigma,\n"
                + "       tbp,\n"
                + "       skewness,\n"
                + "       kurtosis,\n"
                + "       raw_skewness,\n"
                + "       raw_kurtosis,\n"
                + "       freq_centroid,\n"
                + "       STATUS\n"
                + "  from %s aa, \n"
                + "       %s a,        \n"
                + "       %s b,        \n"
                + "       %s c\n"
                + "       where streamid = ? and aa.detectorid = a.detectorid\n"
                + "       and STATUS IN %s \n"
                + "       and a.detectorid = b.DETECTORID\n"
                + "       and b.TRIGGERID = c.triggerid",
                TableNames.getDetectorTable(),
                TableNames.getDetectorTrainingDataTable(),
                TableNames.getDetectionTable(),
                TableNames.getTriggerDataFeatureTable(),
                statusClause);
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, streamid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int col = 1;
                        double snr = rs.getDouble(col++);
                        double amplitude = rs.getDouble(col++);
                        double timeCentroid = rs.getDouble(col++);
                        double timeSigma = rs.getDouble(col++);
                        double temporalSkewness = rs.getDouble(col++);
                        double temporalKurtosis = rs.getDouble(col++);
                        double freqSigma = rs.getDouble(col++);
                        double tbp = rs.getDouble(col++);
                        double skewness = rs.getDouble(col++);
                        double kurtosis = rs.getDouble(col++);
                        double rawSkewness = rs.getDouble(col++);
                        double rawKurtosis = rs.getDouble(col++);
                        double freqCentroid = rs.getDouble(col++);
                        String decision = rs.getString(col++);
                        result.add(new LabeledFeature(decision.equals("g"),
                                snr,
                                amplitude,
                                timeCentroid,
                                timeSigma,
                                temporalSkewness,
                                temporalKurtosis,
                                freqSigma,
                                tbp,
                                skewness,
                                kurtosis,
                                rawSkewness,
                                rawKurtosis,
                                freqCentroid));
                    }
                    return result;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private List<LabeledFeature> getAllLabeledFeaturesP(int runid, ClassificationType type) throws SQLException {
        ArrayList<LabeledFeature> result = new ArrayList<>();
        String statusClause = "('g','b')";
        if (type == ClassificationType.USABILITY_CLASSIFICATION) {
            statusClause = "('g','u')";
        }
        String sql = String.format("select snr, "
                + "       AMPLITUDE, \n"
                + "       time_centroid,\n"
                + "       time_sigma, "
                + "       TEMPORAL_SKEWNESS, \n"
                + "       TEMPORAL_KURTOSIS, \n"
                + "       freq_sigma,\n"
                + "       tbp,\n"
                + "       skewness,\n"
                + "       kurtosis,\n"
                + "       raw_skewness,\n"
                + "       raw_kurtosis,\n"
                + "       freq_centroid,\n"
                + "       STATUS\n"
                + "  from %s a, "
                + "       %s b, "
                + "       %s c\n"
                + "       where STATUS IN %s and a.detectorid = b.DETECTORID\n"
                + "       and b.TRIGGERID = c.triggerid and runid = ?",
                TableNames.getDetectorTrainingDataTable(),
                TableNames.getDetectionTable(),
                TableNames.getTriggerDataFeatureTable(),
                statusClause);
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int col = 1;
                        double snr = rs.getDouble(col++);
                        double amplitude = rs.getDouble(col++);
                        double timeCentroid = rs.getDouble(col++);
                        double timeSigma = rs.getDouble(col++);
                        double temporalSkewness = rs.getDouble(col++);
                        double temporalKurtosis = rs.getDouble(col++);
                        double freqSigma = rs.getDouble(col++);
                        double tbp = rs.getDouble(col++);
                        double skewness = rs.getDouble(col++);
                        double kurtosis = rs.getDouble(col++);
                        double rawSkewness = rs.getDouble(col++);
                        double rawKurtosis = rs.getDouble(col++);
                        double freqCentroid = rs.getDouble(col++);
                        String decision = rs.getString(col++);
                        result.add(new LabeledFeature(decision.equals("g"),
                                snr,
                                amplitude,
                                timeCentroid,
                                timeSigma,
                                temporalSkewness,
                                temporalKurtosis,
                                freqSigma,
                                tbp,
                                skewness,
                                kurtosis,
                                rawSkewness,
                                rawKurtosis,
                                freqCentroid));
                    }
                    return result;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private List<DbLabeledFeature> getAllUnLabeledFeaturesP(int runid) throws SQLException {
        ArrayList<DbLabeledFeature> result = new ArrayList<>();
        String sql = String.format("select a.triggerid,  "
                + "       snr,\n"
                + "       AMPLITUDE, \n"
                + "       time_centroid,\n"
                + "       time_sigma, "
                + "       TEMPORAL_SKEWNESS, \n"
                + "       TEMPORAL_KURTOSIS, \n"
                + "       freq_sigma,\n"
                + "       tbp,\n"
                + "       skewness,\n"
                + "       kurtosis,\n"
                + "       raw_skewness,\n"
                + "       raw_kurtosis,\n"
                + "       freq_centroid\n"
                + "  from %s a, %s b where a.triggerid = b.triggerid and b.runid = ?",
                TableNames.getTriggerDataFeatureTable(),
                TableNames.getDetectionTable());
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int col = 1;
                        int triggerid = rs.getInt(col++);
                        double snr = rs.getDouble(col++);
                        double amplitude = rs.getDouble(col++);
                        double timeCentroid = rs.getDouble(col++);
                        double timeSigma = rs.getDouble(col++);
                        double temporalSkewness = rs.getDouble(col++);
                        double temporalKurtosis = rs.getDouble(col++);
                        double freqSigma = rs.getDouble(col++);
                        double tbp = rs.getDouble(col++);
                        double skewness = rs.getDouble(col++);
                        double kurtosis = rs.getDouble(col++);
                        double rawSkewness = rs.getDouble(col++);
                        double rawKurtosis = rs.getDouble(col++);
                        double freqCentroid = rs.getDouble(col++);
                        LabeledFeature feature = new LabeledFeature(null,
                                snr,
                                amplitude,
                                timeCentroid,
                                timeSigma,
                                temporalSkewness,
                                temporalKurtosis,
                                freqSigma,
                                tbp,
                                skewness,
                                kurtosis,
                                rawSkewness,
                                rawKurtosis,
                                freqCentroid);
                        result.add(new DbLabeledFeature(triggerid, feature));
                    }
                    return result;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private Map<Integer, TriggerClassification> getClassificationFromTrainingDataP(int runid) throws SQLException {
        Map<Integer, TriggerClassification> result = new HashMap<>();
        String sql = String.format("SELECT TRIGGERID, STATUS FROM "
                + "%s A, "
                + "%s B\n"
                + "WHERE A.RUNID = ?\n"
                + "AND A.DETECTORID = B.DETECTORID",
                TableNames.getDetectionTable(),
                TableNames.getDetectorTrainingDataTable());
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int triggerid = rs.getInt(jdx++);
                        String status = rs.getString(jdx++);
                        TriggerClassification tc = TriggerClassification.createFromSingleStatusString(status);
                        result.put(triggerid, tc);
                    }
                    return result;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
}
