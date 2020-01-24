/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import java.util.Objects;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class OriginInfo {
    private final int evid;
    private final double lat;
    private final double lon;
    private final double depth;
    private final double time;
    private final double mag;
    private final String auth;

    public OriginInfo(int evid, double lat, double lon, double depth, double time, double mag, String auth) {
        this.evid = evid;
        this.lat = lat;
        this.lon = lon;
        this.depth = depth;
        this.time = time;
        this.mag = mag;
        this.auth = auth;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.evid;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.depth) ^ (Double.doubleToLongBits(this.depth) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.mag) ^ (Double.doubleToLongBits(this.mag) >>> 32));
        hash = 59 * hash + Objects.hashCode(this.auth);
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
        final OriginInfo other = (OriginInfo) obj;
        if (this.evid != other.evid) {
            return false;
        }
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        if (Double.doubleToLongBits(this.depth) != Double.doubleToLongBits(other.depth)) {
            return false;
        }
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mag) != Double.doubleToLongBits(other.mag)) {
            return false;
        }
        if (!Objects.equals(this.auth, other.auth)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OriginInfo{" + "evid=" + evid + ", lat=" + lat + ", lon=" + lon + ", depth=" + depth + ", time=" + new TimeT(time).toString() + ", mag=" + mag + ", auth=" + auth + '}';
    }

    public int getEvid() {
        return evid;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getDepth() {
        return depth;
    }

    public double getTime() {
        return time;
    }

    public double getMag() {
        return mag;
    }

    public String getAuth() {
        return auth;
    }
    
}
