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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;
import llnl.gnem.core.dataAccess.dataObjects.StationEpoch;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.StationDAO;

/**
 *
 * @author dodge1
 */
public class OracleStationDAO implements StationDAO {

    @Override
    public Collection<StationEpoch> getEventWaveformStations(long eventId, double delta) throws DataAccessException {
        try {
            return getEventWaveformStationsP(eventId, delta);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Collection<StationEpoch> getEventWaveformStationsP(long eventId, double delta) throws SQLException {
        Collection<StationEpoch> all = getEventWaveformStationsQ(eventId, delta);
        Set<Long> arrayIds = all.stream().map(s -> s.getArrayId()).filter(Objects::nonNull).collect(Collectors.toSet());
        List<StationEpoch> stations = all.stream().filter(StationEpoch::isSingleStation).collect(Collectors.toList());
        Collection<StationEpoch> arrays = getArrayStationEpochs(arrayIds);
        stations.addAll(arrays);
        return stations;
    }

    private Collection<StationEpoch> getArrayStationEpochs(Collection<Long> arrayIds) throws SQLException {
        String sql = String.format("select c.station_source   source_code,\n"
                + "       c.network_code,\n"
                + "       c.net_start_date   start_date,\n"
                + "       c.station_code,\n"
                + "       c.description,\n"
                + "       c.lat,\n"
                + "       c.lon,\n"
                + "       c.elev,\n"
                + "       c.begin_time,\n"
                + "       c.end_time,\n"
                + "       c.network_id,\n"
                + "       c.station_id,\n"
                + "       c.station_epoch_id,\n"
                + "       a.station_id array_element_id\n"
                + "  from %s a, %s b, %s c\n"
                + " where a.array_id = ?\n"
                + "   and b.refsta_id = a.station_id\n"
                + "   and a.station_id = c.station_id",
                TableNames.ARRAY_MEMBER_TABLE,
                TableNames.ARRAY_TABLE,
                TableNames.STATION_EPOCH_VIEW);
        Collection<StationEpoch> result = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            for (Long arrayId : arrayIds) {
                StationEpoch se = retrieveEpoch(stmt, arrayId);
                if (se != null) {
                    result.add(se);
                }
            }
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

    private Collection<StationEpoch> getEventWaveformStationsQ(long eventId, double delta) throws SQLException {
        String sql = String.format("select /*+ ordered use_nl(b,c,d,e) */\n"
                + "distinct e.source_code,\n"
                + "         d.network_code,\n"
                + "         d.start_date,\n"
                + "         c.station_code,\n"
                + "         c.description,\n"
                + "         b.lat,\n"
                + "         b.lon,\n"
                + "         b.elev,\n"
                + "         b.begin_time,\n"
                + "         b.end_time,\n"
                + "         d.network_id,\n"
                + "         c.station_id,\n"
                + "         b.station_epoch_id,\n"
                + "         a.array_id,\n"
                + "         a.array_element_id\n"
                + "  from %s a, %s b, %s c, %s d, %s e\n"
                + " where event_id = ? and degdist <= ?\n"
                + "   and a.used_epoch_id = b.station_epoch_id\n"
                + "   and b.station_id = c.station_id\n"
                + "   and c.network_id = d.network_id\n"
                + "   and c.source_id = e.source_id",
                TableNames.SEARCH_LINK_TABLE,
                TableNames.STATION_EPOCH_TABLE,
                TableNames.STATION_TABLE,
                TableNames.NETWORK_TABLE,
                TableNames.SOURCE_TABLE);
        Collection<StationEpoch> result = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, eventId);
            stmt.setDouble(2, delta);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                String stationSource = rs.getString(jdx++);
                String networkCode = rs.getString(jdx++);
                int netStartDate = rs.getInt(jdx++);
                String stationCode = rs.getString(jdx++);
                String description = rs.getString(jdx++);
                double lat = rs.getDouble(jdx++);
                double lon = rs.getDouble(jdx++);
                Double elev = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    elev = null;
                }
                double beginTime = rs.getDouble(jdx++);
                double endTime = rs.getDouble(jdx++);
                int networkId = rs.getInt(jdx++);
                int stationId = rs.getInt(jdx++);
                int stationEpochId = rs.getInt(jdx++);
                Long arrayId = rs.getLong(jdx++);
                if (rs.wasNull()) {
                    arrayId = null;
                }
                Long arrayElementId = rs.getLong(jdx++);
                if (rs.wasNull()) {
                    arrayElementId = null;
                }
                result.add(new StationEpoch(stationSource,
                        networkCode,
                        netStartDate,
                        stationCode,
                        description,
                        lat,
                        lon,
                        elev,
                        beginTime,
                        endTime,
                        networkId,
                        stationId,
                        stationEpochId,
                        arrayId,
                        arrayElementId));
            }
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

    @Override
    public ApplicationStationInfo getStationInfoForWaveform(long waveformId) throws DataAccessException {
        try {
            return getStationInfoForWaveformP(waveformId);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private ApplicationStationInfo getStationInfoForWaveformP(long waveformId) throws SQLException {
        String sql = String.format("select b.station_source, \n"
                + "b.network_code,\n"
                + "b.net_start_date,\n"
                + "b.station_code,\n"
                + "b.DESCRIPTION, \n"
                + "b.lat,\n"
                + "b.lon,\n"
                + "b.elev, \n"
                + "b.begin_time, \n"
                + "b.end_time, \n"
                + "b.network_id, \n"
                + "b.STATION_ID, \n"
                + "b.STATION_EPOCH_ID, \n"
                + "a.ARRAY_ID, \n"
                + "a.ARRAY_ELEMENT_ID from \n"
                + "%s a, \n"
                + "%s b \n"
                + "where a.waveform_id = ?\n"
                + "and a.used_station_id = b.station_id ",
                TableNames.SEARCH_LINK_TABLE,
                TableNames.STATION_EPOCH_VIEW);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, waveformId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                String stationSource = rs.getString(jdx++);
                String networkCode = rs.getString(jdx++);
                int netStartDate = rs.getInt(jdx++);
                String stationCode = rs.getString(jdx++);
                String description = rs.getString(jdx++);
                double lat = rs.getDouble(jdx++);
                double lon = rs.getDouble(jdx++);
                Double elev = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    elev = null;
                }
                double beginTime = rs.getDouble(jdx++);
                double endTime = rs.getDouble(jdx++);
                int networkId = rs.getInt(jdx++);
                int stationId = rs.getInt(jdx++);
                int stationEpochId = rs.getInt(jdx++);
                Long arrayId = rs.getLong(jdx++);
                if (rs.wasNull()) {
                    arrayId = null;
                }
                Long arrayElementId = rs.getLong(jdx++);
                if (rs.wasNull()) {
                    arrayElementId = null;
                }
                return new ApplicationStationInfo(stationSource,
                        networkCode,
                        netStartDate,
                        stationCode,
                        description,
                        lat,
                        lon,
                        elev,
                        beginTime,
                        endTime,
                        networkId,
                        stationId,
                        stationEpochId,
                        arrayId,
                        arrayElementId);
            }
            throw new IllegalStateException(String.format("Failed to retrieve StationInfo for waveform_id (%d)!", waveformId));
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

    private StationEpoch retrieveEpoch(PreparedStatement stmt, Long arrayId) throws SQLException {
        ResultSet rs = null;
        try {
            stmt.setLong(1, arrayId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jdx = 1;
                String stationSource = rs.getString(jdx++);
                String networkCode = rs.getString(jdx++);
                int netStartDate = rs.getInt(jdx++);
                String stationCode = rs.getString(jdx++);
                String description = rs.getString(jdx++);
                double lat = rs.getDouble(jdx++);
                double lon = rs.getDouble(jdx++);
                Double elev = rs.getDouble(jdx++);
                if (rs.wasNull()) {
                    elev = null;
                }
                double beginTime = rs.getDouble(jdx++);
                double endTime = rs.getDouble(jdx++);
                int networkId = rs.getInt(jdx++);
                int stationId = rs.getInt(jdx++);
                int stationEpochId = rs.getInt(jdx++);
                Long arrayElementId = rs.getLong(jdx++);
                if (rs.wasNull()) {
                    arrayElementId = null;
                }
                return new StationEpoch(stationSource,
                        networkCode,
                        netStartDate,
                        stationCode,
                        description,
                        lat,
                        lon,
                        elev,
                        beginTime,
                        endTime,
                        networkId,
                        stationId,
                        stationEpochId,
                        arrayId,
                        arrayElementId);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
