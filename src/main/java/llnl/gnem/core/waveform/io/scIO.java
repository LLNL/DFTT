package llnl.gnem.core.waveform.io;

import java.io.InputStream;

/**
 * User: dodge1 Date: Feb 9, 2004 Time: 1:45:21 PM To
 * change this template use Options | File Templates.
 */
public class scIO extends BinaryDataReader {
    public static final int SAC_HEADER_SIZE = 632;
    
    @Override
    public BinaryData readData(InputStream stream, int offset, int npts, BinaryData buffer) throws Exception {
        return readFloatData(stream, getActualOffset(offset), buffer, false);
    }
    
    @Override
    public BinaryData getBuffer(int npts) {
        return new FloatBinaryData(npts);
    }
    
    private int getActualOffset(int offset) {
        return offset == SAC_HEADER_SIZE ? offset : offset + 632;
    }
}
