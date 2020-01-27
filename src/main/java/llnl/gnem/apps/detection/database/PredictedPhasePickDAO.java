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
package llnl.gnem.apps.detection.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePick;
import llnl.gnem.core.database.ConnectionManager;

/**
 *
 * @author dodge1
 */
public class PredictedPhasePickDAO {
    
    private static final double COINCIDENCE_THRESHOLD_SECONDS = 10.0;
    private PredictedPhasePickDAO() {
    }
    
    public static PredictedPhasePickDAO getInstance() {
        return PredictedPhasePickDAOHolder.INSTANCE;
    }
    
    private static class PredictedPhasePickDAOHolder {
        
        private static final PredictedPhasePickDAO INSTANCE = new PredictedPhasePickDAO();
    }
    
    public Collection<PredictedPhasePick> getPredictedPicks(int runid, int detectorid) throws SQLException {
        Collection<PredictedPhasePick> result = new ArrayList<>();
        String sql = String.format("select a.detectionid, \n"
                + "d.evid,\n"
                + "d.mag, \n"
                + "d.ptime, \n"
                + "d.stime from %s a, %s b, %s c, %s d\n"
                + "where a.detectorid = ?\n"
                + "and a.runid = ?\n"
                + "and a.runid = b.runid\n"
                + "and a.triggerid = c.triggerid\n"
                + "and b.CONFIGID = d.configid\n"
                + "and d.PTIME between c.time - %f and c.time + %f", 
                "detection", 
                "framework_run", 
                "trigger_record", 
                "event_station_times",
                COINCIDENCE_THRESHOLD_SECONDS,
                COINCIDENCE_THRESHOLD_SECONDS);
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,detectorid);
                stmt.setInt(2, runid);
                try(ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        int jdx = 1;
                        int detectionid = rs.getInt(jdx++);
                        int evid = rs.getInt(jdx++);
                        double magnitude = rs.getDouble(jdx++);
                        double ptime = rs.getDouble(jdx++);
                        double stime = rs.getDouble(jdx++);
                        result.add(new PredictedPhasePick(evid, detectionid, magnitude, "P", ptime));
                        result.add(new PredictedPhasePick(evid, detectionid, magnitude, "S", stime));
                    }
                }
            }
            return result;
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }
}
