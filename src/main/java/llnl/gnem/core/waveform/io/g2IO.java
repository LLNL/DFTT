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
