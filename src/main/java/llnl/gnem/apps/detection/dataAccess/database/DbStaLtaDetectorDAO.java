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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTADetector;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.StaLtaDetectorDAO;

import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.PowerDetThreshold;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public abstract class DbStaLtaDetectorDAO implements StaLtaDetectorDAO {

    @Override
    public Detector retrieveStaLtaDetector(int detectorid, ConcreteStreamProcessor processor) throws DataAccessException {
        try {
            return retrieveStaLtaDetectorP(detectorid, processor);
        } catch (IOException | SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    
    @Override
    public int saveStaLtaDetector(STALTASpecification specs, int streamid)throws DataAccessException {
        try {
            return saveStaLtaDetectorP(specs, streamid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Detector retrieveStaLtaDetectorP(int detectorid, ConcreteStreamProcessor processor)
            throws IOException, SQLException {

        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();
        Connection conn = null;
        String sql = String.format("select threshold,blackout_seconds,sta_duration,lta_duration,gap_duration, enable_spawning "
                + "from  %s a, %s b where a.detectorid = ? and a.detectorid = b.detectorid ",
                TableNames.getDetectorTable(),
                TableNames.getSTALTADetectorParamsTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        PowerDetThreshold.getInstance().setThresholdFromDb(rs.getDouble(1));
                        float threshold = (float) PowerDetThreshold.getInstance().getThreshold();

                        float blackOutSeconds = (float) rs.getDouble(2);
                        float STADuration = (float) rs.getDouble(3);
                        float LTADuration = (float) rs.getDouble(4);
                        float gapDuration = (float) rs.getDouble(5);
                        boolean enableSpawning = rs.getString(6).equals("y");
                        ArrayList< StreamKey> channels = DetectorUtil.getDetectorChannels(conn, detectorid);

                        STALTASpecification spec
                                = new STALTASpecification(
                                        threshold,
                                        blackOutSeconds,
                                        channels,
                                        STADuration,
                                        LTADuration,
                                        gapDuration,
                                        enableSpawning);
                        double decimatedSampleRate = processor.getParams().getPreprocessorParams().getDecimatedSampleRate();
                        return new STALTADetector(detectorid, spec, decimatedSampleRate, processor.getStreamName(), decimatedBlockSize);
                    } else {
                        throw new IllegalArgumentException("No detector sta/lta detector row found for detectorid " + detectorid);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private int saveStaLtaDetectorP(STALTASpecification specs, int streamid) throws SQLException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            int detectorid = DetectorUtil.writeDetectorRow(conn,
                    streamid,
                    DetectorType.STALTA,
                    specs.getThreshold(),
                    specs.getBlackoutPeriod(),
                    null);
            writeStaLtaParams(conn,
                    detectorid,
                    specs.getSTADuration(),
                    specs.getLTADuration(),
                    specs.getGapDuration(),
                    specs.spawningEnabled());
            DetectorUtil.writeDetectorChannels(conn, detectorid, specs.getStreamKeys());
            conn.commit();
            return detectorid;
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
            
        }
    }

    private void writeStaLtaParams(Connection conn, int detectorid, double staDuration, double ltaDuration, double gapDuration, boolean enableSpawning) throws SQLException {
        String sql = String.format("insert into %s values (?,?,?,?,?)",
                TableNames.getSTALTADetectorParamsTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);
            stmt.setDouble(2, staDuration);
            stmt.setDouble(3, ltaDuration);
            stmt.setDouble(4, gapDuration);
            stmt.setString(5, enableSpawning ? "y" : "n");
            stmt.execute();
        }
    }
}
