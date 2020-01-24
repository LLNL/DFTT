package llnl.gnem.core.waveform.io;

import java.io.InputStream;

/**
 * User: dodge1 Date: Feb 9, 2004 Time: 1:45:21 PM To
 * change this template use Options | File Templates.
 */
public class t4IO extends BinaryDataReader {    
    @Override
    public BinaryData readData(InputStream stream, int offset, int npts, BinaryData buffer) throws Exception {
        return readFloatData(stream, offset, buffer, false);
    }
    
    @Override
    public BinaryData getBuffer(int npts) {
        return new FloatBinaryData(npts);
    }
}
