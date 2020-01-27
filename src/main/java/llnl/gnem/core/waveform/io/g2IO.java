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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: dodge1 Date: Feb 6, 2004 Time: 3:15:04 PM To change this template use
 * Options | File Templates.
 */
public class g2IO extends BinaryDataReader {

    /**
     * Read data from a .w file.
     *
     * @param stream
     * @param offset
     * @param npts
     * @param buffer
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public BinaryData readData(InputStream stream, int offset, int npts, BinaryData buffer) throws Exception {
        return readIntData(stream, offset, buffer);
    }

    @Override
    public BinaryData getBuffer(int npts) {
        return new IntBinaryData(npts);
    }

    public static BinaryData readIntData(InputStream stream, int byteOffset, BinaryData buffer) throws IOException {
        byte[] read = readRawData(stream, byteOffset, buffer.size() * 4);
        int gain_value[] = {0, 2, 4, 7};

        int ib = 0;
        for (int idx = 0; idx < read.length - 1; idx += 2) {
            int value = ((read[idx] & 0x3f) << 8) | read[idx + 1] & 0xff;
            int to = (value - 8191) << gain_value[(0xc0 & read[idx]) >> 6];
            buffer.setInt(ib++, to);
            if (ib >= buffer.size()) {
                break;
            }
        }

        return buffer;
    }
}
