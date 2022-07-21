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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

public class SACFileWriter {

    RandomAccessFile file;
    SACHeader header;

    public SACFileWriter(String filename, String mode) throws IOException {

        File F = new File(filename);

        if (F.exists() && mode != null) {

            // obtain header from file

            DataInputStream dis = new DataInputStream(new FileInputStream(F));
            header = new SACHeader(dis);
            dis.close();

            // check compatibility

            if (header.leven != 1 || header.iftype != SACHeader.ITIME) {
                throw new IllegalStateException("Existing SAC file is not an evenly spaced waveform file");
            }

            // open file for reading and writing

            file = new RandomAccessFile(F, "rw");
            file.seek(F.length()); // position at file end to write more data
        }

    }

    public SACFileWriter(String filename, SACHeader header) throws IOException {

        File F = new File(filename);

        if (F.exists()) {
            F.delete(); // overwrite existing file
        }

        this.header = header;
        this.header.leven = 1;
        this.header.iftype = SACHeader.ITIME;
        this.header.npts = 0;
        this.header.b = 0.0f;
        this.header.e = 0.0f;

        // open new file for reading and writing

        file = new RandomAccessFile(F, "rw");
        this.header.writeHeader(file);

    }

    public SACFileWriter(String filename) throws IOException {

        File F = new File(filename);

        if (F.exists()) {
            F.delete(); // overwrite existing file
        }

        // construct default header

        header = new SACHeader();
        header.leven = 1;
        header.iftype = SACHeader.ITIME;
        header.npts = 0;
        header.b = 0.0f;
        header.e = 0.0f;

        // open new file for reading and writing

        file = new RandomAccessFile(F, "rw");
        header.writeHeader(file);
    }

    public SACHeader getHeader() {
        return header;
    }

    public final static int swapBytes(int val) {
        return ((val & 0xff000000) >>> 24) + ((val & 0x00ff0000) >> 8) + ((val & 0x0000ff00) << 8) + ((val & 0x000000ff) << 24);
    }

    public void writeFloatArray(float[] data) throws IOException {

        if (header.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            // Phil Crotwell's solution:  
            // careful here as dos.writeFloat() will collapse all NaN floats to
            // a single NaN value. But we are trying to write out byte swapped values
            // so different floats that are all NaN are different values in the
            // other byte order. Solution is to swap on the integer bits, not the float.
            for (float element : data) {
                file.writeInt(swapBytes(Float.floatToRawIntBits(element)));
            }
        } else {
            for (float element : data) {
                file.writeFloat(element);
            }
        }

        header.npts += data.length;
        header.e = (header.npts - 1) * header.delta;
    }

    public void close() throws IOException {

        file.seek(0);
        header.leven = 1;
        header.iftype = SACHeader.ITIME;
        header.writeHeader(file);
        file.close();

    }



}
