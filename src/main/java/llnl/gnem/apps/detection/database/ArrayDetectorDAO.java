/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayPowerDetector;
import llnl.gnem.apps.detection.util.PowerDetThreshold;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class ArrayDetectorDAO {

    private ArrayDetectorDAO() {
    }

    public static ArrayDetectorDAO getInstance() {
        return ArrayDetectorDAOHolder.instance;
    }

    private static class ArrayDetectorDAOHolder {

        private static final ArrayDetectorDAO instance = new ArrayDetectorDAO();
    }

    public ArrayPowerDetector retrieveArrayDetector(Connection conn, int detectorid, ConcreteStreamProcessor processor) throws Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select threshold,blackout_seconds,array_name,"
                    + "sta_duration,lta_duration,gap_duration, enable_spawning,"
                    + "back_Azimuth,velocity "
                    + "from detector a, array_detector_params b where a.detectorid = ?  "
                    + "and a.detectorid = b.detectorid");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();
            if (rs.next()) {

                PowerDetThreshold.getInstance().setThresholdFromDb(rs.getDouble(1));
                float threshold = (float) PowerDetThreshold.getInstance().getThreshold();
                float blackOutSeconds = (float) rs.getDouble(2);
                String arrayName = rs.getString(3);
                float STADuration = (float) rs.getDouble(4);
                float LTADuration = (float) rs.getDouble(5);
                float gapDuration = (float) rs.getDouble(6);
                boolean enableSpawning = rs.getString(7).equals("y");
                double backAzimuth = rs.getDouble(8);
                double velocity = rs.getDouble(9);
                int jdate = ProcessingPrescription.getInstance().getMinJdateToProcess();
                ArrayList< StreamKey> channels = DetectorDAO.getInstance().getDetectorChannels(conn, detectorid);
                StreamDAO.getInstance().validateStreamCompatibility(conn, processor.getStreamId(), channels);
                
               
                double decimatedSampleRate = processor.getParams().getPreprocessorParams().getDecimatedSampleRate();
                int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();

                ArrayDetectorSpecification spec = 
                        ArrayDetectorSpecification.createFromDatabase(
                                                                       threshold,
                                                                       blackOutSeconds,
                                                                       channels,
                                                                       STADuration,
                                                                       LTADuration,
                                                                       gapDuration,
                                                                       enableSpawning,
                                                                       (float) backAzimuth,
                                                                       (float) velocity,
                                                                       arrayName,
                                                                       TableNames.getInstance().getSiteTableName(),
                                                                       jdate      );
                return new ArrayPowerDetector(detectorid, spec, decimatedSampleRate, processor.getStreamName(), decimatedBlockSize);
            } else {
                throw new IllegalArgumentException("No array detector row found for detectorid " + detectorid);
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

    public int saveArrayDetector(ArrayDetectorSpecification specs, int streamid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            int detectorid = DbOps.getInstance().writeDetectorRow(conn, streamid, DetectorType.ARRAYPOWER, specs.getThreshold(), specs.getBlackoutPeriod(), null);
            stmt = conn.prepareStatement("insert into array_detector_params values (?,?,?,?,?,?,?,?)");
            stmt.setInt(1, detectorid);
            stmt.setString(2, specs.getArrayConfiguration().getArrayName());
            stmt.setDouble(3, specs.getSTADuration());
            stmt.setDouble(4, specs.getLTADuration());
            stmt.setDouble(5, specs.getGapDuration());
            stmt.setString(6, specs.spawningEnabled() ? "y" : "n");
            stmt.setDouble(7, specs.getBackAzimuth());
            stmt.setDouble(8, specs.getVelocity());

            stmt.execute();
            DetectorDAO.getInstance().writeDetectorChannels(conn, detectorid, specs.getStaChanList());
            conn.commit();
            return detectorid;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }

    }
}
