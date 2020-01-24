/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    
    
}
