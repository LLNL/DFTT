/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.dftt.core.dataAccess.database.oracle.waveformUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.FileSystemException;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import llnl.gnem.dftt.core.util.FileUtil.FileManager;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.io.BinaryData;
import llnl.gnem.dftt.core.waveform.io.BinaryDataReader;
import llnl.gnem.dftt.core.waveform.io.e1IO;
import llnl.gnem.dftt.core.waveform.io.s4IO;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class WaveformUtil {

    private static final String DB_DATA_GRP = "210";
    private static final String SEP_CHAR = "/";  //Specify unix separator. This must be used even if code is running on Windows.

    public static int[] getSeismogramDataAsIntArray(String fname, int foff, int nsamp, String datatype) throws Exception {
        String mappedName = DriveMapper.getInstance().maybeMapPath(fname);
        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            BinaryData bd = bdr.readData(mappedName, foff, nsamp);
            return bd.getIntData();
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }

    }

    public static int[] getSeismogramDataAsIntArray(String dir, String dfile, int foff, int nsamp, String datatype) throws Exception {
        String fname = dir + '/' + dfile;
        String mappedName = DriveMapper.getInstance().maybeMapPath(fname);
        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            BinaryData bd = bdr.readData(mappedName, foff, nsamp);
            return bd.getIntData();
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }

    }

    public static float[] getSeismogramDataAsFloatArray(String dir, String dfile, int foff, int nsamp, String datatype) throws Exception {
        String fname = dir + '/' + dfile;
        String mappedName = DriveMapper.getInstance().maybeMapPath(fname);

        BinaryDataReader bdr = BinaryDataReader.getReader(datatype);
        if (bdr != null) {
            BinaryData bd = bdr.readData(mappedName, foff, nsamp);
            return bd.getFloatData();
        } else {
            throw new IllegalStateException("No BinaryDataReader was instantiated. Could not read data.");
        }

    }

    public static String writeDfile(String filename, int[] data, int numSamples) throws IOException {
        String datatype = "e1";
        boolean wroteE1;
        try {
            wroteE1 = e1IO.WriteIntData(data, numSamples, filename);
        } catch (IOException e) {
            wroteE1 = false;
            ApplicationLogger.getInstance().log(Level.FINER, "Failed writing e1 data!", e);
        }
        if (!wroteE1) {
            datatype = "s4";
            s4IO.writeIntData(filename, data);
        }

        String os = System.getProperty("os.name");
        if (!os.contains("Windows")) {
            FileManager.updateFilePermissions(filename, DB_DATA_GRP);
        }
        return datatype;
    }

    public static String makeSegmentOutputDirectory(long evid, TimeT evtime, String baseDirectory) throws IOException {

        String subdir = String.format("%04d%s%02d%s%09d%s", evtime.getYear(), SEP_CHAR, evtime.getMonth(), SEP_CHAR, evid, SEP_CHAR);
        String targetDir = baseDirectory + SEP_CHAR + subdir;
        String mappedDir = DriveMapper.getInstance().maybeMapPath(targetDir);
        File directory = new File(mappedDir);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new FileSystemException(String.format("Failed creating directory (%s)!", targetDir));
            }
        }
        String os = System.getProperty("os.name");
        if (!os.contains("Windows")) {
            FileManager.updateDirPermissions(baseDirectory, subdir, DB_DATA_GRP);
        }

        return targetDir;
    }

    public static CssSeismogram getSingleSeismogramFromCssTable(int wfid, String wfdiscTable, Connection conn) throws Exception {

        String sql = String.format("select sta, chan,time,nsamp,samprate,calib,calper,datatype,dir,dfile, foff from %s where wfid = ? ", wfdiscTable);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, wfid);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        }
    }
}

