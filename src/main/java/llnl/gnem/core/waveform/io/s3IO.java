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
