package llnl.gnem.core.database.dao;

import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author addair1
 */
public interface ContinuousWaveformDAO {
    public PairT<TimeT, TimeT> getBounds(StreamKey stachan) throws Exception;
    
    public TimeSeries getSeismogram(StreamKey stachan, TimeT start, TimeT end, int decimation) throws Exception;
    
    public double getSampleRate(StreamKey stachan) throws Exception;
}
