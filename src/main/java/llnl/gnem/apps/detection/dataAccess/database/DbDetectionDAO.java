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
