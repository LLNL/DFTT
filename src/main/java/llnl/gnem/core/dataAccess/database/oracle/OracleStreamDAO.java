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
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.StreamEpochInfo;
import llnl.gnem.core.dataAccess.dataObjects.StreamInfo;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.StreamDAO;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class OracleStreamDAO implements StreamDAO {

    @Override
    public StreamEpochInfo getBestStreamEpoch(int streamId, double time) throws DataAccessException {
        try {
            return getBestStreamEpochP(streamId, time);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private StreamEpochInfo getBestStreamEpochP(int streamId, double time) throws SQLException {
        String sql = String.format("select station_id, "
                + "station_source,network_code,net_start_date,station_code,"
                + "chan,location_code,band, instrument_code, orientation_code,"
                + "description,begin_time, end_time, depth, azimuth,dip, "
                + "samprate from %s where stream_epoch_id = ? ",
                TableNames.STREAM_EPOCH_VIEW);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            int streamEpochId = getBestStreamEpochId(streamId, time, conn);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, streamEpochId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                int stationId = rs.getInt(jdx++);
                String stationSource = rs.getString(jdx++);
                String networkCode = rs.getString(jdx++);
                int netDate = rs.getInt(jdx++);;
                String stationCode = rs.getString(jdx++);
                String chan = rs.getString(jdx++);
                String locationCode = rs.getString(jdx++);
                String band = rs.getString(jdx++);
                if (rs.wasNull()) {
                    band = null;
                }
                String instrumentCode = rs.getString(jdx++);
                if (rs.wasNull()) {
                    instrumentCode = null;
                }
                String orientationCode = rs.getString(jdx++);
                if (rs.wasNull()) {
                    orientationCode = null;
                }
                String description = rs.getString(jdx++);
                if (rs.wasNull()) {
                    description = null;
                }
                double beginTime = rs.getDouble(jdx++);
                double endTime = rs.getDouble(jdx++);
                Double depth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    depth = null;
                }
                Double azimuth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    azimuth = null;
                }
                Double dip = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    dip = null;
                }
                Double samprate = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    samprate = null;
                }

                StreamKey streamKey = new StreamKey(stationSource, networkCode, netDate, stationCode, chan, locationCode);
                StreamInfo streamInfo = new StreamInfo(streamId,
                        stationId,
                        streamKey,
                        band,
                        instrumentCode,
                        orientationCode,
                        description);

                return new StreamEpochInfo(streamEpochId,
                        streamInfo,
                        beginTime,
                        endTime,
                        depth,
                        azimuth,
                        dip,
                        samprate);
            }
            ApplicationLogger.getInstance().log(Level.WARNING, String.format("No StreamInfo found for stream_id = %d and time = %f!", streamId, time));
            return getBestStreamEpochNoTimeConstraint(streamId);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private StreamEpochInfo getBestStreamEpochNoTimeConstraint(int streamId) throws SQLException {
        String sql = String.format("select stream_epoch_id, station_id, "
                + "station_source,network_code,net_start_date,station_code,"
                + "chan,location_code,band, instrument_code, orientation_code,"
                + "description,begin_time, end_time, depth, azimuth,dip, "
                + "samprate from %s where stream_id = ? order by stream_epoch_id desc",
                TableNames.STREAM_EPOCH_VIEW);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, streamId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.interrupted()) {
                    return null;
                }
                int jdx = 1;
                int streamEpochId = rs.getInt(jdx++);
                int stationId = rs.getInt(jdx++);
                String stationSource = rs.getString(jdx++);
                String networkCode = rs.getString(jdx++);
                int netDate = rs.getInt(jdx++);;
                String stationCode = rs.getString(jdx++);
                String chan = rs.getString(jdx++);
                String locationCode = rs.getString(jdx++);
                String band = rs.getString(jdx++);
                if (rs.wasNull()) {
                    band = null;
                }
                String instrumentCode = rs.getString(jdx++);
                if (rs.wasNull()) {
                    instrumentCode = null;
                }
                String orientationCode = rs.getString(jdx++);
                if (rs.wasNull()) {
                    orientationCode = null;
                }
                String description = rs.getString(jdx++);
                if (rs.wasNull()) {
                    description = null;
                }
                double beginTime = rs.getDouble(jdx++);
                double endTime = rs.getDouble(jdx++);
                Double depth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    depth = null;
                }
                Double azimuth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    azimuth = null;
                }
                Double dip = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    dip = null;
                }
                Double samprate = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    samprate = null;
                }

                StreamKey streamKey = new StreamKey(stationSource, networkCode, netDate, stationCode, chan, locationCode);
                StreamInfo streamInfo = new StreamInfo(streamId,
                        stationId,
                        streamKey,
                        band,
                        instrumentCode,
                        orientationCode,
                        description);

                return new StreamEpochInfo(streamEpochId,
                        streamInfo,
                        beginTime,
                        endTime,
                        depth,
                        azimuth,
                        dip,
                        samprate);
            }
            throw new IllegalStateException(String.format("No StreamInfo found for stream_id = %d!", streamId));
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            DAOFactory.getInstance().getConnections().checkIn(conn);

        }
    }

    private int getBestStreamEpochId(int streamId, double time, Connection conn) throws SQLException {

        String sql = "select llnl2.station_util.get_best_stream_epoch(?,?) from dual";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, streamId);
            stmt.setDouble(2, time);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                int streamEpochId = rs.getInt(jdx++);
                if (rs.wasNull()) {
                    return -1;
                } else {
                    return streamEpochId;
                }
            }
            return -1;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

}
