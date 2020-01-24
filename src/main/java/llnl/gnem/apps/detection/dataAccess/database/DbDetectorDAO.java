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

}
