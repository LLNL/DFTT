package llnl.gnem.core.seismicData;

/**
 *
 * @author dodge1
 */
public class EventInfo extends AbstractEventInfo {

    private final Double depth;

    public EventInfo(long evid ) {
        super(evid, -999, -999, "-", -999999999);
        this.depth = null;
    }

    public EventInfo() {
        super(-1, -999, -999, "-", -999999999);
        this.depth = null;
    }
    public EventInfo(long evid, double lat, double lon, Double depth, double time) {
        super(evid, lat, lon, "-", time);
        this.depth = depth;
    }
    public EventInfo(long evid, double lat, double lon, Double depth, double time, String evName) {
        super(evid, lat, lon, evName, time);
        this.depth = depth;
    }

    public EventInfo(AbstractEventInfo info) {
        super(info.getEvid(), info.getLat(), info.getLon(), info.getName(), info.getTime());
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
