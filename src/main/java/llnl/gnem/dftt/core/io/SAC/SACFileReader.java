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
package llnl.gnem.dftt.core.io.SAC;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import llnl.gnem.dftt.core.io.FileDataSource;
import static llnl.gnem.dftt.core.io.FileDataSource.CSS_F4;
import static llnl.gnem.dftt.core.io.FileDataSource.CSS_S3;
import static llnl.gnem.dftt.core.io.FileDataSource.CSS_S4;
import static llnl.gnem.dftt.core.io.FileDataSource.CSS_T4;
import llnl.gnem.dftt.core.signalprocessing.Sequence;
import llnl.gnem.dftt.core.util.TimeT;


public class SACFileReader extends FileDataSource {

    // instance variables
    public SACHeader header;
    public TimeT timeT;

    public SACFileReader(String filename) throws FileNotFoundException {
        this(new File(filename));
    }

    public SACFileReader(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
        path = file.getAbsolutePath();
    }

    //constructor
    public SACFileReader(ObjectInput stream) {
        try {
            header = new SACHeader(stream);

            totalNumSamples = header.npts;
            nextSample = 0;
            numSamplesRemaining = totalNumSamples;
            station = (header.kstnm).trim();
            channel = (header.kcmpnm).trim();
            samplingRate = 1.0 / ((double) header.delta);
            timeT = new TimeT(header.nzyear,
                    header.nzjday,
                    header.nzhour,
                    header.nzmin,
                    header.nzsec,
                    header.nzmsec);
            foff = 4 * WORDS_IN_HEADER;
            timeT = timeT.add((double) header.b);
            startTime = timeT.getEpochTime();

            if (header.checkByteSwap()) {
                format = CSS_F4;
            } else {
                format = CSS_T4;
            }

        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public SACFileReader(InputStream stream) {
        try {
            header = new SACHeader(stream);

            totalNumSamples = header.npts;
            nextSample = 0;
            numSamplesRemaining = totalNumSamples;
            station = (header.kstnm).trim();
            channel = (header.kcmpnm).trim();
            samplingRate = 1.0 / ((double) header.delta);
            timeT = new TimeT(header.nzyear,
                    header.nzjday,
                    header.nzhour,
                    header.nzmin,
                    header.nzsec,
                    header.nzmsec);
            foff = 4 * WORDS_IN_HEADER;
            timeT = timeT.add((double) header.b);
            startTime = timeT.getEpochTime();

            if (header.checkByteSwap()) {
                format = CSS_F4;
            } else {
                format = CSS_T4;
            }

            initiate(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
    private static final int WORDS_IN_HEADER = 158;

    public float[] getAllSamples() {
        float[] result = new float[header.npts];
        readFloatArray(result);
        return result;
    }

    public float[] getData(ObjectInput in, SACHeader header) {
        int numSamplesToRead = header.npts;
        float[] dataArray = new float[header.npts];

        try {

            int numBytesToRead = 0;
            boolean legitFormat = false;
            switch (format) {

                // calculate #bytes to read and allocate buffer space as appropriate
                case CSS_S4:
                case CSS_T4:
                case CSS_F4:

                    numBytesToRead = numSamplesToRead * 4;
                    legitFormat = true;
                    break;

                case CSS_S3:

                    numBytesToRead = numSamplesToRead * 3;
                    legitFormat = true;
                    break;

                default:

                    System.err.println("io.FileDataSource:  unsupported format " + format);

            }

            buffer = new byte[numBytesToRead];

            in.read(buffer, 0, numBytesToRead);

            DataInputStream dis;
            if (legitFormat) {
                dis = new DataInputStream(new ByteArrayInputStream(buffer));
            } else {
                throw new IOException("io.SacFileReader#getData:  Unsupported format: " + format);
            }

            int offset = 0, ib = 0;
            // perform appropriate read
            switch (format) {

                case CSS_S4:

                    for (int i = 0; i < numSamplesToRead; i++) {
                        dataArray[i + offset] = (float) dis.readInt();
                    }
                    numSamplesRemaining -= numSamplesToRead;
                    nextSample += numSamplesToRead;
                    break;

                case CSS_T4:

                    for (int i = 0; i < numSamplesToRead; i++) {
                        dataArray[i + offset] = dis.readFloat();
                    }
                    numSamplesRemaining -= numSamplesToRead;
                    nextSample += numSamplesToRead;
                    break;

                case CSS_S3:

                    ib = 0;
                    for (int i = 0; i < numSamplesToRead; i++) {

                        if ((0x80 & buffer[ib]) == 0x80) {
                            dataArray[i + offset] = (float) (((0xff) << 24)
                                    + ((buffer[ib++] & 0xff) << 16)
                                    + ((buffer[ib++] & 0xff) << 8)
                                    + ((buffer[ib++] & 0xff)));
                        } else {
                            dataArray[i + offset] = (float) (((0x00) << 24)
                                    + ((buffer[ib++] & 0xff) << 16)
                                    + ((buffer[ib++] & 0xff) << 8)
                                    + ((buffer[ib++] & 0xff)));
                        }

                    }
                    numSamplesRemaining -= numSamplesToRead;
                    nextSample += numSamplesToRead;

                    break;

                case CSS_F4:

                    ib = 0;
                    for (int i = 0; i < numSamplesToRead; i++) {
                        dataArray[i + offset] = Float.intBitsToFloat( // float conversion
                                ((buffer[ib++] & 0xff)) + // per Phil Crotwell's
                                ((buffer[ib++] & 0xff) << 8) + // suggestion
                                ((buffer[ib++] & 0xff) << 16)
                                + ((buffer[ib++] & 0xff) << 24));
                    }
                    numSamplesRemaining -= numSamplesToRead;
                    nextSample += numSamplesToRead;

                    break;

                default:

                    System.err.println("io.FileDataSource:  unsupported format");

            }
            dis.close();

        } catch (IOException ioe) {
            System.err.println("io.FileDataSource  " + ioe.getMessage());
        }
        return dataArray;
    }

    public SACHeader getHeader() {
        return header;
    }

    public int getNumPtsRemaining() {
        return (int) numSamplesRemaining;
    }       // legacy

    public TimeT getStartTime() {
        return timeT;
    }

    public void readFloatArray(float[] samples) {                             // legacy
        getData(samples, 0, samples.length);
    }

    public Sequence readSequence(int nPtsRequested) {                         // legacy
        int n = Math.min(nPtsRequested, (int) numSamplesRemaining);
        float[] seqv = new float[n];
        getData(seqv, 0, n);
        return new Sequence(seqv);
    }

}
