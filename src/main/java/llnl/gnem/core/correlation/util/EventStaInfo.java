/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.correlation.util;

import java.io.Serializable;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class EventStaInfo implements Serializable {

    private final long evid;
    private final long wfid;
    private final double delta;
    private final double olat;
    private final double olon;
    private static final long serialVersionUID = 17989434118061147L;

    public EventStaInfo(long evid, long wfid, double delta, double olat, double olon) {
        this.evid = evid;
        this.wfid = wfid;
        this.delta = delta;
        this.olat = olat;
        this.olon = olon;
    }

    @Override
    public String toString() {
        return "EventStaInfo{" + "evid=" + evid + ", wfid=" + wfid + ", delta=" + delta + '}';
    }

    public long getEvid() {
        return evid;
    }

    public long getWfid() {
        return wfid;
    }

    public double getDelta() {
        return delta;
    }

    public double getOlat() {
        return olat;
    }

    public double getOlon() {
        return olon;
    }

}
