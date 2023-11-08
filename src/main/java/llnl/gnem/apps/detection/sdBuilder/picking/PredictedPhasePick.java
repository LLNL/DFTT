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
package llnl.gnem.apps.detection.sdBuilder.picking;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class PredictedPhasePick {
    private final int eventId;
    private final int associatedDetectionid;
    private final String stationCode;
    private final String chan;
    private final String phase;
    private final double time;

    public PredictedPhasePick(int eventId, int associatedDetectionid, String stationCode, String chan,String phase, double time) {
        this.eventId = eventId;
        this.associatedDetectionid = associatedDetectionid;
        this.stationCode = stationCode;
        this.chan = chan;
        this.phase = phase;
        this.time = time;
    }

    public int getEventId() {
        return eventId;
    }

    public int getAssociatedDetectionid() {
        return associatedDetectionid;
    }

    public String getPhase() {
        return phase;
    }

    public double getTime() {
        return time;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getChan() {
        return chan;
    }

    @Override
    public String toString() {
        return "PredictedPhasePick{" + "eventId=" + eventId + ", associatedDetectionid=" + associatedDetectionid + ", stationCode=" + stationCode + ", chan=" + chan + ", phase=" + phase + ", time=" + time + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.eventId;
        hash = 41 * hash + this.associatedDetectionid;
        hash = 41 * hash + Objects.hashCode(this.stationCode);
        hash = 41 * hash + Objects.hashCode(this.chan);
        hash = 41 * hash + Objects.hashCode(this.phase);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
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
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (!Objects.equals(this.stationCode, other.stationCode)) {
            return false;
        }
        if (!Objects.equals(this.chan, other.chan)) {
            return false;
        }
        return Objects.equals(this.phase, other.phase);
    }

    
}
