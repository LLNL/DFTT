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
package llnl.gnem.dftt.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 *
 * @author dodge1
 */
public class OracleDBUtil {

    public static long getNextId(Connection conn, String sequenceName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("select %s.nextval from dual", sequenceName);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } finally {

            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

        }
        return -1;
    }

    public static ArrayList<Long> getBlockOfIdValues(Connection conn, String sequenceName, int blockSize) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Long> result = new ArrayList<>();
        try {
            String sql = String.format("select %s.nextval from dual connect by level <= %d", sequenceName, blockSize);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                result.add(rs.getLong(1));
            }
            return result;
        } finally {

            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }

        }
    }

    public static long getIdCurrVal(Connection conn, String sequenceName) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(String.format("select %s.currval from dual", sequenceName));
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            {
                throw new IllegalStateException(String.format("Could not get %s.currval!", sequenceName));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * Gets the nextval from a sequence on a remote database accessed through a link.
     * @param conn
     * @param remoteSequenceExpr A string of the form: SEQUENCE_NAME.NEXTVAL@DB_LINK_NAME
     * @return
     * @throws SQLException 
     */
    public static long getRemoteSequenceVal(Connection conn, String remoteSequenceExpr) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(String.format("select %s from dual", remoteSequenceExpr));
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            {
                throw new IllegalStateException(String.format("Could not get %s!", remoteSequenceExpr));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    
    public static void setStringValue(String value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null || value.isEmpty()) {
            stmt.setNull(jdx, Types.VARCHAR);
        } else {
            stmt.setString(jdx, value);
}
    }

    public static void setDoubleValue(Double value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null || value.isInfinite() || value.isNaN() || ( value != 0 && Math.abs(value) < Float.MIN_NORMAL)) {
            stmt.setNull(jdx, Types.DOUBLE);
        } else {
            stmt.setDouble(jdx, value);
        }
    }

    public static void setIntegerValue(Integer value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null) {
            stmt.setNull(jdx, Types.INTEGER);
        } else {
            stmt.setInt(jdx, value);
        }
    }

    public static void setLongValue(Long value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null) {
            stmt.setNull(jdx, Types.INTEGER);
        } else {
            stmt.setLong(jdx, value);
        }
    }


    public static String getStringFromCursor(final ResultSet rs, int jdx) throws SQLException {
        String result = rs.getString(jdx);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    public static Double getDoubleFromCursor(ResultSet rs, int jdx) throws SQLException {
        Double result = rs.getDouble(jdx);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    public static Integer getIntegerFromCursor(ResultSet rs, int jdx) throws SQLException {
        Integer result = rs.getInt(jdx);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }
    public static Long getLongFromCursor(ResultSet rs, int jdx) throws SQLException {
        Long result = rs.getLong(jdx);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }
   
    public static void alterSessionParallelism(boolean parallel, Connection conn) throws SQLException {
        String tmp = parallel ? "enable " : "disable ";
        String sql = "alter session " + tmp + "parallel dml";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public static String getCurrentUser(Connection conn) throws SQLException {
        String sql = "select lower(user) from dual";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    return rs.getString(1);
                }
            }
        }
        return "";
    }

}
