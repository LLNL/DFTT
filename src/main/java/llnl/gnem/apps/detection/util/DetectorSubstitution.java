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
package llnl.gnem.apps.detection.util;


import llnl.gnem.apps.detection.dataAccess.dataobjects.SubstitutionReason;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
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
