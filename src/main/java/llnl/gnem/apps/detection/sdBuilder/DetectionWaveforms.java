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
