/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
