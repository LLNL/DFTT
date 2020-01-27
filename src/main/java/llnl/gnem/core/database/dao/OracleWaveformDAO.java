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
package llnl.gnem.core.database.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.Wfdisc;
import llnl.gnem.core.waveform.io.BinaryData;
import llnl.gnem.core.waveform.io.BinaryDataReader;
import llnl.gnem.core.waveform.merge.IntWaveform;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 * User: dodge1 Date: Feb 26, 2004 Time: 9:07:23 AM
 */
@SuppressWarnings({"unchecked"})
public class OracleWaveformDAO extends WaveformDAO {

    private static OracleWaveformDAO instance = null;
    private static final String sepChar = File.separator;

    public synchronized static OracleWaveformDAO getInstance() {
        if (instance == null) {
            instance = new OracleWaveformDAO();
        }
        return instance;
    }

    protected OracleWaveformDAO() {
    }

    public int[] getSeismogramDataAsIntArray(String dir, String dfile, int foff, int nsamp, String datatype) throws Exception {
        String fname = dir + '/' + dfile;

        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            BinaryData bd = bdr.readData(fname, foff, nsamp);
            return bd.getIntData();
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }

    }

    public float[] getSeismogramDataAsFloatArray(String dir, String dfile, int foff, int nsamp, String datatype) throws Exception {
        String fname = dir + '/' + dfile;

        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            BinaryData bd = bdr.readData(fname, foff, nsamp);
            return bd.getFloatData();
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }

    }

    public IntWaveform getWaveform(int wfid, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String wfdiscTable = "llnl.wfdisc";
        String sql = String.format("select sta,chan,dir,dfile,foff,time,samprate,nsamp,datatype from %s where wfid = ?", wfdiscTable);
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, wfid);

            rs = stmt.executeQuery();
            if (rs.next()) {
                String sta = rs.getString(1);
                String chan = rs.getString(2);

                String dir = rs.getString(3);
                String dfile = rs.getString(4);
                int foff = rs.getInt(5);
                double time = rs.getDouble(6);
                double samprate = rs.getDouble(7);
                int nsamp = rs.getInt(8);
                String datatype = rs.getString(9);

                if (foff < 0) {
                    throw new IllegalStateException(String.format("For single waveform: %s, %s, wfid = %d offset is < 0. Cannot read.", sta, chan, wfid));
                }
                int[] data = new int[nsamp];//
                Arrays.fill(data, 0);
                try {
                    data = getSeismogramDataAsIntArray(dir, dfile, foff, nsamp, datatype);
                } catch (Exception e) {
                    ApplicationLogger.getInstance().log(Level.WARNING, String.format("Failed to read waveform data for (%s, %s): Substituting zero-filled trace...", dir, dfile), e);
                }
                return new IntWaveform(wfid, time, samprate, data).trim();

            } else {
                throw new IllegalArgumentException(String.format("No data found for WFID (%d)!", wfid));
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

    @Override
    public CssSeismogram getSeismogram(Wfdisc wfdisc) throws InterruptedException {
        String dir = wfdisc.getDir();
        String dfile = wfdisc.getDfile();
        int foff = wfdisc.getFoff();
        int nsamp = wfdisc.getNsamp();
        String datatype = wfdisc.getDatatype();
        BinaryData data = getBinaryData(dir, dfile, foff, nsamp, datatype);
        return new CssSeismogram(wfdisc, data);
    }

    public CssSeismogram getSingleSeismogram(int wfid, String wfdiscTable, Connection conn) throws Exception {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("select sta, chan,time,nsamp,samprate,calib,calper,datatype,dir,dfile, foff from %s where wfid = ? ", wfdiscTable);

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, wfid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String sta = rs.getString(1);
                String chan = rs.getString(2);
                double time = rs.getDouble(3);
                int nsamp = rs.getInt(4);
                double samprate = rs.getDouble(5);
                double calib = rs.getDouble(6);
                double calper = rs.getDouble(7);
                String datatype = rs.getString(8);
                String dir = rs.getString(9);
                String dfile = rs.getString(10);
                int foff = rs.getInt(11);
                float[] floatData = getSeismogramDataAsFloatArray(dir, dfile, foff, nsamp, datatype);
                return new CssSeismogram(wfid, sta, chan, floatData, samprate, new TimeT(time), calib, calper);
            } else {
                throw new IllegalArgumentException("Invalid WFID: " + wfid);
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
