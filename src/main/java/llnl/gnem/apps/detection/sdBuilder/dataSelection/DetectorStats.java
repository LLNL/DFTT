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

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
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

    public DetectorStats(int runid, int detectorid, DetectorType detectorType, String creationType, int rank, int detectionCount) {
        this.runid = runid;
        this.detectorid = detectorid;
        this.detectorType = detectorType;
        this.creationType = creationType;
        this.rank = rank;
        this.detectionCount = detectionCount;
    }

    @Override
    public String toString() {
        return String.format("Rank %d %s(%s) ID = %d (%d)", rank, detectorType.toString(),creationType, detectorid, detectionCount);
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
}
