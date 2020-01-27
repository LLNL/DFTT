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
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.apps.detection.dataAccess.interfaces.StationDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;

public abstract class DbStationDAO implements StationDAO {

    @Override
    public Collection<StationInfo> getGroupStations(int groupid) throws DataAccessException {
        try {
            return getGroupStationsInfoP(groupid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Collection<StationInfo> getGroupStationsInfoP(int groupid) throws SQLException {
        Collection<StationInfo> result = new ArrayList<>();
        String sql = String.format("select configid,sta,stla,stlo from %s where groupid = ?", TableNames.getGroupStationDataTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, groupid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int configid = rs.getInt(jdx++);
                        String sta = rs.getString(jdx++);
                        double stla = rs.getDouble(jdx++);
                        double stlo = rs.getDouble(jdx++);
                        result.add(new StationInfo(configid, sta, stla, stlo));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    @Override
    public int getGroupForDetectionid(long detectionid) throws DataAccessException {
        try {
            return getGroupForDetectionidP(detectionid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

    }

    private int getGroupForDetectionidP(long detectionid) throws SQLException {
        String sql = String.format("select groupid\n"
                + "  from %s\n"
                + " where configid in (select configid\n"
                + "                      from %s a, %s b\n"
                + "                     where detectionid = ?\n"
                + "                       and a.runid = b.runid)", TableNames.getConfigurationGroupTable(), TableNames.getDetectionTable(), TableNames.getFrameworkRunTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, detectionid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;  // No group for this detection
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

}
