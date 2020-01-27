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
package llnl.gnem.core.io.SAC;

import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.core.signalprocessing.Sequence;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.TimeT;

public class SACFileWriter {

    public SACHeader header;
    private byte[] dataBuffer;
    private final File file;
    // instance variables
    private RandomAccessFile sacout;

    public SACFileWriter(File file) throws IOException, FileNotFoundException {
        if (file.exists()) {
            if (!file.delete()) {
                throw new FileSystemException("Failed to delete file: " + file.getAbsolutePath());
            }
        }
        this.file = file;
        sacout = new RandomAccessFile(file, "rw");
        header = new SACHeader();
        header.write(sacout);
    }

    public SACFileWriter(File file, SACHeader header, Sequence S) throws IOException {
        this.file = file;
        try {
            if (file.exists()) {
                if (!file.delete()) {
                    throw new FileSystemException("Failed to delete file: " + file.getAbsolutePath());
                }
            }

            sacout = new RandomAccessFile(file, "rw");
            this.header = header;
            header.write(sacout);  // write the header values to the file
            writeSequence(S); // write the data sequence to the file
        } catch (Exception e) {
            Logger.getLogger(SACFileWriter.class.getCanonicalName()).log(Level.SEVERE, "Error Writing SAC file", e);
        } finally {
            close();          // close the file
        }
    }

    public SACFileWriter(String filename) throws IOException, FileNotFoundException {
        file = new File(filename);
        SACFileWriter sfw = new SACFileWriter(file);
        this.sacout = sfw.sacout;
        this.header = sfw.header;
        this.dataBuffer = sfw.dataBuffer;
    }

    public SACFileWriter(String filename, SACHeader header, Sequence S) throws IOException {
        file = new File(filename);
        SACFileWriter sfw = new SACFileWriter(file, header, S);
        this.sacout = sfw.sacout;
        this.header = sfw.header;
        this.dataBuffer = sfw.dataBuffer;
    }

    /**
     * WSAC1 is a utility method to allow simple rewrite of fortran and C codes
     * that use the SAC library routine:
     * <p>
     * </p>
     * call WSAC1(name, data, npts, begintime, delta, NERR)
     * <p>
     * </p>
     * replace the above statement with:
     * <p>
     * </p>
     * SACFileWriter.WSAC1(name, data, npts, begintime, delta)
     * <p>
     * </p>
     * Note the NERR value is eliminated from the call statement because it is a
     * return value, not input
     * <p>
     * </p>
     * WSAC1 writes a SAC file with certain defined header values and data
     * points
     */
    public static void WSAC1(String filename, float[] data, int npts, float begintime, float delta) {
        Sequence sequence = new Sequence(data);
        SACHeader header = new SACHeader();
        header.npts = npts;
        header.b = begintime;
        header.delta = delta;
        try {
            new SACFileWriter(filename, header, sequence);
        } catch (Exception e) {
            System.err.println("Unable to write sac file: " + e);
        }
    }

    // constructors
    /**
     * Robust constructor capable of writing SAC file to any output stream
     * (Local, HDFS, etc)
     *
     * @param outputStream Initialized output stream to target location.
     * @param header Initialized SAC header
     * @param S SAC data
     */
    public static void writeSACFile(DataOutput outputStream, SACHeader header, Sequence S) throws IOException {

        header.write(outputStream);
        writeSequence(outputStream, S);
    }

    private static void writeSequence(DataOutput outputStream, Sequence S) throws IOException {
        float[] array = S.getArray();
        for (int i = 0; i < array.length; i++) {
            float b = array[i];
            outputStream.writeFloat(b);
        }
    }

    // close file
    public void close() {
        try {
            try {
                header.write(sacout);
            } finally {
                if (sacout != null) {
                    sacout.close();
                }
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    public void reOpen() throws IOException {
        sacout = new RandomAccessFile(file, "rw");
    }

    // mutators
    public void setTime(TimeT T) {
        header.setTime(T);
    }

    public void writeFloatArray(float[] f) throws IOException {
        long length = sacout.length();
        sacout.seek(length);    // position of waveform start

        int nbytes = f.length * 4;

        dataBuffer = new byte[nbytes];

        int it;
        for (int i = 0; i < f.length; i++) {
            it = Float.floatToIntBits(f[i]);
            dataBuffer[4 * i] = (byte) (it >> 24);
            dataBuffer[4 * i + 1] = (byte) (it >> 16);
            dataBuffer[4 * i + 2] = (byte) (it >> 8);
            dataBuffer[4 * i + 3] = (byte) (it);
        }

        sacout.write(dataBuffer);//, 0, nbytes);

        setSequenceHeaderValues(f); // ensure that the header values agree with the actual data
    }

    // write header
    public void writeHeader() {
        header.write(sacout);
    }

    // sequence writer
    public void writeSequence(Sequence S) throws IOException {
        writeFloatArray(S.getArray());
    }

    /**
     * modify the header values npts, e, depmax, depmin, depmen based on
     * timeseries data
     */
    private void correctHeaderValues(Sequence sequence, SACHeader sacHeader) throws IOException {
        sacHeader.npts = (int) (sacout.length() - 632) / 4; // the sac header is 632 bytes (158 * 4) : this assumes the header has been written

        float npts2 = (float) sequence.length(); // the number of new data points added to the sac file
        float npts1 = (float) sacHeader.npts - npts2; // how many points were present before the current write

        float depmen1 = sacHeader.depmen; // the previous mean value of the data
        float depmen2 = sequence.mean(); // the mean of the newly added data

        sacHeader.depmen = depmen1 * (npts1 / (npts1 + npts2)) + depmen2 * (npts2 / (npts1 + npts2)); // the new mean

        sacHeader.validate();

        sacHeader.depmax = Math.max(sacHeader.depmax, sequence.max()); // the max of the whole data set
        sacHeader.depmin = Math.min(sacHeader.depmin, sequence.min()); // the min of the whole data set
    }

    private void setSequenceHeaderValues(float[] f) throws IOException {
        //TODO debugging below
        // modify the header values npts, e, depmax, depmin, depmen
        Sequence sequence = new Sequence(f);
        header.npts = (int) (sacout.length() - 632) / 4; // the sac header is 632 bytes (158 * 4) : this assumes the header has been written

        float npts2 = (float) f.length; // the number of new data points added to the sac file
        float npts1 = (float) header.npts - npts2; // how many points were present before the current write

        float depmen1 = header.depmen; // the previous mean value of the data
        float depmen2 = sequence.mean(); // the mean of the newly added data

        header.depmen = depmen1 * (npts1 / (npts1 + npts2)) + depmen2 * (npts2 / (npts1 + npts2)); // the new mean

        header.validate();

        header.depmax = Math.max(header.depmax, sequence.max()); // the max of the whole data set
        header.depmin = Math.min(header.depmin, sequence.min()); // the min of the whole data set

        //todo might want to replace below with header.write(sacout) to avoid bugs
        sacout.seek(79 * 4);             // position of npts in header
        sacout.writeInt(header.npts);    // write to file

        sacout.seek(7 * 4);             // position of e in header
        sacout.writeFloat(header.e);     // write to file

        sacout.seek(3 * 4);             // position of depmax in header
        sacout.writeFloat(header.depmax);    // write to file

        sacout.seek(2 * 4);             // position of depmin in header
        sacout.writeFloat(header.depmin);    // write to file*/

        sacout.seek(56 * 4);             // position of depmen in header
        sacout.writeFloat(header.depmen);    // write to file*/

        sacout.seek(sacout.length());
    }

}
