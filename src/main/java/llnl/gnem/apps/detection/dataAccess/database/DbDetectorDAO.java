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
package llnl.gnem.apps.detection.dataAccess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectorDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;

public abstract class DbDetectorDAO implements DetectorDAO {

    @Override
    public List<StreamKey> getDetectorChannels(long detectorid) throws DataAccessException {
        try {
            return getDetectorChannelsP(detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private List<StreamKey> getDetectorChannelsP(long detectorid) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        String sql = String.format("select sta, chan from detector_channel where detectorid = ? order by position", TableNames.getDetectorChannelTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String sta = rs.getString(1);
                        String chan = rs.getString(2);
                        result.add(new StreamKey(sta, chan));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    @Override
    public List<StreamKey> getDetectorChannelsFromConfig(long configid) throws DataAccessException {
        try {
            return getDetectorChannelsPFromConfigP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private List<StreamKey> getDetectorChannelsPFromConfigP(long configid) throws SQLException {
        ArrayList<StreamKey> result = new ArrayList<>();
        String sql = String.format("select sta, chan from %s where streamid in (\n"
                + "select streamid from stream where configid = ?)", 
                TableNames.getStreamChannelTable(),
                TableNames.getStreamTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, configid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String sta = rs.getString(1);
                        String chan = rs.getString(2);
                        result.add(new StreamKey(sta, chan));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
    
    @Override
    public void writeChangedThreshold(int runid, int detectorid, TimeT streamTime, double newThreshold) throws DataAccessException
    {
       try {
            writeChangedThresholdP(runid, detectorid, streamTime, newThreshold);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void writeChangedThresholdP(int runid, int detectorid, TimeT streamTime, double newThreshold) throws SQLException{
        String sql = String.format("insert into %s values (?,?,?,?)", TableNames.getDetectorThresholdHistoryTable());
                Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1,runid);
                stmt.setInt(2, detectorid);
                stmt.setDouble(3, streamTime.getEpochTime());
                stmt.setDouble(4, newThreshold);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

}
