/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.dataobjects;

/**
 *
 * @author dodge1
 */
public class EventInfo {
    private final int eventid;
    private final double minTime;
    private final double maxTime;

    public EventInfo(int eventid, double minTime, double maxTime) {
        this.eventid = eventid;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public int getEventid() {
        return eventid;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxTime() {
        return maxTime;
    }
    
    public double getDuration()
    {
        return maxTime - minTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.eventid;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.minTime) ^ (Double.doubleToLongBits(this.minTime) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.maxTime) ^ (Double.doubleToLongBits(this.maxTime) >>> 32));
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
        final EventInfo other = (EventInfo) obj;
        if (this.eventid != other.eventid) {
            return false;
        }
        if (Double.doubleToLongBits(this.minTime) != Double.doubleToLongBits(other.minTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxTime) != Double.doubleToLongBits(other.maxTime)) {
            return false;
        }
        return true;
    }
    
}
