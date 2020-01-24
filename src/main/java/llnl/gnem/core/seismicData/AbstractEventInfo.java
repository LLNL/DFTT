package llnl.gnem.core.seismicData;

import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.gui.map.location.LocationInfo;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.util.TimeT;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public abstract class AbstractEventInfo extends LocationInfo<Event<GeographicCoordinate>> {
    protected AbstractEventInfo(long evid,
            double lat,
            double lon,
            String name,
            double time) {
        this(evid, lat, lon, name, new TimeT(time));
    }
    
    protected AbstractEventInfo(long evid,
            double lat,
            double lon,
            String name,
            TimeT time) {
        this(evid, lat, lon, name, time, 0.0);
    }
    
    protected AbstractEventInfo(long evid,
            double lat,
            double lon,
            String name,
            double time,
            double depth) {
        this(evid, lat, lon, name, new TimeT(time), depth);
    }

    protected AbstractEventInfo(long evid,
            double lat,
            double lon,
            String name,
            TimeT time,
            double depth) {
        super(Event.fromGeo(lat, lon, depth, time, evid, name));
    }
    
    public void updateOrigin(double lat, double lon, double depth) {
        Event event = Event.fromGeo(lat, lon, depth, getTime(), getEvid(), getName());
        setLocation(event);
    }

    public long getEvid() {
        return getLocation().getId();
    }

    public TimeT getTime() {
        return getLocation().getTime();
    }

    @Override
    public String toString() {
        TimeT otime = getTime();
        String tmp = String.format("Event called %s on %s", getName(), otime.toString());
        return String.format("%s (Evid=%d, Lat = %8.4f, Lon = %8.4f)",
                tmp, getEvid(), getLat(), getLon());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEventInfo) {
            AbstractEventInfo other = (AbstractEventInfo) obj;
            return other.getEvid() == getEvid();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int)this.getEvid();
        return hash;
    }

    public Double getDepth() {
        return getLocation().getDepth();
    }

    public Point3D getPoint3D() {
        Double tmp = getDepth();
        double z = tmp == null ? 0.0 : -tmp;
        return new Point3D((float)getLon(), (float)getLat(), (float)z);
    }
}
