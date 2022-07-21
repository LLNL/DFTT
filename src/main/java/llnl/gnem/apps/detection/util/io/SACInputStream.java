/*
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
package llnl.gnem.apps.detection.util.io;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

public class SACInputStream extends DataInputStream {

    public SACHeader header;
    private byte[] dataBytes;

    public SACInputStream(String filename) throws IOException {
        super(new BufferedInputStream(new FileInputStream(filename)));

        header = new SACHeader(this);
        dataBytes = null;
    }

    public int numPtsAvailable() {

        int navail = 0;
        try {
            navail = available() / 4;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return navail;
    }

    public int skipSamples(int samplesToSkip) throws IOException {
        return this.skipBytes(samplesToSkip * 4) / 4;
    }

    public int readData(float[] data) throws IOException {

        Arrays.fill(data, 0.0f);

        int nread = Math.min(data.length, numPtsAvailable());

        int requestedBytes = nread * 4;

        if (dataBytes == null) {
            dataBytes = new byte[requestedBytes];
        } else if (dataBytes.length != requestedBytes) {
            dataBytes = new byte[requestedBytes];
        }

        readFully(dataBytes);
        int ptr = 0;
        int ib = 0;
        while (ptr < nread) {
            if (header.byteOrder == ByteOrder.LITTLE_ENDIAN) {
                data[ptr++] = Float.intBitsToFloat(((dataBytes[ib++] & 0xff) << 0) + ((dataBytes[ib++] & 0xff) << 8) + ((dataBytes[ib++] & 0xff) << 16) + ((dataBytes[ib++] & 0xff) << 24));
            } else {
                data[ptr++] = Float.intBitsToFloat(((dataBytes[ib++] & 0xff) << 24) + ((dataBytes[ib++] & 0xff) << 16) + ((dataBytes[ib++] & 0xff) << 8) + ((dataBytes[ib++] & 0xff) << 0));
            }
        }

        return nread;
    }

}
