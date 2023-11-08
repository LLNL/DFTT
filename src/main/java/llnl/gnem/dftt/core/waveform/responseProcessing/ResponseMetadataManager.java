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
package llnl.gnem.dftt.core.waveform.responseProcessing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

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
