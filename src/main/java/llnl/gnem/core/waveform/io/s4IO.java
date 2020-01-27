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
package llnl.gnem.core.waveform.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import llnl.gnem.core.waveform.Utility;

/**
 * User: dodge1 Date: Feb 6, 2004 Time: 3:15:04 PM To
 * change this template use Options | File Templates.
 */
public class s4IO extends BinaryDataReader {

    /**
     * Read data from a .w file.
     *
     * @param stream
     * @param byteOffset The offset in the file in bytes.
     * @param npts The number of points to read.
     * @param buffer
     * @return A BinaryData object holding the waveform data read from the file.
     * @throws IOException Exception thrown for any kind of IO error.
     */
    @Override
    public BinaryData readData(InputStream stream, int byteOffset, int npts, BinaryData buffer) throws IOException {
        return readIntData(stream, byteOffset, buffer);
    }

    @Override
    public BinaryData getBuffer(int npts) {
        return new IntBinaryData(npts);
    }

    public static BinaryData readIntData(InputStream stream, int offset, BinaryData buffer) throws IOException {
        byte[] raw = readRawData(stream, offset, buffer.size() * 4);
        buffer.fillAsInts(raw, false);
        return buffer;
    }

     public static long[] readUnsignedData(InputStream stream, int offset, int totalBytes) throws IOException {
        int requiredBytes = totalBytes - offset;
        byte[] raw = readRawData(stream, offset, requiredBytes);
        return Utility.byteArrayToLongArray_4bytes_per_long(raw);
    }

    public static void writeIntData(String filename, int[] data) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filename, "rw");
        byte[] dataBuffer = Utility.intArrayToByteArray(data, false);
        raf.write(dataBuffer);
        raf.close();
    }
}
