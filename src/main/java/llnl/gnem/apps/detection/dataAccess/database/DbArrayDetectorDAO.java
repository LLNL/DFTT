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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayPowerDetector;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.ArrayDetectorDAO;

import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.PowerDetThreshold;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public abstract class DbArrayDetectorDAO implements ArrayDetectorDAO {

    @Override
    public int saveArrayDetector(ArrayDetectorSpecification specs, int streamid) throws DataAccessException {
        try {
            return saveArrayDetectorP(specs, streamid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public ArrayPowerDetector retrieveArrayDetector(int detectorid, ConcreteStreamProcessor processor) throws DataAccessException {
        try {
            return retrieveArrayDetectorP(detectorid, processor);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private ArrayPowerDetector retrieveArrayDetectorP(int detectorid, ConcreteStreamProcessor processor) throws Exception {

        Connection conn = null;
        String sql = String.format("select threshold,blackout_seconds,array_name,"
                + "sta_duration,lta_duration,gap_duration, enable_spawning,"
                + "back_Azimuth,velocity "
                + "from %s a, %s b where a.detectorid = ?  "
                + "and a.detectorid = b.detectorid",
                TableNames.getDetectorTable(),
                TableNames.getArrayDetectorParamsTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
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
                        ArrayList< StreamKey> channels = DetectorUtil.getDetectorChannels(conn, detectorid);
                        validateStreamCompatibility(conn, processor.getStreamId(), channels);

                        double decimatedSampleRate = processor.getParams().getPreprocessorParams().getDecimatedSampleRate();
                        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();

                        ArrayDetectorSpecification spec
                                = ArrayDetectorSpecification.create(
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
                                        jdate);
                        return new ArrayPowerDetector(detectorid, spec, decimatedSampleRate, processor.getStreamName(), decimatedBlockSize);
                    } else {
                        throw new IllegalArgumentException("No array detector row found for detectorid " + detectorid);
                    }
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private int saveArrayDetectorP(ArrayDetectorSpecification specs, int streamid) throws SQLException {
        Connection conn = null;
        String sql = String.format("insert into %s values (?,?,?,?,?,?,?,?)",
                TableNames.getArrayDetectorParamsTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            int detectorid = DetectorUtil.writeDetectorRow(conn, streamid, DetectorType.ARRAYPOWER, specs.getThreshold(), specs.getBlackoutPeriod(), null);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                stmt.setString(2, specs.getArrayConfiguration().getArrayName());
                stmt.setDouble(3, specs.getSTADuration());
                stmt.setDouble(4, specs.getLTADuration());
                stmt.setDouble(5, specs.getGapDuration());
                stmt.setString(6, specs.spawningEnabled() ? "y" : "n");
                stmt.setDouble(7, specs.getBackAzimuth());
                stmt.setDouble(8, specs.getVelocity());

                stmt.execute();
                DetectorUtil.writeDetectorChannels(conn, detectorid, specs.getStreamKeys());
                conn.commit();
                return detectorid;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private void validateStreamCompatibility(Connection conn, int streamid, Collection<StreamKey> channels) throws DataAccessException {

        Collection<StreamKey> keys = DetectionDAOFactory.getInstance().getStreamDAO().getStreamKeysForStream(streamid);
        Set<StreamKey> streamChannels = new HashSet<>();
        streamChannels.addAll(keys);

        for (StreamKey sck : channels) {
            if (!streamChannels.contains(sck)) {
                throw new IllegalStateException("Detector channel: " + sck + " not found in stream!");
            }
        }

    }
}
