package llnl.gnem.core.waveform.continuous.segments;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.continuous.StationSelectionMode;

public interface SegmentInfoDAO {

    ArrayList<ChannelSegmentCatalog> getChannelSegments(StreamKey channel,StationSelectionMode mode) throws Exception;

    Collection<String> getAvailableNetworks(StationSelectionMode mode) throws Exception;

    Collection<String> getAvailableStations(String net,StationSelectionMode mode) throws Exception;

    Collection<String> getAvailableChannels(String net, String sta, StationSelectionMode mode) throws Exception;

    Collection<String> getAvailableLocids(String net, String sta, String chan, StationSelectionMode mode) throws Exception;

    TimeT getLastSampleTime(String netCode, String stationCode, String chanCode) throws Exception;
}
