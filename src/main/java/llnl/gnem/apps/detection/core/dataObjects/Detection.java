package llnl.gnem.apps.detection.core.dataObjects;

import com.oregondsp.util.TimeStamp;
import java.util.Objects;
import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: Oct 13, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class Detection {

    private final int detectionid;
    private final Trigger trigger;

    public Detection(int detectionid, Trigger trigger) {
        this.detectionid = detectionid;
        this.trigger = trigger;
    }

    @Override
    public String toString() {
        return String.format("detectionid: %d (%s)", detectionid, trigger.toString());
    }

    public String titleString() {
        return String.format("detectionid: %d (triggerid %d) by %s detector %d at time %s (%f) with statistic %f ",
                detectionid, trigger.getTriggerid(), trigger.getDetectorType(), trigger.getDetectorid(),
                trigger.getTriggerTime(), trigger.getTriggerTime().epochAsDouble(), trigger.getMaxDetStat());
    }

    public int getDetectionid() {
        return detectionid;
    }

    public float getMaxDetStat() {
        return trigger.getMaxDetStat();
    }

    public TimeStamp getTriggerTime() {
        return trigger.getTriggerTime();
    }

    public int getTriggerid() {
        return trigger.getTriggerid();
    }

    public int getRunid() {
        return trigger.getRunid();
    }

    public int getDetectorid() {
        return trigger.getDetectorid();
    }

    public DetectorType getDetectorType() {
        return trigger.getDetectorType();
    }

    public double getSignalDuration() {
        return trigger.getSignalDuration();
    }

    public double getCentroidOffset() {
        return trigger.getCentroidOffset();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.detectionid;
        hash = 83 * hash + Objects.hashCode(this.trigger);
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
        final Detection other = (Detection) obj;
        if (this.detectionid != other.detectionid) {
            return false;
        }
        if (!Objects.equals(this.trigger, other.trigger)) {
            return false;
        }
        return true;
    }

}
