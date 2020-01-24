/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;


import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class DetectorSubstitution {

    private final Detector detector;
    private final double shift;
    private final double statisticValue;
    private final int srcDetectorid;
    private final SubstitutionReason substitutionReason;

    public DetectorSubstitution(Detector detector, 
            double shift, 
            double statisticValue, 
            int srcDetectorid, 
            SubstitutionReason substitutionReason) {
        this.detector = detector;
        this.shift = shift;
        this.statisticValue = statisticValue;
        this.srcDetectorid = srcDetectorid;
        this.substitutionReason = substitutionReason;
    }

    /**
     * @return the detector
     */
    public Detector getDetector() {
        return detector;
    }

    /**
     * @return the shift
     */
    public double getShift() {
        return shift;
    }

    /**
     * @return the statisticValue
     */
    public double getStatisticValue() {
        return statisticValue;
    }

    /**
     * @return the srcDetectorid
     */
    public int getSrcDetectorid() {
        return srcDetectorid;
    }

    /**
     * @return the substitutionReason
     */
    public SubstitutionReason getSubstitutionReason() {
        return substitutionReason;
    }

}
