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
