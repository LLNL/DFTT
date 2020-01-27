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

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTADetector;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import llnl.gnem.apps.detection.util.PowerDetThreshold;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class StaLtaDetectorDAO {

    private StaLtaDetectorDAO() {
    }

    public static StaLtaDetectorDAO getInstance() {
        return StaLtaDetectorDAOHolder.INSTANCE;
    }

    private static class StaLtaDetectorDAOHolder {

        private static final StaLtaDetectorDAO INSTANCE = new StaLtaDetectorDAO();
    }

    public Detector retrieveStaLtaDetector(Connection conn, int detectorid, ConcreteStreamProcessor processor)
            throws IOException, SQLException {

        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("select threshold,blackout_seconds,sta_duration,lta_duration,gap_duration, enable_spawning "
                    + "from  detector a, stalta_detector_params b where a.detectorid = ? and a.detectorid = b.detectorid ");
            stmt.setInt(1, detectorid);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                PowerDetThreshold.getInstance().setThresholdFromDb(rs.getDouble(1));
                float threshold = (float) PowerDetThreshold.getInstance().getThreshold();
             
                float blackOutSeconds = (float) rs.getDouble(2);
                float STADuration = (float) rs.getDouble(3);
                float LTADuration = (float) rs.getDouble(4);
                float gapDuration = (float) rs.getDouble(5);
                boolean enableSpawning = rs.getString(6).equals("y");
                ArrayList< StreamKey> channels = DetectorDAO.getInstance().getDetectorChannels(conn, detectorid);

                STALTASpecification spec = 
                        new STALTASpecification( 
                                                 threshold,
                                                 blackOutSeconds,
                                                 channels,
                                                 STADuration,
                                                 LTADuration,
                                                 gapDuration,
                                                 enableSpawning  );
                double decimatedSampleRate = processor.getParams().getPreprocessorParams().getDecimatedSampleRate();
                return new STALTADetector(detectorid, spec, decimatedSampleRate, processor.getStreamName(), decimatedBlockSize);
            } else {
                throw new IllegalArgumentException("No detector sta/lta detector row found for detectorid " + detectorid);
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

    public int saveStaLtaDetector(STALTASpecification specs, int streamid) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            int detectorid = DbOps.getInstance().writeDetectorRow( conn, 
                                                                   streamid, 
                                                                   DetectorType.STALTA, 
                                                                   specs.getThreshold(), 
                                                                   specs.getBlackoutPeriod(), 
                                                                   null  );
            writeStaLtaParams( conn, 
                               detectorid, 
                               specs.getSTADuration(),
                               specs.getLTADuration(), 
                               specs.getGapDuration(), 
                               specs.spawningEnabled()  );
            DetectorDAO.getInstance().writeDetectorChannels(conn, detectorid, specs.getStaChanList());
            conn.commit();
            return detectorid;
        } finally {

            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public void writeStaLtaParams(Connection conn, int detectorid, double staDuration, double ltaDuration, double gapDuration, boolean enableSpawning) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into stalta_detector_params values (?,?,?,?,?)")) {
            stmt.setInt(1, detectorid);
            stmt.setDouble(2, staDuration);
            stmt.setDouble(3, ltaDuration);
            stmt.setDouble(4, gapDuration);
              stmt.setString(5, enableSpawning ? "y":"n");
            stmt.execute();
        }
    }
}
