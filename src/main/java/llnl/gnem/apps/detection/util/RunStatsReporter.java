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
package llnl.gnem.apps.detection.util;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import com.oregondsp.util.TimeStamp;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.ApplicationLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Created by dodge1
 * Date: Dec 20, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class RunStatsReporter {

    public static void reportAllDetections() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            int runid = RunInfo.getInstance().getRunid();
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select a.detectionid, a.detectorid,c.detectortype,b.time,b.detection_statistic " +
                    "from detection a, trigger_record b, detector c where a.runid = ? and a.\n" +
                    "triggerid = b.triggerid and b.runid = a.runid and a.detectorid = c.detectorid order by time");
            stmt.setInt(1, runid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int detectionid = rs.getInt(1);
                int detectorid = rs.getInt(2);
                DetectorType type = DetectorType.valueOf(rs.getString(3));
                double time = rs.getDouble(4);
                String timeString = new TimeStamp(time).toString();
                float statistic = rs.getFloat(5);
                String statsLine = String.format("Detectionid=%d, Detectorid=%d, detector Type = %s, Detection Time = %s, Detection Statistic = %10.4f",
                        detectionid,detectorid,type,timeString,statistic);
                ApplicationLogger.getInstance().log(Level.INFO, statsLine);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                ConnectionManager.getInstance().checkIn(conn);
        }

    }


    public static void reportDetectionSummary() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            int runid = RunInfo.getInstance().getRunid();
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement("select detectorid, count(*) from detection where runid = ? group by detectorid order by count(*)");
            stmt.setInt(1, runid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int detectorid = rs.getInt(1);
                int count = rs.getInt(2);
                String statsLine = String.format(" Detectorid=%d, Detection count = %d", detectorid,count);
                ApplicationLogger.getInstance().log(Level.INFO, statsLine);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                ConnectionManager.getInstance().checkIn(conn);
        }

    }

}
