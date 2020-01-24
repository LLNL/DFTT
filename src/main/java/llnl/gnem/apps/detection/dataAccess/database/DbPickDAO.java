package llnl.gnem.apps.detection.dataAccess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.dataAccess.interfaces.PickDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.Epoch;

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

    private Collection<PhasePick> getPicksForDetectionP(int detectionid) throws SQLException {
        Collection<PhasePick> result = new ArrayList<>();
        String sql = String.format("select pickid,configid, phase,time,pick_std from %s where detectionid = ?",
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
                        Integer configid = rs.getInt(jdx++);
                        if (rs.wasNull()) {
                            configid = null;
                        }
                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        result.add(new PhasePick(pickid, configid, detectionid, phase, time, std));
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
            String sql = String.format("select b.pickid, b.configid, b.detectionid,b.phase,b.time,b.pick_std from %s a, "
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
                        Integer configid = rs.getInt(jdx++);
                        if (rs.wasNull()) {
                            configid = null;
                        }
                        int detectionid = rs.getInt(jdx++);
                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        result.add(new PhasePick(pickid, configid, detectionid, phase, time, std));
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
            sql = String.format("merge into %s a using\n"
                    + "(select ? pickid, ? configid, ? detectionid, ? phase, ? time, ? pick_std from dual) b\n"
                    + "on(a.pickid = b.pickid) when matched "
                    + "then update set a.configid = b.configid, a.detectionid = b.detectionid, a.phase = b.phase, a.time = b.time, a.pick_std = b.pick_std \n"
                    + "when not matched then\n"
                    + "insert (pickid,configid, detectionid, phase,time,pick_std,lddate) "
                    + "values(%s.nextval,b.configid,b.detectionid,b.phase,b.time,b.pick_std,sysdate)",
                    TableNames.getPhasePickTable(),
                    SequenceNames.getPickidSequenceName());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (PhasePick dpp : picks) {
                    int jdx = 1;
                    stmt.setInt(jdx++, dpp.getPickid());
                    stmt.setInt(jdx++, dpp.getConfigid());
                    Integer detectionid = dpp.getDetectionid();
                    if (detectionid == null) {
                        stmt.setNull(jdx++, Types.INTEGER);
                    } else {
                        stmt.setInt(jdx++, detectionid);
                    }
                    stmt.setString(jdx++, dpp.getPhase());
                    stmt.setDouble(jdx++, dpp.getTime());
                    stmt.setDouble(jdx++, dpp.getStd());
                    stmt.execute();
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
            String sql = String.format("select pickid, detectionid,phase,time,pick_std from %s "
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
                        Integer detectionid = rs.getInt(jdx++);
                        if (rs.wasNull()) {
                            detectionid = null;
                        }

                        String phase = rs.getString(jdx++);
                        double time = rs.getDouble(jdx++);
                        double std = rs.getDouble(jdx++);
                        result.add(new PhasePick(pickid, configid, detectionid, phase, time, std));
                    }
                }
            }
            return result;
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
}
