package llnl.gnem.core.seismicData;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author addair1
 */
public class EventRecord {
    private final Event<GeographicCoordinate> event;
    private final Origin prefor;
    private final Collection<Origin> origins;

    public EventRecord(double lat, double lon, double depth, double time) {
        this(-1, lat, lon, depth, time);
    }

    public EventRecord(int evid, double lat, double lon, double depth, double time) {
        event = Event.fromGeo(lat, lon, depth, new TimeT(time), evid);
        prefor = new Origin(lat, lon, depth, time);
        origins = new ArrayList<Origin>();
        origins.add(prefor);
    }
    
    public long getEvid() {
        return event.getId();
    }
    
    public double getLat() {
        return event.getCoordinate().getLat();
    }
    
    public double getLon() {
        return event.getCoordinate().getLon();
    }

    public double getDepth() {
        return event.getDepth();
    }

    public double getTime() {
        return event.getTime().getEpochTime();
    }

    public Origin getPreferredOrigin() {
        return prefor;
    }
}
