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
import llnl.gnem.apps.detection.dataAccess.dataobjects.EventInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.apps.detection.dataAccess.interfaces.EventDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.Epoch;

public abstract class DbEventDAO implements EventDAO {

    @Override
    public Collection<Integer> getEventList() throws DataAccessException {
        try {
            return getEventListP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<StationInfo> getEventStationInfo(int evid) throws DataAccessException {
        try {
            return getEventStationInfoP(evid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void defineNewEvent(double minTime, double maxTime) throws DataAccessException {
        try {
            defineNewEventP(minTime, maxTime);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    
    @Override
    public Collection<EventInfo> getEventsInTimeWindow(Epoch epoch) throws DataAccessException{
        try {
            return getEventsInTimeWindowP(epoch);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Collection<Integer> getEventListP() throws SQLException {
        Collection<Integer> result = new ArrayList<>();
        String sql = String.format("select distinct evid, time from %s where status not in ( 'no_data', 'No Signal') order by time", TableNames.getOriginTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getInt(1));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<StationInfo> getEventStationInfoP(int evid) throws SQLException {
        Collection<StationInfo> result = new ArrayList<>();
        String sql = String.format("select configid,sta,stla,stlo from %s where evid = ? order by stime", TableNames.getEventStationTimesTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, evid);
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
    public void saveEventStatus(int evid, String status) throws DataAccessException {
        try {
            saveEventStatusP(evid, status);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void saveEventStatusP(int evid, String status) throws SQLException {
        String sql = String.format("update %s set status = ? where evid = ?", TableNames.getOriginTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setInt(2, evid);
                stmt.execute();
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private void defineNewEventP(double minTime, double maxTime) throws SQLException {
        String sql = String.format("insert into %s values ( %s.nextval,?,?)",
                TableNames.getEventTable(),
                SequenceNames.getEventidSequenceName());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, minTime);
                stmt.setDouble(2, maxTime);
                stmt.execute();
                addPicks(minTime, maxTime, conn);
                conn.commit();
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void addPicks(double minTime, double maxTime, Connection conn) throws SQLException {
        String sql = String.format("insert into %s select %s.currval, pickid from %s where time between ? and ?",
                TableNames.getEventPickAssocTable(),
                SequenceNames.getEventidSequenceName(),
                TableNames.getPhasePickTable());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minTime);
            stmt.setDouble(2, maxTime);
            stmt.execute();
        }
    }

    private Collection<EventInfo> getEventsInTimeWindowP(Epoch epoch) throws SQLException {
        Collection<EventInfo> result = new ArrayList<>();
        String sql = String.format("select eventid, min_time,max_time from %s where min_time >= ? and max_time <= ?", 
                TableNames.getEventTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, epoch.getStart());
            stmt.setDouble(2, epoch.getEnd());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int eventId = rs.getInt(jdx++);
                        double minTime = rs.getDouble(jdx++);
                        double maxTime = rs.getDouble(jdx++);
                        result.add(new EventInfo(eventId, minTime, maxTime));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

}
