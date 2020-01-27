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
package llnl.gnem.apps.detection.core.dataObjects;

import com.oregondsp.util.TimeStamp;
import java.util.Objects;
import llnl.gnem.apps.detection.util.SubstitutionReason;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.TimeT;
import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: Oct 8, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class Trigger {

    /**
     * @return the centroidOffset
     */
    public double getCentroidOffset() {
        return centroidOffset;
    }

    private final int triggerid;
    private final int runid;
    private final int detectorid;
    private final DetectorType detectorType;
    private final float maxDetStat;
    private final TimeStamp triggerTime;
    private final boolean processed;
    private final boolean rejected;
    private final int srcDetectorid;
    private final int srcTriggerid;
    private final SubstitutionReason reason;
    private final double signalDuration;
    private final int rawTriggerIndex;
    private final double centroidOffset;

    public Trigger(int triggerid, 
            int runid, 
            int detectorid, 
            DetectorType detectorType, 
            float maxDetStat, 
            TimeStamp triggerTime, 
            boolean processed, 
            boolean rejected, 
            int srcDetectorid, 
            int srcTriggerid, 
            SubstitutionReason reason, 
            double signalDuration,
            int rawTriggerIndex,
            double centroidOffset) {
        this.triggerid = triggerid;
        this.runid = runid;
        this.detectorid = detectorid;
        this.detectorType = detectorType;
        this.maxDetStat = maxDetStat;
        this.triggerTime = triggerTime;
        this.processed = processed;
        this.rejected = rejected;
        this.srcDetectorid = srcDetectorid;
        this.srcTriggerid = srcTriggerid;
        this.reason = reason;
        this.signalDuration = signalDuration;
        this.rawTriggerIndex = rawTriggerIndex;
        this.centroidOffset = centroidOffset;
    }

   
    @Override
    public String toString() {
        TimeT tmp = new TimeT(triggerTime.epochAsDouble());
        return String.format("Trigger %d by %s detector %d at time %s with detection statistic %9.4f",
                triggerid, detectorType, detectorid, tmp.toString(), maxDetStat);
    }

    public float getMaxDetStat() {
        return maxDetStat;
    }

    public TimeStamp getTriggerTime() {
        return triggerTime;
    }

    public int getTriggerid() {
        return triggerid;
    }

    public int getRunid() {
        return runid;
    }

    public int getDetectorid() {
        return detectorid;
    }

    public boolean isProcessed() {
        return processed;
    }

    public boolean isRejected() {
        return rejected;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    /**
     * @return the srcDetectorid
     */
    public int getSrcDetectorid() {
        return srcDetectorid;
    }

    /**
     * @return the srcTriggerid
     */
    public int getSrcTriggerid() {
        return srcTriggerid;
    }

    /**
     * @return the reason
     */
    public SubstitutionReason getReason() {
        return reason;
    }

    /**
     * @return the signalDuration
     */
    public double getSignalDuration() {
        return signalDuration;
    }
    
    public Epoch getEpoch()
    {
        return new Epoch(triggerTime.epochAsDouble(), triggerTime.epochAsDouble() + signalDuration);
    }

    public int getRawTriggerIndex() {
        return rawTriggerIndex;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.triggerid;
        hash = 89 * hash + this.runid;
        hash = 89 * hash + this.detectorid;
        hash = 89 * hash + Objects.hashCode(this.detectorType);
        hash = 89 * hash + Float.floatToIntBits(this.maxDetStat);
        hash = 89 * hash + Objects.hashCode(this.triggerTime);
        hash = 89 * hash + (this.processed ? 1 : 0);
        hash = 89 * hash + (this.rejected ? 1 : 0);
        hash = 89 * hash + this.srcDetectorid;
        hash = 89 * hash + this.srcTriggerid;
        hash = 89 * hash + Objects.hashCode(this.reason);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.signalDuration) ^ (Double.doubleToLongBits(this.signalDuration) >>> 32));
        hash = 89 * hash + this.rawTriggerIndex;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.centroidOffset) ^ (Double.doubleToLongBits(this.centroidOffset) >>> 32));
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
        final Trigger other = (Trigger) obj;
        if (this.triggerid != other.triggerid) {
            return false;
        }
        if (this.runid != other.runid) {
            return false;
        }
        if (this.detectorid != other.detectorid) {
            return false;
        }
        if (Float.floatToIntBits(this.maxDetStat) != Float.floatToIntBits(other.maxDetStat)) {
            return false;
        }
        if (this.processed != other.processed) {
            return false;
        }
        if (this.rejected != other.rejected) {
            return false;
        }
        if (this.srcDetectorid != other.srcDetectorid) {
            return false;
        }
        if (this.srcTriggerid != other.srcTriggerid) {
            return false;
        }
        if (Double.doubleToLongBits(this.signalDuration) != Double.doubleToLongBits(other.signalDuration)) {
            return false;
        }
        if (this.rawTriggerIndex != other.rawTriggerIndex) {
            return false;
        }
        if (Double.doubleToLongBits(this.centroidOffset) != Double.doubleToLongBits(other.centroidOffset)) {
            return false;
        }
        if (this.detectorType != other.detectorType) {
            return false;
        }
        if (!Objects.equals(this.triggerTime, other.triggerTime)) {
            return false;
        }
        if (this.reason != other.reason) {
            return false;
        }
        return true;
    }
    
    
}
