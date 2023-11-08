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
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.dataAccess.interfaces.PickDAO;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.database.oracle.OracleDBUtil;
import llnl.gnem.dftt.core.database.Connections;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;

public abstract class DbPickDAO implements PickDAO {

    @Override
    public Collection<PhasePick> getPicksForDetection(int detectionid) throws DataAccessException {
        try {
            return getPicksForDetectionP(detectionid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<PhasePick> getDetectionPhasePicks(int runid, int detectorid) throws DataAccessException {
        try {
            return getDetectionPhasePicksP(runid, detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

    }

    @Override
    public void saveDetectionPhasePicks(ArrayList<PhasePick> picks, ArrayList<Integer> picksToRemove) throws DataAccessException {
        try {
            saveDetectionPhasePicksP(picks, picksToRemove);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<PhasePick> getPicks(int configid, Epoch epoch) throws DataAccessException {
        try {
            return getPicksP(configid, epoch);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public Collection<PhasePick> getAllPicks(int configid) throws DataAccessException {
        try {
            return getAllPicksP(configid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Collection<PhasePick> getPicksForDetectionP(int detectionid) throws SQLException {
        Collection<PhasePick> result = new ArrayList<>();
        String sql = String.format("select pickid,configid, agency, network, network_start_date,station_code,chan, location_code, phase,time,pick_std from %s where detectionid = ?",
                TableNames.getPhasePickTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, detectionid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int pickid = rs.getInt(jdx++);
                        Integer configid = OracleDBUtil.getIntegerFromCursor(rs, jdx++);

                        String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        String network = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        Integer netStartDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                        String sta = rs.getString(jdx++);
                        String chan = rs.getString(jdx++);
                        String locid = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        StreamKey key = new StreamKey(agency, network, netStartDate, sta, chan, locid);
                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        result.add(new PhasePick(pickid, configid, detectionid, key, phase, time, std));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<PhasePick> getDetectionPhasePicksP(int runid, int detectorid) throws SQLException {
        Collection<PhasePick> result = new ArrayList<>();
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            String sql = String.format("select b.pickid, b.configid, b.detectionid,b.agency, b.network, "
                    + " b.network_start_date,b.station_code,b.chan, b.location_code,b.phase,b.time,b.pick_std from %s a, "
                    + "%s b where  runid = ? and a.detectorid = ? and a.detectionid = b.detectionid",
                    TableNames.getDetectionTable(),
                    TableNames.getPhasePickTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                stmt.setInt(2, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int pickid = rs.getInt(jdx++);
                        Integer configid = OracleDBUtil.getIntegerFromCursor(rs, jdx++);

                        int detectionid = rs.getInt(jdx++);
                        String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        String network = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        Integer netStartDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                        String sta = rs.getString(jdx++);
                        String chan = rs.getString(jdx++);
                        String locid = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        StreamKey key = new StreamKey(agency, network, netStartDate, sta, chan, locid);
                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        result.add(new PhasePick(pickid, configid, detectionid, key, phase, time, std));
                    }
                }
            }
            return result;
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private void saveDetectionPhasePicksP(ArrayList<PhasePick> picks, ArrayList<Integer> picksToRemove) throws SQLException {
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            String sql = String.format("delete from %s where pickid = ?",
                    TableNames.getPhasePickTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int pickid : picksToRemove) {
                    stmt.setInt(1, pickid);
                    stmt.execute();
                }
            }
//            sql = String.format("delete from %s where detectionid = ? and phase = ?",
//                    TableNames.getPhasePickTable());
//            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//                for (PhasePick pp : picks) {
//                    stmt.setInt(1, pp.getDetectionid());
//                    stmt.setString(2, pp.getPhase());
//                    stmt.execute();
//                }
//            }
            

            sql = String.format("merge into %s a\n"
                    + "using (select ? pickid,\n"
                    + "              ? configid,\n"
                    + "              ? detectionid,\n"
                    + "              ? agency,\n"
                    + "              ? network,\n"
                    + "              ? network_start_date,\n"
                    + "              ? station_code,\n"
                    + "              ? chan,\n"
                    + "              ? location_code,\n"
                    + "              ? phase,\n"
                    + "              ? time,\n"
                    + "              ? pick_std\n"
                    + "         from dual) b\n"
                    + "on (a.pickid = b.pickid)\n"
                    + "when matched then\n"
                    + "  update\n"
                    + "     set a.configid           = b.configid,\n"
                    + "         a.detectionid        = b.detectionid,\n"
                    + "         a.agency             = b.network,\n"
                    + "         a.network            = b.chan,\n"
                    + "         a.network_start_date = b.network_start_date,\n"
                    + "         a.station_code       = b.station_code,\n"
                    + "         a.chan               = b.chan,\n"
                    + "         a.location_code      = b.location_code,\n"
                    + "         a.phase              = b.phase,\n"
                    + "         a.time               = b.time,\n"
                    + "         a.pick_std           = b.pick_std\n"
                    + "when not matched then\n"
                    + "  insert\n"
                    + "    (pickid,\n"
                    + "     configid,\n"
                    + "     detectionid,\n"
                    + "     agency,\n"
                    + "     network,\n"
                    + "     network_start_date,\n"
                    + "     station_code,\n"
                    + "     chan,\n"
                    + "     location_code,\n"
                    + "     phase,\n"
                    + "     time,\n"
                    + "     pick_std,\n"
                    + "     lddate)\n"
                    + "  values\n"
                    + "    (%s.nextval,\n"
                    + "     b.configid,\n"
                    + "     b.detectionid,\n"
                    + "     b.agency,\n"
                    + "     b.network,\n"
                    + "     b.network_start_date,\n"
                    + "     b.station_code,\n"
                    + "     b.chan,\n"
                    + "     b.location_code,\n"
                    + "     b.phase,\n"
                    + "     b.time,\n"
                    + "     b.pick_std,\n"
                    + "     sysdate)",
                    TableNames.getPhasePickTable(),
                    SequenceNames.getPickidSequenceName());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                 for (PhasePick dpp : picks) {
                    int jdx = 1;
                    stmt.setInt(jdx++, dpp.getPickid());
                    stmt.setInt(jdx++, dpp.getConfigid());
                    OracleDBUtil.setIntegerValue(dpp.getDetectionid(), stmt, jdx++);
                    StreamKey key = dpp.getKey();
                    OracleDBUtil.setStringValue(key.getAgency(), stmt, jdx++);
                    OracleDBUtil.setStringValue(key.getNet(), stmt, jdx++);
                    OracleDBUtil.setIntegerValue(key.getNetJdate(), stmt, jdx++);
                    OracleDBUtil.setStringValue(key.getSta(), stmt, jdx++);
                    OracleDBUtil.setStringValue(key.getChan(), stmt, jdx++);
                    OracleDBUtil.setStringValue(key.getLocationCode(), stmt, jdx++);
                    stmt.setString(jdx++, dpp.getPhase());
                    stmt.setDouble(jdx++, dpp.getTime());
                    stmt.setDouble(jdx++, dpp.getStd());
                    stmt.execute();
                    if (dpp.getPickid() < 0) {
                        long pickid = OracleDBUtil.getIdCurrVal(conn, SequenceNames.getPickidSequenceName());
                        dpp.setPickid(pickid);
                    }
                }
            }
            conn.commit();
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<PhasePick> getPicksP(int configid, Epoch epoch) throws SQLException {
        Collection<PhasePick> result = new ArrayList<>();
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            String sql = String.format("select pickid, detectionid,agency, network, network_start_date,station_code,chan, location_code,phase,time,pick_std from %s "
                    + " where  configid = ? and time between ? and ?",
                    TableNames.getPhasePickTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                stmt.setDouble(2, epoch.getStart());
                stmt.setDouble(3, epoch.getEnd());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int pickid = rs.getInt(jdx++);
                        Integer detectionid = OracleDBUtil.getIntegerFromCursor(rs, jdx++);

                        String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        String network = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        Integer netStartDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                        String sta = rs.getString(jdx++);
                        String chan = rs.getString(jdx++);
                        String locid = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        StreamKey key = new StreamKey(agency, network, netStartDate, sta, chan, locid);

                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        result.add(new PhasePick(pickid, configid, detectionid, key, phase, time, std));
                    }
                }
            }
            return result;
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<PhasePick> getAllPicksP(int configid) throws SQLException {
        Collection<PhasePick> result = new ArrayList<>();
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            String sql = String.format("select pickid, detectionid,agency, network, network_start_date,station_code,chan, location_code, phase,time,pick_std from %s "
                    + " where  configid = ?",
                    TableNames.getPhasePickTable());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int pickid = rs.getInt(jdx++);
                        Integer detectionid = OracleDBUtil.getIntegerFromCursor(rs, jdx++);

                        String agency = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        String network = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        Integer netStartDate = OracleDBUtil.getIntegerFromCursor(rs, jdx++);
                        String sta = rs.getString(jdx++);
                        String chan = rs.getString(jdx++);
                        String locid = OracleDBUtil.getStringFromCursor(rs, jdx++);
                        StreamKey key = new StreamKey(agency, network, netStartDate, sta, chan, locid);
                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        result.add(new PhasePick(pickid, configid, detectionid, key, phase, time, std));
                    }
                }
            }
            return result;
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

}
