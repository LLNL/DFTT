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
package llnl.gnem.core.util;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.Geometry.EModel;

/**
 *
 * @author ganzberger1
 */
public class AppDbUtils {

    private static double MAX_SITE_TOL = 1;

    public static void verifyContextName(String theContextName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            String sql = String.format("select contextid from %s where name = ?", TableNames.CONTEXT_STA_REMAP_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, theContextName);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException(String.format("User-supplied context(%s) is not valid!", theContextName));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    public static Map<String, String> getContextStaMap(String theContextName) throws SQLException {
        Map<String, String> result = new HashMap<String, String>();
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            String sql = String.format("select oldsta, newsta from %s a, %s b where name = ? and a.contextid = b.contextid",
                            TableNames.CONTEXT_STA_REMAP_TABLE, TableNames.CONTEXT_STA_ENTRY_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, theContextName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String oldSta = rs.getString(1);
                String newSta = rs.getString(2);
                result.put(oldSta, newSta);
            }
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }

    }

    public static String maybeRemapSta(String sta, Map<String, String> contextStaMap) {

        String temp = contextStaMap.get(sta);
        return temp != null ? temp : sta;
    }

    public static void checkForSiteConflict(String siteTable, String sta, int ondate, double lat, double lon, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(String.format("select lat, lon from %s where sta = ? and ? between ondate and offdate", siteTable));

            stmt.setString(1, sta);
            stmt.setInt(2, ondate);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double thisLat = rs.getDouble(1);
                double thisLon = rs.getDouble(2);
                double distance = EModel.getDistanceWGS84(thisLat, thisLon, lat, lon);
                if (distance > MAX_SITE_TOL) {
                    String msg = String.format("Input Site %s (%d) at (%f, %f)  conflicts with existing row  in %s. Distance = %f KM. ",
                            sta, ondate, lat, lon, siteTable, distance);

                    throw new IllegalStateException(msg);
                }
            }

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static boolean siteExists(String siteTable, String sta, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(String.format("select * from %s where sta = ? ", siteTable));

            stmt.setString(1, sta);

            rs = stmt.executeQuery();
            return rs.next();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

    }

    public static ArrayList<ContextMapEntry> getContextMapEntries(String sta) throws SQLException {
        Connection conn = null;
        PreparedStatement getContextEntryStmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            String sql = String.format("select a.contextid, oldsta, newsta, name from %s a, %s b where a.contextid = b.contextid and (oldsta = '%s' or newsta = '%s')",
                            TableNames.CONTEXT_STA_REMAP_TABLE, TableNames.CONTEXT_STA_ENTRY_TABLE, sta, sta);
            getContextEntryStmt = conn.prepareStatement(sql);
            ArrayList<ContextMapEntry> result = new ArrayList<ContextMapEntry>();
            rs = getContextEntryStmt.executeQuery();
            while (rs.next()) {
                int contextid = rs.getInt(1);
                String oldsta = rs.getString(2);
                String newsta = rs.getString(3);
                String name = rs.getString(4);
                result.add(new ContextMapEntry(contextid, name, oldsta, newsta));
            }
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (getContextEntryStmt != null) {
                getContextEntryStmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }


    public static void createValidateOrUpdateSite(String sta,
            int ondate,
            int offdate,
            double lat,
            double lon,
            double elev,
            String descrip,
            String staType,
            String refSta,
            double dnorth,
            double deast,
            String net,
            String netDescrip,
            double netStart,
            double netEnd,
            String auth) throws SQLException {
        Connection conn = null;
        CallableStatement cstmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            String sql = String.format("{call %s.create_validate_or_update_site( ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ? )}",
                            TableNames.SITE_MGR_SP_PREFIX);
            cstmt = conn.prepareCall(sql);

            cstmt.setString(1, sta);
            cstmt.setInt(2, ondate);
            cstmt.setInt(3, offdate);
            cstmt.setDouble(4, lat);
            cstmt.setDouble(5, lon);
            cstmt.setDouble(6, elev);
            cstmt.setString(7, descrip);

            cstmt.setString(8, staType);
            cstmt.setString(9, refSta);
            cstmt.setDouble(10, dnorth);
            cstmt.setDouble(11, deast);

            cstmt.setString(12, net);
            cstmt.setString(13, netDescrip);
            cstmt.setDouble(14, netStart);
            cstmt.setDouble(15, netEnd);
            cstmt.setString(16, auth);
            cstmt.execute();
            conn.commit();
        } finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    //TODO this should really be called validateWritableDirectory
        public static  void validateDirectory(String directory) throws FileSystemException {
        File theDir = new File(directory);
        if (!theDir.exists()) {
            throw new IllegalStateException(String.format("Base directory (%s) does not exist!", directory));
        }

        checkFreeSpace();
        TimeT atime = new TimeT();
        String timeString = atime.toString("yyyy.DDD.HH.mm.ss.SSS");
        String testDirName = String.format("%s/test.dir.%s", directory, timeString);
        File testDir = new File(testDirName);
        if (testDir.exists()) {
            if (!testDir.delete()) {
                throw new FileSystemException(String.format("Could not delete test directory (%s)!", testDirName));
            }
        } else {
            if (testDir.mkdirs()) {
                if (testDir.exists()) {
                    if (!testDir.delete()) {
                        throw new FileSystemException(String.format("Could not delete test directory (%s)!", testDirName));
                    }
                }
            } else {
                throw new FileSystemException(String.format("Could not create test directory (%s)!", testDirName));
            }
        }
    }
        
    /**
     * validateReadableDirectory
     * @param directory
     * @throws FileSystemException
     */
    public static void validateReadableDirectory(String directory) throws FileSystemException {
        File theDir = new File(directory);
        if (!theDir.exists()) {
            throw new IllegalStateException(String.format("Base directory (%s) does not exist!", directory));
        }
        
        if (!theDir.isDirectory()) {
            throw new FileSystemException(String.format("File (%s) is not a directory!", directory));
        }
        
        if (theDir.list().length <= 0) {
            throw new FileSystemException(String.format("Base directory (%s) is Empty!", directory));
        }
        
    }

    private static void checkFreeSpace() throws FileSystemException {
//        File f = new File(baseDirectory);
//        long usableBytes = f.getFreeSpace();
//        if (usableBytes < MIN_ALLOWABLE_FREE_SPACE)
//            throw new FileSystemException(String.format("Available space on volume is less than %d bytes", usableBytes));
    }

    //TODO: where do you really want this to go?
    
    public static class TableNames {
        static final String tmpSchema = System.getProperty("schema_name");
        static final String schema = tmpSchema != null && !tmpSchema.isEmpty() ? tmpSchema : "llnl";
        
        public static final String CONTEXT_STA_ENTRY_TABLE = schema + ".CONTEXT_STA_ENTRY";
        public static final String CONTEXT_STA_REMAP_TABLE = schema + ".CONTEXT_STA_REMAP";
        
        //Stored Procedures
        public static final String SITE_MGR_SP_PREFIX = schema + ".SITE_MGR";
    }
    
    /**
     * The utility addresses the following cases: Remove an '_' from between the Chan and the Loc. The Loc should be two
     * digits, for example change '1' to '01'.
     * 
     * If you pass in 'null', you will get null back.
     * Original motivation for this method was the UNR channel naming not supported by IRIS.
     * 
     * @param String representation of the Chan.
     * @return an LLNL formatted ChanLoc 3 digit + 2 digit (note Loc is optional)
     */
    public static String llnlFormatChan(String chan) {

        String newChan = chan;
        String newLoc = null;

        if (chan != null) {
            String[] parts = chan.split("_");

            if (parts.length == 2) {
                newChan = parts[0];
                newLoc = parts[1];

                if (newLoc.trim().length() == 1) {
                    newLoc = "0" + newLoc;

                } else if (newLoc.length() >= 3) {
                    ApplicationLogger.getInstance().log(Level.WARNING, "Cannot sucessfully process Loc for Chan: " + chan);
                    throw new IllegalStateException("Cannot sucessfully process Loc for Chan: " + chan);
                }
            } else if (parts.length == 1) {
                ApplicationLogger.getInstance().log(Level.FINE, "Could not split Chan: " + chan);
            } else {
                ApplicationLogger.getInstance().log(Level.WARNING, "Cannot sucessfully process Chan: " + chan);
            }
        }

        if (newLoc != null) {
            return newChan + newLoc;
        } else {
            return newChan;
        }
    }
}
