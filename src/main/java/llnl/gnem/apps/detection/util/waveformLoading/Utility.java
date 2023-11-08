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
package llnl.gnem.apps.detection.util.waveformLoading;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.io.SAC.SACFile;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.io.e1IO;
import llnl.gnem.dftt.core.waveform.io.s4IO;

/**
 *
 * @author dodge1
 */
public class Utility {

    private static final String sepChar = File.separator;

    public static String makeOutputDirectory(String baseDirectory, String sta, double time) throws FileSystemException {
        TimeT segTime = new TimeT(time);
        String subdir = String.format("%s%s%04d%s%03d%s", sta, sepChar, segTime.getYear(), sepChar, segTime.getDayOfYear(), sepChar);
        String dir = baseDirectory + sepChar + subdir;
        File directory = new File(dir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new FileSystemException("Failed to create directory: " + directory.getAbsolutePath());
        }
        return dir;
    }

    public static int getNextAvailableWfid(Connection conn) throws SQLException {
        String sql = "select wfid.nextval from dual";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return -1;
                }
            }
        }
    }

    public static String writeDfile(String filename, int[] data, int numSamples) throws IOException {
        String datatype = "e1";
        boolean wroteE1;
        try {
            wroteE1 = e1IO.WriteIntData(data, numSamples, filename);
        } catch (Exception e) {
            wroteE1 = false;
            ApplicationLogger.getInstance().log(Level.FINER, "Failed writing e1 data!", e);
        }
        if (!wroteE1) {
            datatype = "s4";
            s4IO.writeIntData(filename, data);
        }
        return datatype;
    }

    public static int[] convertToInts(float[] floatData) {
        int N = floatData.length;
        int[] result = new int[N];
        float scale = 1.0f;
        float minDif = Float.MAX_VALUE;
        for (int j = 0; j < N - 1; ++j) {
            float v1 = floatData[j];
            float v2 = floatData[j + 1];
            if (v1 != v2) {
                float dif = Math.abs(v1 - v2);
                if (dif < minDif) {
                    minDif = dif;
                }
            }
        }
        if (minDif < 1) {
            scale = 1.0f / minDif;
        }
        for (int j = 0; j < N; ++j) {
            result[j] = (int) Math.round(floatData[j] * scale);
        }
        return result;
    }

    public static void writeDataBlocks(double sampRate,
            int blockSizeSeconds,
            int nsamps,
            String baseDir,
            double beginTime,
            int[] data,
            StreamKey key,
            Double calib,
            Double calper,
            DuplicateWaveformAction duplicateAction, String wfdiscTableName, Connection conn) throws DataAccessException, SQLException {
        // Split into blockSizeSeconds chunks writing each as a separate wfdisc table entry...
        double delta = 1.0 / sampRate;

        int blockSampleSize = (int) Math.round(blockSizeSeconds * sampRate);
        int minSamps = 1;
        int idx = 0;
        int[] segmentData = new int[blockSampleSize];
        while (idx + blockSampleSize < nsamps - minSamps) {
            writeChunk(baseDir, key, beginTime, idx, delta, data, segmentData,
                    blockSampleSize, sampRate, calib, calper, duplicateAction, wfdiscTableName, conn);
            idx += blockSampleSize;
        }

        //write out any remaining samples
        blockSampleSize = nsamps - idx;
        if (blockSampleSize > 0) {
            segmentData = new int[blockSampleSize];
            writeChunk(baseDir, key, beginTime, idx, delta, data, segmentData,
                    blockSampleSize, sampRate, calib, calper, duplicateAction, wfdiscTableName, conn);
        }
    }

    private static void writeChunk(String baseDir,
            StreamKey key,
            double beginTime,
            int idx,
            double delta,
            int[] data,
            int[] segmentData,
            int blockSampleSize,
            double sampRate,
            Double calib,
            Double calper,
            DuplicateWaveformAction duplicateAction, String wfdiscTableName, Connection conn) throws DataAccessException, SQLException {
        double segmentBegin = beginTime + idx * delta;
        Long existingWaveformId = Utility.findExistingWaveform(key, segmentBegin, wfdiscTableName, conn);
        if (existingWaveformId != null) {
            switch (duplicateAction) {
                case skip:
                    ApplicationLogger.getInstance().log(Level.INFO, "\t Skipping...");
                    return;
                case replaceExisting:
                    ApplicationLogger.getInstance().log(Level.INFO, "\t Replacing existing...");
                    removeSeismogram(existingWaveformId, wfdiscTableName, conn);
                    break;
                case Ignore://fall out of switch...
                    ApplicationLogger.getInstance().log(Level.INFO, "\t File exists already, but writing duplicate segment...");
            }
        }
        try {
            System.arraycopy(data, idx, segmentData, 0, blockSampleSize);
            Utility.writeSegment(baseDir, key, segmentData, segmentBegin, blockSampleSize, sampRate, calib, calper, wfdiscTableName, conn);
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed writing chunk!", ex);
        }
    }

    private static void writeSegment(String baseDirectory, StreamKey key, int[] segmentData, double segmentBegin, int msamps, double sampRate, Double calib, Double calper, String wfdiscTableName, Connection conn) throws SQLException {
        TimeT time = new TimeT(segmentBegin);
        double endTime = segmentBegin + (msamps - 1) / sampRate;
        ApplicationLogger.getInstance().log(Level.INFO, String.format("\tWriting %d point block starting at %s", msamps, time));

        String directoryName = String.format("%s/%s/%04d/%03d", baseDirectory, key.getSta(), time.getYear(), time.getDayOfYear());

        // On non-windows platforms mappedPath is the same as directoryName
        String mappedPath = DriveMapper.getInstance().maybeMapPath(directoryName);
        File file = new File(mappedPath);
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (!success) {
                ApplicationLogger.getInstance().log(Level.WARNING, String.format("Failed creating directory %s!", directoryName));
                return;
            }
        }
        int wfid = getNextAvailableWfid(conn);

        String dfile = String.format("%s_%s_%d.w", key.getSta(), key.getChan(), wfid);

        File fileToWrite = new File(mappedPath, dfile);

        try {
            //Write the .w file...
            String datatype = writeDfile(fileToWrite.getAbsolutePath(), segmentData, msamps);
            writeRow(wfid, key, time, endTime, msamps, sampRate, calib, calper, datatype, mappedPath, dfile, wfdiscTableName, conn);
        } catch (IOException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed inserting row into waveform table!", ex);
        }

    }

    private static void writeRow(int wfid, StreamKey key, TimeT time, double endTime, int msamps, double sampRate, Double calib, Double calper, String datatype, String dir, String dfile, String wfdiscTableName, Connection conn) throws SQLException {
        final String sql = String.format("insert into %s values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, sysdate)", wfdiscTableName);
        if (calib == null) {
            calib = -999.0;
        }
        if (calper == null) {
            calper = -1.0;
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idx = 1;
            stmt.setInt(idx++, wfid);
            stmt.setString(idx++, key.getNet());
            stmt.setString(idx++, key.getSta());
            stmt.setString(idx++, key.getChan());
            stmt.setString(idx++, key.getLocationCode());
            stmt.setDouble(idx++, time.getEpochTime());
            stmt.setDouble(idx++, endTime);
            stmt.setInt(idx++, time.getJdate());
            stmt.setInt(idx++, msamps);
            stmt.setDouble(idx++, sampRate);

            stmt.setDouble(idx++, calib);
            stmt.setDouble(idx++, calper);
            stmt.setString(idx++, datatype);
            stmt.setString(idx++, dir);
            stmt.setString(idx++, dfile);
            stmt.setInt(idx++, 0);
            stmt.execute();
            conn.commit();
        }
    }

    private static Long findExistingWaveform(StreamKey key, double segmentBegin, String wfdiscTableName, Connection conn) throws SQLException {
        String sql = String.format("select wfid from %s where net = ? and sta = ? and chan = ? and locid = ? and time between ? and ?", wfdiscTableName);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key.getNet());
            stmt.setString(2, key.getSta());
            stmt.setString(3, key.getChan());
            stmt.setString(4, key.getLocationCode());
            stmt.setDouble(5, segmentBegin - 1.0);
            stmt.setDouble(6, segmentBegin + 1.0);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }

    private static void removeSeismogram(long wfid, String wfdiscTableName, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(String.format("delete from %s where wfid = ?", wfdiscTableName))) {
            stmt.setLong(1, wfid);
            stmt.execute();
        }
    }
}
