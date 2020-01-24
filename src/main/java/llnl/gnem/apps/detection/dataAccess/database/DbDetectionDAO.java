package llnl.gnem.apps.detection.dataAccess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ShortDetectionSummary;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectionDAO;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.Epoch;

public abstract class DbDetectionDAO implements DetectionDAO {

    @Override
    public Collection<ShortDetectionSummary> getDetectionsInTimeInterval(int configid, Epoch epoch) throws DataAccessException {
        try {
            return getDetectionsInTimeIntervalP(configid, epoch);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private Collection<ShortDetectionSummary> getDetectionsInTimeIntervalP(int configid, Epoch epoch) throws SQLException {
        Collection<ShortDetectionSummary> result = new ArrayList<>();
        String sql = String.format("select a.detectorid, a.detectionid, time, detection_statistic\n"
                + "  from %s a, %s b\n"
                + " where a.detectionid in\n"
                + "       (select detectionid\n"
                + "          from %s a, %s b, %s c\n"
                + "         where configid = ?\n"
                + "           and a.runid = b.runid\n"
                + "           and b.triggerid = c.triggerid\n"
                + "           and time between ? and ?)\n"
                + "   and a.triggerid = b.triggerid", 
                TableNames.getDetectionTable(), 
                TableNames.getTriggerRecordTable(), 
                TableNames.getFrameworkRunTable(), 
                TableNames.getDetectionTable(), 
                TableNames.getTriggerRecordTable());
        Connection conn = null;
        try {
            Connections connections = DetectionDAOFactory.getInstance().getConnections();
            conn = connections.checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, configid);
                stmt.setDouble(2, epoch.getStart());
                stmt.setDouble(3, epoch.getEnd());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int jdx = 1;
                        int detectorid = rs.getInt(jdx++);
                        int detectionid = rs.getInt(jdx++);
                        double time = rs.getDouble(jdx++);
                        double detectionStatistic = rs.getDouble(jdx++);
                        result.add(new ShortDetectionSummary(detectorid, detectionid, time, detectionStatistic));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
}
