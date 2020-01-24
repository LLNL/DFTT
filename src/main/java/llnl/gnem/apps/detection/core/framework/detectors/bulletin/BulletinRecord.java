/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors.bulletin;

import java.io.Serializable;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class BulletinRecord  implements Serializable{
    private final int evid;
    private final double lat;
    private final double lon;
    private final double time;
    private final double depth;
    private final double mw;
    private final double expectedPTime;
    private static final long serialVersionUID = 1L;

    public BulletinRecord(int evid, double lat, double lon, double time, double depth, double mw, double expectedPTime) {
        this.evid = evid;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.depth = depth;
        this.mw = mw;
        this.expectedPTime = expectedPTime;
    }

    @Override
    public String toString() {
        return "BulletinRecord{" + "evid=" + evid + ", mw=" + mw + ", expectedPTime=" + new TimeT(expectedPTime) + '}';
    }
    
    

    /**
     * @return the evid
     */
    public int getEvid() {
        return evid;
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
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @return the depth
     */
    public double getDepth() {
        return depth;
    }

    /**
     * @return the mw
     */
    public double getMw() {
        return mw;
    }

    /**
     * @return the expectedPTime
     */
    public double getExpectedPTime() {
        return expectedPTime;
    }
    
}
