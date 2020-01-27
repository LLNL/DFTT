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

/**
 * User: dodge1
 * Date: Feb 6, 2004
 * Time: 3:15:04 PM
 * To change this template use Options | File Templates.
 */
public class s3IO extends BinaryDataReader {

    /**
     * Read data from a .w file.
     *
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
        byte[] read = readRawData(stream, byteOffset, buffer.size() * 3);

        int ib = 0;
        for (int i = 0; i < buffer.size(); i++) {

            if ((0x80 & read[ib]) == 0x80) {
                buffer.setInt(i,
                        (((0xff) << 24)
                        + ((read[ib++] & 0xff) << 16)
                        + ((read[ib++] & 0xff) << 8)
                        + ((read[ib++] & 0xff))));
            } else {
                buffer.setInt(i,
                        (((read[ib++] & 0xff) << 16)
                        + ((read[ib++] & 0xff) << 8)
                        + ((read[ib++] & 0xff))));
            }

        }

        return buffer;
    }
}
