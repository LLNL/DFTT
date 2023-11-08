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
package llnl.gnem.dftt.core.dataAccess.dataObjects;

import java.util.Objects;
import llnl.gnem.dftt.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class EventSummary {

    /**
     * @return the hasWaveforms
     */
    public boolean isHasWaveforms() {
        return hasWaveforms;
    }

    private final long eventId;
    private final long originId;
    private final double lat;
    private final double lon;
    private final Double depth;
    private final double time;
    private final String etype;
    private final String magtype;
    private final Double magnitude;
            private final boolean hasWaveforms;
    
    
    /**
     * @return the eventId
     */
    public long getEventId() {
        return eventId;
    }

    /**
     * @return the originId
     */
    public long getOriginId() {
        return originId;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @return the depth
     */
    public Double getDepth() {
        return depth;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @return the etype
     */
    public String getEtype() {
        return etype;
    }

    /**
     * @return the magtype
     */
    public String getMagtype() {
        return magtype;
    }

    /**
     * @return the magnitude
     */
    public Double getMagnitude() {
        return magnitude;
    }

 
    public EventSummary(long eventId, 
            long originId, 
            double lat, 
            double lon, 
            Double depth, 
            double time, 
            String etype, 
            String magtype, 
            Double magnitude,
            boolean hasWaveforms) {
        this.eventId = eventId;
        this.originId = originId;
        this.lat = lat;
        this.lon = lon;
        this.depth = depth;
        this.time = time;
        this.etype = etype;
        this.magtype = magtype;
        this.magnitude = magnitude;
        this.hasWaveforms = hasWaveforms;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (int) (this.eventId ^ (this.eventId >>> 32));
        hash = 83 * hash + (int) (this.originId ^ (this.originId >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 83 * hash + Objects.hashCode(this.depth);
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 83 * hash + Objects.hashCode(this.etype);
        hash = 83 * hash + Objects.hashCode(this.magtype);
        hash = 83 * hash + Objects.hashCode(this.magnitude);
        hash = 83 * hash + (this.hasWaveforms ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventSummary other = (EventSummary) obj;
        if (this.eventId != other.eventId) {
            return false;
        }
        if (this.originId != other.originId) {
            return false;
        }
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (this.hasWaveforms != other.hasWaveforms) {
            return false;
        }
        if (!Objects.equals(this.etype, other.etype)) {
            return false;
        }
        if (!Objects.equals(this.magtype, other.magtype)) {
            return false;
        }
        if (!Objects.equals(this.depth, other.depth)) {
            return false;
        }
        if (!Objects.equals(this.magnitude, other.magnitude)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Event %10d: lat = %9.4f, lon = %10.4f, depth = %5.1f km, time = %5s, %s = %5.2f", eventId,lat,lon, depth,new TimeT(time).toString(), magtype, magnitude);
    }
    
}
