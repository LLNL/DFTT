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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.TemplateNormalization;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.SubspaceTemplateDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.StreamKey;
import oracle.sql.BLOB;

/**
 *
 * @author dodge1
 */
public abstract class DbSubspaceTemplateDAO implements SubspaceTemplateDAO {

    @Override
    public SubspaceTemplate getSubspaceTemplate(int detectorid) throws DataAccessException {
        try {
            return getSubspaceTemplateP(detectorid);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public EmpiricalTemplate getEmpiricalTemplate(int detectorid) throws DataAccessException {
        return getSubspaceTemplate(detectorid);
    }

    @Override
    public ArrayCorrelationTemplate getArrayCorrelationTemplate(int detectorid) throws DataAccessException {
        try {
            return getArrayCorrelationTemplateP(detectorid);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void saveTemplateData(int detectorid, EmpiricalTemplate template, TemplateNormalization normalization) throws DataAccessException {
        try {
            saveTemplateDataP(detectorid, template, normalization);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void updateTemplateData(int detectorid, EmpiricalTemplate template, TemplateNormalization normalization) throws DataAccessException {
        try {
            updateTemplateDataP(detectorid, template, normalization);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private SubspaceTemplate getSubspaceTemplateP(int detectorid) throws Exception {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();

            Object tmpObj = getTemplate(conn, detectorid);
            SubspaceTemplate tmp = (SubspaceTemplate) tmpObj;
            if (tmp.getSpecification() == null) {
                SubspaceSpecification spec = retrieveSubspaceSpecification(detectorid, conn);
                tmp = new SubspaceTemplate(tmp, spec);
            }
            return tmp;
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void updateTemplateDataP(int detectorid, EmpiricalTemplate template, TemplateNormalization normalization) throws Exception {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            removeExistingTemplate(detectorid, conn);
            conn.commit();
            saveTemplateData(detectorid, template, normalization);
            conn.commit();
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }


    private void removeExistingTemplate(int detectorid, Connection conn) throws SQLException {
        String sql = String.format("delete from %s where detectorid = ?",
                TableNames.getSubspaceTemplateTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private void saveTemplateDataP(int detectorid, EmpiricalTemplate template, TemplateNormalization normalization) throws Exception {
        Connection conn = null;
        String sql = String.format("insert into %s values (?,empty_blob(),?,?)",
                TableNames.getSubspaceTemplateTable());

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement insertTemplateStmt = conn.prepareStatement(sql)) {
                insertTemplateStmt.setInt(1, detectorid);
                insertTemplateStmt.setString(2, normalization.toString());
                Double tbp = template.getTemplateTBP();
                if (tbp != null) {
                    insertTemplateStmt.setDouble(3, tbp);
                } else {
                    insertTemplateStmt.setNull(3, Types.DOUBLE);
                }
                insertTemplateStmt.execute();
                writeIntoTemplateBlob(detectorid, conn, template);
                conn.commit();

            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void writeIntoTemplateBlob(int detectorid, Connection conn, Object template) throws SQLException, IOException {

        String sql = String.format("select template from %s where detectorid = ?",
                TableNames.getSubspaceTemplateTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BLOB blob = (BLOB) rs.getBlob(1);
                    try (OutputStream os = blob.getBinaryOutputStream()) {
                        try (ObjectOutputStream oop = new ObjectOutputStream(os)) {
                            oop.writeObject(template);
                            oop.flush();
                        }
                    }
                } else {
                    throw new IllegalStateException("Failed to write template data for detector!");
                }
            }
        }

    }

    private ArrayCorrelationTemplate getArrayCorrelationTemplateP(int detectorid) throws Exception {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            return (ArrayCorrelationTemplate) getTemplate(conn, detectorid);
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private Object getTemplate(Connection conn, int detectorid) throws ClassNotFoundException, SQLException, IllegalStateException, IOException {

        String sql = String.format("select template from %s where detectorid = ?",
                TableNames.getSubspaceTemplateTable());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return DetectorUtil.getBlobObject(rs);
                } else {
                    throw new IllegalStateException("No template found for detectorid: " + detectorid);
                }

            }
        }
    }

    private SubspaceSpecification retrieveSubspaceSpecification(int detectorid, Connection conn) throws Exception {
        String sql = String.format("select threshold,\n"
                + "       blackout_seconds,\n"
                + "       sta_duration, "
                + "       lta_duration, "
                + "       gap_duration\n"
                + "  from %s a, %s b\n"
                + "  where a.detectorid = ?"
                + "   and a.detectorid = b.detectorid ",
                TableNames.getDetectorTable(),
                TableNames.getSubspaceDetectorParamsTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idx = 1;
                    double threshold = rs.getDouble(idx++);
                    double blackoutSeconds = rs.getDouble(idx++);
                    double staDuration = rs.getDouble(idx++);
                    double ltaDuration = rs.getDouble(idx++);
                    double gapDuration = rs.getDouble(idx++);

                    ArrayList< StreamKey> channels = DetectorUtil.getDetectorChannels(conn, detectorid);

                    return new SubspaceSpecification(
                            (float) threshold,
                            (float) blackoutSeconds,
                            (float) staDuration,
                            (float) ltaDuration,
                            (float) gapDuration,
                            channels);

                }
                return null;
            }
        }
    }

}
