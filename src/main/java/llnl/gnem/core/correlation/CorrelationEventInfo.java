package llnl.gnem.core.correlation;

import llnl.gnem.core.seismicData.EventInfo;

/**
 *
 * @author dodge1
 */
public class CorrelationEventInfo extends EventInfo {

    private final Double depth;

    public CorrelationEventInfo(int evid ) {
        super(evid, -999, -999, -999.0, -999999999);
        this.depth = null;
    }

    public CorrelationEventInfo() {
        super(-1, -999, -999, -999.0, -999999999);
        this.depth = null;
    }
    public CorrelationEventInfo(int evid, double lat, double lon, double depth, double time) {
        super(evid, lat, lon, depth, time);
        this.depth = depth;
    }

    public CorrelationEventInfo(EventInfo info) {
        super(info.getEvid(), info.getLat(), info.getLon(), info.getDepth(), info.getTime().getEpochTime());
        depth = info.getDepth();
    }

    @Override
    public String getMapAnnotation() {
        return "unsupported";
    }

    @Override
    public Double getDepth() {
        return depth;
    }
}

