/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.database;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.Bulletin;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinDetector;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinSpecification;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class BulletinDetectorDAO {

    private BulletinDetectorDAO() {
    }

    public static BulletinDetectorDAO getInstance() {
        return BulletinDetectorDAOHolder.INSTANCE;
    }

    private static class BulletinDetectorDAOHolder {

        private static final BulletinDetectorDAO INSTANCE = new BulletinDetectorDAO();
    }

    public Detector retrieveBulletinDetector(Connection conn, int detectorid, ConcreteStreamProcessor processor)
            throws IOException, SQLException, ClassNotFoundException {

        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select threshold,blackout_seconds, enable_spawning "
                    + "from  detector a, bulletin_detector_spec b where a.detectorid = ? and a.detectorid = b.detectorid ");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();

            if (rs.next()) {

                float threshold = (float) rs.getDouble(1);

                float blackOutSeconds = (float) rs.getDouble(2);

                boolean enableSpawning = rs.getString(3).equals("y");
                ArrayList< StreamKey> channels = DetectorDAO.getInstance().getDetectorChannels(conn, detectorid);
                Bulletin bulletin = getBulletin(detectorid, conn);
                BulletinSpecification spec
                        = new BulletinSpecification(threshold,
                                blackOutSeconds,
                                channels,
                                bulletin,
                                enableSpawning);
                double decimatedSampleRate = processor.getParams().getPreprocessorParams().getDecimatedSampleRate();
                return new BulletinDetector(detectorid, spec, decimatedSampleRate, processor.getStreamName(), decimatedBlockSize);
            } else {
                throw new IllegalArgumentException("No bulletin detector row found for detectorid " + detectorid);
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

    public int saveBulletinDetector(BulletinSpecification specs, int streamid) throws SQLException, IOException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            int detectorid = DbOps.getInstance().writeDetectorRow(conn,
                    streamid,
                    DetectorType.BULLETIN,
                    specs.getThreshold(),
                    specs.getBlackoutPeriod(),
                    null);
            writeBulletinSpec(conn,
                    detectorid,
                    specs.getBulletin(),
                    specs.getDetectorType(),
                    specs.spawningEnabled());
            DetectorDAO.getInstance().writeDetectorChannels(conn, detectorid, specs.getStaChanList());
            conn.commit();
            return detectorid;
        } finally {

            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private Bulletin getBulletin(int detectorid, Connection conn) throws SQLException, IOException, ClassNotFoundException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select bulletin\n"
                    + "  from bulletin_detector_spec\n"
                    + " where detectorid = ? ");
            stmt.setInt(1, detectorid);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return (Bulletin) DbOps.getBlobObject(rs);
            } else {
                throw new IllegalStateException("No Bulletin found for detectorid: " + detectorid);
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

    private void writeBulletinSpec(Connection conn, int detectorid, Bulletin bulletin, DetectorType type, boolean enableSpawning) throws SQLException, IOException {
        PreparedStatement stmt = null;
        PreparedStatement getBlobStmt = null;
        try {

            stmt = conn.prepareStatement("insert into bulletin_detector_spec values (?,empty_blob(),?,?)");
            stmt.setInt(1, detectorid);
            stmt.setString(2, type.toString());
            stmt.setString(3, enableSpawning ? "y" : "n");
            stmt.execute();

            getBlobStmt = conn.prepareStatement("select bulletin from bulletin_detector_spec where detectorid = ?");

            DbOps.writeIntoBlob(detectorid, getBlobStmt, bulletin);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (getBlobStmt != null) {
                getBlobStmt.close();
            }

        }
    }

}
