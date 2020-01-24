/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.responseProcessing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class ResponseMetadataManager {
    private static String sensorTable = "llnl.sensor";
    private static String instrumentTable = "llnl.instrument";

    
    /**
     * Gets the responseMetaData attribute of the ResponseManager object
     *
     * @param s Description of the Parameter
     * @param conn
     * @return The responseMetaData value
     * @throws SQLException Thrown if an error occurs getting metadata from
     * database occurs.
     */
    public EnhancedResponseMetaData getEnhancedResponseMetaData(CssSeismogram s, Connection conn) throws SQLException {
      
        return getEnhancedResponseMetadata(conn, s.getSta(), s.getChan(), s.getTimeAsDouble(), s.getCalib(), s.getCalper());
    }

    private EnhancedResponseMetaData getEnhancedResponseMetadata(Connection conn, String sta, String chan, double time, double calib, double calper) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            
            String sql = String.format("select a.inid, a.dir, a.dfile, a.rsptype, a.ncalib, a.ncalper, b.calratio, "
                    + "b.calper from %s a, %s b where a.inid = b.inid and b.sta = ? and b.chan = ? and ? between b.time and b.endtime", instrumentTable, sensorTable);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, sta);
            stmt.setString(2, chan);
            stmt.setDouble(3, time);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String Dir = rs.getString(2);
                return new EnhancedResponseMetaData(rs.getInt(1), Dir + '/' + rs.getString(3),
                        ResponseType.valueOf(rs.getString(4).toUpperCase()),
                        rs.getDouble(5),
                        rs.getDouble(6),
                        rs.getDouble(7),
                        rs.getDouble(8),
                        calib,
                        calper);
            } else {
                return null;
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

    static void setSensorTable(String st) {
        sensorTable = st;
    }

    static void setInstrumentTable(String it) {
        instrumentTable = it;
    }
    public static class EnhancedResponseMetaData extends ResponseMetaData {

        private EnhancedResponseMetaData(int inid, String filename,
                ResponseType rsptype,
                double nominalCalib,
                double nominalCalper,
                double sensorCalper,
                double sensorCalratio,
                double wfdiscCalib,
                double wfdiscCalper) {
            super(filename, rsptype, nominalCalib, nominalCalper, sensorCalper, sensorCalratio, wfdiscCalib, wfdiscCalper);
            this.inid = inid;
        }
        private final int inid;

        public int getInid() {
            return inid;
        }
    }

}
