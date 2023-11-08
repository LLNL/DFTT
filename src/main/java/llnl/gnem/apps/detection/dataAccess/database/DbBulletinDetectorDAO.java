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
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.Bulletin;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinDetector;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinSpecification;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.BulletinDetectorDAO;

import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.initialization.BootDetectorParams;
import llnl.gnem.apps.detection.util.initialization.StreamInfo;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;

import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public abstract class DbBulletinDetectorDAO implements BulletinDetectorDAO {

    @Override
    public void deleteBulletinDetector(int streamid) throws DataAccessException {
        try {
            deleteBulletinDetectorP(streamid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Detector retrieveBulletinDetector(int detectorid, ConcreteStreamProcessor processor) throws DataAccessException {
        try {
            return retrieveBulletinDetectorP(detectorid, processor);
        } catch (IOException | SQLException | ClassNotFoundException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int saveBulletinDetector(BulletinSpecification specs, int streamid) throws DataAccessException {
        try {
            return saveBulletinDetectorP(specs, streamid);
        } catch (SQLException | IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    
    @Override
    public void maybeReplaceBulletinDetector(int streamid) throws DataAccessException {
        try {
            maybeReplaceBulletinDetectorP(streamid);
        } catch (SQLException | IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Detector retrieveBulletinDetectorP(int detectorid, ConcreteStreamProcessor processor)
            throws IOException, SQLException, ClassNotFoundException {

        int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();
        Connection conn = null;
        String sql = String.format("select threshold,blackout_seconds, enable_spawning "
                + "from  %s a, %s b where a.detectorid = ? and a.detectorid = b.detectorid ",
                TableNames.getDetectorTable(),
                TableNames.getBulletinDetectorSpecTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        float threshold = (float) rs.getDouble(1);

                        float blackOutSeconds = (float) rs.getDouble(2);

                        boolean enableSpawning = rs.getString(3).equals("y");
                        ArrayList< StreamKey> channels = DetectorUtil.getDetectorChannels(conn, detectorid);
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
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private int saveBulletinDetectorP(BulletinSpecification specs, int streamid) throws SQLException, IOException {
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            int detectorid = DetectorUtil.writeDetectorRow(conn,
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
            DetectorUtil.writeDetectorChannels(conn, detectorid, specs.getStreamKeys());
            conn.commit();
            return detectorid;
        } finally {

            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private Bulletin getBulletin(int detectorid, Connection conn) throws SQLException, IOException, ClassNotFoundException {
        String sql = String.format("select bulletin\n"
                + "  from %s\n"
                + " where detectorid = ? ",
                TableNames.getBulletinDetectorSpecTable());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return (Bulletin) DetectorUtil.getBlobObject(rs);
                } else {
                    throw new IllegalStateException("No Bulletin found for detectorid: " + detectorid);
                }
            }
        }
    }

    private void writeBulletinSpec(Connection conn, int detectorid, Bulletin bulletin, DetectorType type, boolean enableSpawning) throws SQLException, IOException {

        String sql = String.format("insert into %s values (?,empty_blob(),?,?)",
                TableNames.getBulletinDetectorSpecTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detectorid);
            stmt.setString(2, type.toString());
            stmt.setString(3, enableSpawning ? "y" : "n");
            stmt.execute();
            sql = String.format("select bulletin from %s where detectorid = ?",
                    TableNames.getBulletinDetectorSpecTable());
            try (PreparedStatement getBlobStmt = conn.prepareStatement(sql)) {
                getBlobStmt.setInt(1, detectorid);
                DetectorUtil.writeIntoBlob(getBlobStmt, bulletin);
            }
        }

    }

    private void maybeReplaceBulletinDetectorP(int streamid) throws SQLException, IOException, DataAccessException {
        deleteBulletinDetector(streamid);
        String name = DetectionDAOFactory.getInstance().getStreamDAO().getStreamName(streamid);
        StreamInfo info = StreamsConfig.getInstance().getInfo(name);
        BootDetectorParams bdp = info.getBootDetectorParams();
        for (BulletinSpecification specs : bdp.getBulletinParams(name)) {
            saveBulletinDetector(specs, streamid);
        }

    }

    private void deleteBulletinDetectorP(int streamid) throws SQLException {
        String sql = String.format("delete from %s where streamid = ? and detectortype = 'BULLETIN'",
                TableNames.getDetectorTable());
        Connection conn = null;
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, streamid);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

}
