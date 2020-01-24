/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.picking;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class PredictedPhasePick {
    private final int eventId;
    private final int associatedDetectionid;
    private final double magnitude;
    private final String phase;
    private final double time;

    public PredictedPhasePick(int eventId, int associatedDetectionid, double magnitude, String phase, double time) {
        this.eventId = eventId;
        this.associatedDetectionid = associatedDetectionid;
        this.magnitude = magnitude;
        this.phase = phase;
        this.time = time;
    }

    public int getEventId() {
        return eventId;
    }

    public int getAssociatedDetectionid() {
        return associatedDetectionid;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getPhase() {
        return phase;
    }

    public double getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "PredictedPhasePick{" + "eventId=" + eventId + ", associatedDetectionid=" + associatedDetectionid + ", magnitude=" + magnitude + ", phase=" + phase + ", time=" + time + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.eventId;
        hash = 67 * hash + this.associatedDetectionid;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.magnitude) ^ (Double.doubleToLongBits(this.magnitude) >>> 32));
        hash = 67 * hash + Objects.hashCode(this.phase);
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
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
        final PredictedPhasePick other = (PredictedPhasePick) obj;
        if (this.eventId != other.eventId) {
            return false;
        }
        if (this.associatedDetectionid != other.associatedDetectionid) {
            return false;
        }
        if (Double.doubleToLongBits(this.magnitude) != Double.doubleToLongBits(other.magnitude)) {
            return false;
        }
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (!Objects.equals(this.phase, other.phase)) {
            return false;
        }
        return true;
    }
    
}
