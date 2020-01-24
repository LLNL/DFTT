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
