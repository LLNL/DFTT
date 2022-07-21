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
package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.awt.Color;
import java.util.Objects;
import llnl.gnem.apps.detection.classify.TriggerClassification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class DetectorStats {

    private final int runid;
    private final int detectorid;
    private final DetectorType detectorType;
    private int detectionCount;
    private final int rank;
    private final String creationType;
    private  TriggerClassification triggerClassification;

    public DetectorStats(int runid, 
            int detectorid, 
            DetectorType detectorType, 
            String creationType, 
            int rank, 
            int detectionCount, 
            TriggerClassification triggerClassification) {
        this.runid = runid;
        this.detectorid = detectorid;
        this.detectorType = detectorType;
        this.creationType = creationType;
        this.rank = rank;
        this.detectionCount = detectionCount;
        this.triggerClassification = triggerClassification;
    }

    @Override
    public String toString() {
        Color color = triggerClassification.getTraceDisplayColor();
        String strColor = Integer.toHexString( color.getRGB() & 0x00ffffff );
        return String.format("<html><font color=%s>Rank %d %s(%s) ID = %d (%d)</font></html>", 
                strColor,rank, detectorType.toString(),creationType, detectorid, detectionCount);
    }
    
    public void decrementDetectionCount(){
        --detectionCount;
    }

    /**
     * @return the detectorType
     */
    public DetectorType getDetectorType() {
        return detectorType;
    }

    /**
     * @return the detectorid
     */
    public int getDetectorid() {
        return detectorid;
    }

    /**
     * @return the detectionCount
     */
    public int getDetectionCount() {
        return detectionCount;
    }

    /**
     * @return the runid
     */
    public int getRunid() {
        return runid;
    }

    public int getRank() {
        return rank;
    }

    public String getCreationType() {
        return creationType;
    }

    public TriggerClassification getTriggerClassification() {
        return triggerClassification;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.runid;
        hash = 73 * hash + this.detectorid;
        hash = 73 * hash + Objects.hashCode(this.detectorType);
        hash = 73 * hash + this.detectionCount;
        hash = 73 * hash + this.rank;
        hash = 73 * hash + Objects.hashCode(this.creationType);
        hash = 73 * hash + Objects.hashCode(this.triggerClassification);
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
        final DetectorStats other = (DetectorStats) obj;
        if (this.runid != other.runid) {
            return false;
        }
        if (this.detectorid != other.detectorid) {
            return false;
        }
        if (this.detectionCount != other.detectionCount) {
            return false;
        }
        if (this.rank != other.rank) {
            return false;
        }
        if (!Objects.equals(this.creationType, other.creationType)) {
            return false;
        }
        if (this.detectorType != other.detectorType) {
            return false;
        }
        if (this.triggerClassification != other.triggerClassification) {
            return false;
        }
        return true;
    }

    void updateClassification(TriggerClassification tc) {
        triggerClassification = tc;
    }
    
}
