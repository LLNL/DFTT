/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.core.correlation.CorrelationComponent;

/**
 *
 * @author dodge1
 */
public class DetectionWaveforms {
    private final int detectionid;
    private final ArrayList<CorrelationComponent> segments;
    private final ArrayList<PhasePick> associatedPicks;

    public DetectionWaveforms(int detectionid, 
            Collection<CorrelationComponent> segments, 
            Collection<PhasePick> associatedPicks)
    {
        this.detectionid = detectionid;
        this.segments = new ArrayList<>( segments);
        this.associatedPicks = new ArrayList<>(associatedPicks);
    }

    /**
     * @return the detectionid
     */
    public int getDetectionid() {
        return detectionid;
    }

    /**
     * @return the segments
     */
    public ArrayList<CorrelationComponent> getSegments() {
        return new ArrayList<>(segments);
    }

    public ArrayList<PhasePick> getAssociatedPicks() {
        return new ArrayList<>(associatedPicks);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.detectionid;
        hash = 71 * hash + Objects.hashCode(this.segments);
        hash = 71 * hash + Objects.hashCode(this.associatedPicks);
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
        final DetectionWaveforms other = (DetectionWaveforms) obj;
        if (this.detectionid != other.detectionid) {
            return false;
        }
        if (!Objects.equals(this.segments, other.segments)) {
            return false;
        }
        if (!Objects.equals(this.associatedPicks, other.associatedPicks)) {
            return false;
        }
        return true;
    }
    
    

}
