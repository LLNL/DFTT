/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.database;

import llnl.gnem.apps.detection.core.framework.detectors.TemplateNormalization;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
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
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.util.RunInfo;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.StreamKey;
import oracle.sql.BLOB;

/**
 *
 * @author dodge1
 */
public class SubspaceTemplateDAO {

    private SubspaceTemplateDAO() {
    }

    public static SubspaceTemplateDAO getInstance() {
        return SubspaceTemplateDAOHolder.INSTANCE;
    }

    public void updateTemplateData(int detectorid, EmpiricalTemplate template, TemplateNormalization normalization, Connection conn) throws Exception {
        removeExistingTemplate(detectorid, conn);
        saveTemplateData(detectorid, template, normalization, conn);
    }

    public void updateAllTemplates() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("select detectorid, normalization from %s", "subspace_template");
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            int success = 0;
            int failure = 0;
            while (rs.next()) {
                int detectorid = rs.getInt(1);
                String normalization = rs.getString(2);
                try {
                    SubspaceTemplate template = getSubspaceTemplate(conn, detectorid);
                    updateTemplateData(detectorid, template, TemplateNormalization.valueOf(normalization), conn);
                    conn.commit();
                    ++success;
                } catch (Exception ex) {
                    ++failure;
                }
                System.out.println(String.format("%d converted and %d failures...", success, failure));
            }
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

    private void removeExistingTemplate(int detectorid, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("delete from subspace_template where detectorid = ?")) {
            stmt.setInt(1, detectorid);
            stmt.execute();
        }
    }

    private static class SubspaceTemplateDAOHolder {

        private static final SubspaceTemplateDAO INSTANCE = new SubspaceTemplateDAO();
    }

    public void saveTemplateData(int detectorid, EmpiricalTemplate template, TemplateNormalization normalization, Connection conn) throws Exception {
        PreparedStatement insertTemplateStmt = null;

        PreparedStatement getBlobStmt = null;

        try {
            insertTemplateStmt = conn.prepareStatement("insert into subspace_template values (?,empty_blob(),?,?)");
            getBlobStmt = conn.prepareStatement("select template from subspace_template where detectorid = ?");
            insertTemplateStmt.setInt(1, detectorid);
            insertTemplateStmt.setString(2, normalization.toString());
            Double tbp = template.getTemplateTBP();
            if(tbp != null){
                insertTemplateStmt.setDouble(3, tbp);
            }
            else{
                insertTemplateStmt.setNull(3, Types.DOUBLE);
            }
            insertTemplateStmt.execute();
            writeIntoTemplateBlob(detectorid, getBlobStmt, template);

        } finally {
            if (getBlobStmt != null) {
                getBlobStmt.close();
            }
            if (insertTemplateStmt != null) {
                insertTemplateStmt.close();
            }
        }
    }

    private void writeIntoTemplateBlob(int detectorid, PreparedStatement getBlobStmt, Object template) throws SQLException, IOException {
        getBlobStmt.setInt(1, detectorid);

        ResultSet rs = null;
        OutputStream os = null;
        ObjectOutputStream oop = null;
        try {
            rs = getBlobStmt.executeQuery();
            if (rs.next()) {
                BLOB blob = (BLOB) rs.getBlob(1);
                os = blob.getBinaryOutputStream();
                oop = new ObjectOutputStream(os);
                oop.writeObject(template);
                oop.flush();
            } else {
                throw new IllegalStateException("Failed to write template data for detector!");
            }
        } finally {
            if (oop != null) {
                oop.close();
            }
            if (os != null) {
                os.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public EmpiricalTemplate getEmpiricalTemplate(Connection conn, int detectorid) throws Exception {
        return getSubspaceTemplate(conn, detectorid);
    }
    
    public SubspaceTemplate getSubspaceTemplate(int detectorid) throws Exception {
        Connection conn = null;
        try{
            conn = ConnectionManager.getInstance().checkOut();
            return getSubspaceTemplate( conn,  detectorid);
        }
        finally{
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    public SubspaceTemplate getSubspaceTemplate(Connection conn, int detectorid) throws Exception {
        Object tmpObj = getTemplate(conn, detectorid);;
        SubspaceTemplate tmp = (SubspaceTemplate) tmpObj;
        if (tmp.getSpecification() == null) {
            SubspaceSpecification spec = retrieveSubspaceSpecification(detectorid, conn);
            tmp = new SubspaceTemplate(tmp, spec);
        }
        return tmp;
    }

    public ArrayCorrelationTemplate getArrayCorrelationTemplate(Connection conn, int detectorid) throws Exception {
        return (ArrayCorrelationTemplate) getTemplate(conn, detectorid);
    }

    public Object getTemplate(Connection conn, int detectorid) throws ClassNotFoundException, SQLException, IllegalStateException, IOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select template\n"
                    + "  from subspace_template\n"
                    + " where detectorid = ?");
            stmt.setInt(1, detectorid);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return DbOps.getBlobObject(rs);
            } else {
                throw new IllegalStateException("No template found for detectorid: " + detectorid);
            }

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private SubspaceSpecification retrieveSubspaceSpecification(int detectorid, Connection conn) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement("select threshold,\n"
                    + "       blackout_seconds,\n"
                    + "       sta_duration, "
                    + "       lta_duration, "
                    + "       gap_duration\n"
                    + "  from detector a, subspace_detector_params b\n"
                    + "  where a.detectorid = ?"
                    + "   and a.detectorid = b.detectorid ");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int idx = 1;
                double threshold = rs.getDouble(idx++);
                double blackoutSeconds = rs.getDouble(idx++);
                double staDuration = rs.getDouble(idx++);
                double ltaDuration = rs.getDouble(idx++);
                double gapDuration = rs.getDouble(idx++);

                ArrayList< StreamKey> channels = DetectorDAO.getInstance().getDetectorChannels(conn, detectorid);

                return new SubspaceSpecification(
                        (float) threshold,
                        (float) blackoutSeconds,
                        (float) staDuration,
                        (float) ltaDuration,
                        (float) gapDuration,
                        channels);

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

    public void writeTemplateProjections(int detectorid1, int detectorid2, int delay, double projection) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        int runid = RunInfo.getInstance().getRunid();
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("insert into subspace_detector_projection values (?,?,?,?,?)");
            int jdx = 1;
            stmt.setInt(jdx++, runid);
            stmt.setInt(jdx++, detectorid1);
            stmt.setInt(jdx++, detectorid2);
            stmt.setInt(jdx++, delay);

            stmt.setDouble(jdx++, projection);

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

}
