/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

import java.util.Objects;

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
        return "EventSummary{" + "eventId=" + eventId + ", originId=" + originId + ", lat=" + lat + ", lon=" + lon + ", depth=" + depth + ", time=" + time + ", etype=" + etype + ", magtype=" + magtype + ", magnitude=" + magnitude + ", hasWaveforms=" + hasWaveforms + '}';
    }
    
}
