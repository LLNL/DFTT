/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.EventSummary;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.selectionCriteria.EventSelectionCriteria;
import llnl.gnem.core.dataAccess.selectionCriteria.EventSelectionCriteriaP;
import llnl.gnem.core.dataAccess.selectionCriteria.position.DefaultPositionRestrictionImpl;
import llnl.gnem.core.dataAccess.selectionCriteria.position.PositionRestriction;
import llnl.gnem.core.seismicData.EventInfo;
import llnl.gnem.core.dataAccess.interfaces.EventDataDAO;

/**
 *
 * @author dodge1
 */
public class OracleEventDataDAO implements EventDataDAO {

    @Override
    public EventInfo getEventInfo(long eventId) throws DataAccessException
    {
        try {
            return getEventInfoP(eventId);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }
    private EventInfo getEventInfoP(long eventId) throws SQLException {
        String sql = String.format("select lat,lon,depth, time,evname from "
                + "%s a, %s  b "
                + "where a.event_id = ? and prime = 'y' and a.event_id = b.event_id(+)",
                TableNames.ORIGIN_SOLUTION_TABLE,
                TableNames.EVENT_DESCRIPTION_TABLE);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, eventId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                double lat = rs.getDouble(jdx++);
                double lon = rs.getDouble(jdx++);
                Double depth = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    depth = null;
                }
                double time = rs.getDouble(jdx++);
                String evname = rs.getString(jdx++);
                if (rs.wasNull()) {
                    evname = "-";
                }
                return new EventInfo(eventId, lat, lon, depth, time, evname);
            }
            return null;
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

    @Override
    public Collection<EventSummary> getEvents(EventSelectionCriteria criteria) throws DataAccessException {
        try {
            if (criteria.isUseEvidList()) {
                return getEvents(criteria.getEvidList());
            } else {
                return getEventsP(criteria);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<EventSummary> getEventsP(EventSelectionCriteria criteria) throws SQLException {
        Collection<EventSummary> result = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        EventSelectionCriteriaP crit = new EventSelectionCriteriaP(criteria);
        String sql = crit.getSQl();
        PositionRestriction pos = crit.getPositionRestriction();
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            processResultSet(rs, pos, result);
            return result;
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

    private void processResultSet(ResultSet rs,
            PositionRestriction pos,
            Collection<EventSummary> result) throws SQLException {
        while (rs.next()) {
            int j = 1;
            long evid = rs.getLong(j++);
            long orid = rs.getLong(j++);
            double lat = rs.getDouble(j++);
            double lon = rs.getDouble(j++);
            if (!pos.isInside(lat, lon)) {
                continue;
            }
            Double depth = rs.getDouble(j++);
            if (rs.wasNull()) {
                depth = null;
            }
            double time = rs.getDouble(j++);
            String etype = rs.getString(j++);
            if (rs.wasNull()) {
                etype = null;
            }
            String magtype = rs.getString(j++);
            if (rs.wasNull()) {
                magtype = null;
            }
            Double magnitude = rs.getDouble(j++);
            if (rs.wasNull()) {
                magnitude = null;
            }
            String answer = rs.getString(j++);
            result.add(new EventSummary(evid, orid, lat, lon, depth, time, etype, 
                    magtype, magnitude, answer.equals("y")));
        }
    }

    @Override
    public Collection<EventSummary> getEvents(Collection<Long> evids) throws DataAccessException {
        try {
            return getEventsP(evids);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<EventSummary> getEventsP(Collection<Long> evids) throws SQLException {
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            insertIntoTempTable(evids, conn);
            return retrieveUsingTempTable(conn);

        } finally {
            DAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void insertIntoTempTable(Collection<Long> evids, Connection conn) throws SQLException {
        String sql = String.format("insert into %s values(?)", TableNames.TEMP_EVID_TABLE);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (long evid : evids) {
                stmt.setLong(1, evid);
                stmt.execute();
            }
        }
    }

    private Collection<EventSummary> retrieveUsingTempTable(Connection conn) throws SQLException {
        Collection<EventSummary> result = new ArrayList<>();
        PositionRestriction pos = new DefaultPositionRestrictionImpl();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = String.format("select /*+ ordered use_nl(a) */  \n"
                + "a.event_id,\n"
                + "a.origin_id,\n"
                + "lat,\n"
                + "lon,\n"
                + "depth,\n"
                + "time,\n"
                + "etype,\n"
                + "magtype,\n"
                + "magnitude, has_waveforms\n"
                + "  from %s aa,\n"
                + "       %s a "
                + " where a.event_id = aa.event_id\n",
                TableNames.TEMP_EVID_TABLE,
                TableNames.QUICK_ORIGIN_LOOKUP_TABLE);
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            processResultSet(rs, pos, result);
            conn.commit();
            return result;
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
