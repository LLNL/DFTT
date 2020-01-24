/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
