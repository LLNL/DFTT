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
package llnl.gnem.dftt.core.io;

import java.io.*;

/**
 *
 * @author Dave Harris
 * @version
 */
public class FileDataSource extends AbstractDataSource {

    protected String path;
    protected InputStream fis;
    protected int format;
    protected byte[] buffer;              // temporary buffer for reading data, persists between reads
    protected long foff;                // offset to first byte of data (for formats with headers, offsets)
    public static final int CSS_T4 = 1, CSS_S4 = 2, CSS_S3 = 3, CSS_F4 = 4;

    /**
     * Creates new FileDataSource
     */
    public FileDataSource() {
        path = null;
        format = 0;
        buffer = null;
        fis = null;
        foff = 0;
    }

    public FileDataSource(String path, int format, int firstDataByte) {

        this.path = path;
        this.format = format;
        buffer = null;
        fis = null;
        foff = firstDataByte;

    }

    @Override
    public void initiate() {
        super.initiate();
        close();
        try {
            initiate(new FileInputStream(path));
            fis.skip(foff);   // skip bytes, as necessary
        } catch (IOException ioe) {
            System.err.println("io.FileDataSource.initiate():  " + ioe.getMessage());
            fis = null;
        }
    }

    protected void initiate(InputStream fis) {
        this.fis = fis;
    }

    @Override
    public void close() {
        if (fis != null) {
            try {
                fis.close();
                fis = null;
            } catch (IOException ioe) {
                System.err.println("io.FileDataSource.close():  " + ioe.getMessage());
            }
        }
    }

    @Override
    public void skipSamples(long numSamples) {

        long numSamplesToSkip = Math.min(numSamples, numSamplesRemaining);
        try {

            switch (format) {

                case CSS_S4:
                case CSS_T4:
                case CSS_F4:

                    fis.skip(4 * numSamplesToSkip);
                    break;

                case CSS_S3:

                    fis.skip(3 * numSamplesToSkip);
                    break;

                default:

                    System.err.println("io.FileDataSource:  unsupported format");

            }

            numSamplesRemaining -= numSamplesToSkip;
            nextSample += numSamplesToSkip;

        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

    }

    @Override
    public void getData(float[] dataArray, int offset, int numRequested) {

        //  compute number of samples and bytes to read
        int numBytesToRead = 0;
        int numSamplesToRead = (int) Math.min(numRequested, numSamplesRemaining);
        DataInputStream dis = null;
        boolean legitFormat = false;
        int ib;

        try {

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

            if (buffer == null) {
                buffer = new byte[numBytesToRead];
            } else if (buffer.length < numBytesToRead) {
                buffer = new byte[numBytesToRead];
            }
            if (fis == null) {
                initiate();
            }
            fis.read(buffer, 0, numBytesToRead);

            if (legitFormat) {
                dis = new DataInputStream(new ByteArrayInputStream(buffer));
            }

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
                                ((buffer[ib++] & 0xff))
                                + // per Phil Crotwell's
                                ((buffer[ib++] & 0xff) << 8)
                                + // suggestion
                                ((buffer[ib++] & 0xff) << 16)
                                + ((buffer[ib++] & 0xff) << 24));
                    }
                    numSamplesRemaining -= numSamplesToRead;
                    nextSample += numSamplesToRead;

                    break;

                default:

                    System.err.println("io.FileDataSource:  unsupported format");

            }

            if (dis != null) {
                dis.close();
            }

        } catch (IOException ioe) {
            System.err.println("io.FileDataSource  " + ioe.getMessage());
        }

        if (numSamplesToRead < numRequested) {
            for (int i = numSamplesToRead; i < numRequested; i++) {
                dataArray[i + offset] = 0.0f;
            }
        }

    }

    public String getfilename() {
        return path;
    }

    @Override
    public void print(PrintStream ps) {
        ps.println("io.FileDataSource:");
        super.print(ps);
    }

}
