package llnl.gnem.core.waveform.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import llnl.gnem.core.util.FileSystemException;

/**
 *
 * @author addair1
 */
public abstract class BinaryDataReader {
    private static final int MAX_RETRIES = 3;
    
    public static BinaryDataReader getReader(String dataType) {
        String classname = String.format("%s.%sIO", BinaryDataReader.class.getPackage().getName(), dataType);
        try {
            return (BinaryDataReader) Class.forName(classname).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("No BinaryDataReader exists for datatype: " + dataType + '!', e);
        }
    }

    /**
     * Read data from a .w file.
     *
     * @param filename Name of the file to read from.
     * @param byteOffset The offset in the file in bytes.
     * @param npts The number of points to read.
     * @return A BinaryData object holding the waveform data read from the file.
     * @throws IOException Exception thrown for any kind of IO error.
     */
    public BinaryData readData(String filename, int byteOffset, int npts) throws Exception {
        return readData(filename, byteOffset, getTotalBytes(filename, npts), getBuffer(npts));
    }
    
    public BinaryData readFloatData(String filename, int byteOffset, int npts) throws Exception {
        return readData(filename, byteOffset, getTotalBytes(filename, npts), new FloatBinaryData(npts));
    }
    
    public BinaryData readFloatData(byte[] buffer, int byteOffset, int npts) throws Exception {
        return readData(new ByteArrayInputStream(buffer), byteOffset, getTotalBytes(buffer.length, npts), new FloatBinaryData(npts));
    }
    
    public BinaryData readData(String filename, int byteOffset, int numberOfBytes, BinaryData buffer) throws Exception {
        FileInputStream fis = null;
        if (buffer.size() < 1) {
            throw new IllegalArgumentException("npts must be > 0!");
        }
        if (byteOffset < 0) {
            throw new IllegalArgumentException("offset must be >= 0!");
        }
        try {
            fis = testForBytesAvailable(fis, filename);
            return readData(fis, byteOffset, numberOfBytes, buffer);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    public int getTotalBytes(String filename, int npts) {
        return npts;
    }
    
    public int getTotalBytes(int available, int npts) {
        return npts;
    }
    
    public abstract BinaryData getBuffer(int npts);

    public BinaryData readFloatData(InputStream stream, int offset, BinaryData buffer, boolean swapBytes) throws IOException {
        byte[] raw = readRawData(stream, offset, buffer.size() * 4);
        buffer.fillAsFloats(raw, swapBytes);
        return buffer;
    }
    
    public static byte[] readRawData(File file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] content = new byte[(int) file.length()];
            stream.read(content);
            return content;
        }
    }

    public static byte[] readRawData(InputStream stream, int offset, int nBytes) throws IOException {
        if (nBytes < 1) {
            throw new IllegalArgumentException("npts must be > 0!");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0!");
        }

        try {
            byte[] data = readFromStream(stream, offset, nBytes);
            return data;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed reading stream", ex);
        }
    }

    private static byte[] readFromStream(InputStream stream, int offset, int nBytes) throws IOException {
        byte[] content = new byte[nBytes];
        stream.skip(offset);
        stream.read(content);
        return content;
    }

    public abstract BinaryData readData(InputStream stream, int offset, int npts, BinaryData buffer) throws Exception;

    private FileInputStream testForBytesAvailable(FileInputStream fis, String filename)
            throws InterruptedException, IOException {
        int delayMs = 100;
        fis = new FileInputStream(filename);
        int tries = 0;
        int available = fis.available();
        while (available < 1 && tries < MAX_RETRIES) {
            fis.close();
            fis = new FileInputStream(filename);
            if (tries++ > 0) {
                Thread.sleep(delayMs);
                delayMs *= 2;
            }
        }
        if (available < 1) {
            throw new FileSystemException(String.format("After %d attempts, No bytes available in FileInputStream!", MAX_RETRIES));
        }
        return fis;
    }
}