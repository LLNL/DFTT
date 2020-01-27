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

import java.io.PrintWriter;
import java.io.Serializable;

/**
 *
 * @author dodge1
 */
public class FKScreenParams implements Serializable {

    private final FKScreenRange fkScreenRange;
    private final double maxSlowness;
    private final double minFKFreq;
    private final double maxFKFreq;
    private final double fKWindowLength;
    private final double minFKQual;
    static final long serialVersionUID = -1658410938556202322L;
    private final double minVelocity;
    private final double maxVelocity;
    private final boolean computeFKParams, screenPowerTriggers, requireMinimumVelocity;
    private final boolean requireMaximumVelocity;

    public FKScreenParams(FKScreenRange fkScreenRange,
            double maxSlowness,
            double minFKFreq,
            double maxFKFreq,
            double fKWindowLength,
            double minFKQual,
            double minVelocity,
            double maxVelocity,
            boolean computeFKParams,
            boolean screenPowerTriggers,
            boolean requireMinimumVelocity,
            boolean requireMaximumVelocity) {
        this.fkScreenRange = fkScreenRange;
        this.maxSlowness = maxSlowness;
        this.minFKFreq = minFKFreq;
        this.maxFKFreq = maxFKFreq;
        this.fKWindowLength = fKWindowLength;
        this.minFKQual = minFKQual;
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity > 0 ? maxVelocity : Double.MAX_VALUE;
        this.computeFKParams = computeFKParams;
        this.screenPowerTriggers = screenPowerTriggers;
        this.requireMinimumVelocity = requireMinimumVelocity;
        this.requireMaximumVelocity = requireMaximumVelocity;
        if( requireMaximumVelocity && requireMinimumVelocity && minVelocity >= maxVelocity){
            throw new IllegalStateException(String.format("Minimum Velocity(%f) must be less than Maximum Velocity(%f)!",minVelocity,maxVelocity));
        }
    }

    /**
     * @return the slownessRangeSpecification
     */
    public FKScreenRange getFKScreenRange() {
        return fkScreenRange;
    }

    /**
     * @return the maxSlowness
     */
    public double getMaxSlowness() {
        return maxSlowness;
    }

    /**
     * @return the minFKFreq
     */
    public double getMinFKFreq() {
        return minFKFreq;
    }

    /**
     * @return the maxFKFreq
     */
    public double getMaxFKFreq() {
        return maxFKFreq;
    }

    /**
     * @return the fKWindowLength
     */
    public double getfKWindowLength() {
        return fKWindowLength;
    }

    public boolean isScreenPowerDetections() {
        return isScreenPowerTriggers();
    }

    public static void writeDefaultConfigInfo(PrintWriter writer, String sep) {

        writer.print(String.format("#------------------------ FK Trigger screening ----------------------%s", sep));
        writer.print(String.format("# The framework can calculate and store FK statistics in TRIGGER_FK_STATS for all triggers on an array stream.%s", sep));
        writer.print(String.format("# To turn this behavior on set the next parameter to true.%s", sep));
        writer.print(String.format("ComputeAndSaveFKParams = false%s", sep));

        writer.print(String.format("# If the next parameter is set to true, power triggers will be screened by FK results.%s", sep));
        writer.print(String.format("FKScreenPowerTriggers = false%s", sep));

        writer.print(String.format("# The +-slowness range (x and y) over which to compute the FK%s", sep));
        writer.print(String.format("FKSMax = 0.4%s", sep));

        writer.print(String.format("# The maximum azimuthal deviation to be a valid trigger%s", sep));
        writer.print(String.format("FKAzimuthTolerance = 15.0%s", sep));

        writer.print(String.format("# The maximum velocity deviation to be a valid trigger%s", sep));
        writer.print(String.format("FKVelocityTolerance = 3.0%s", sep));

        writer.print(String.format("# The minimum frequency for the wideband FK%s", sep));
        writer.print(String.format("MinFKFrequency = 0.1%s", sep));

        writer.print(String.format("# The maximum frequency for the wideband FK%s", sep));
        writer.print(String.format("MaxFKFrequency = 8.0%s", sep));

        writer.print(String.format("# The minimum FK quality statistic to pass the FK Screen%s", sep));
        writer.print(String.format("MinFKQuality = 0.6%s", sep));

        writer.print(String.format("# The length of the window to use in calculating the FK%s", sep));
        writer.print(String.format("FKWindowLength = 10.0%s", sep));

        writer.print(String.format("# If the next parameter is set to true, power triggers will be required to have a minimum velocity.%s", sep));
        writer.print(String.format("# This is intended as a means to screen out detections on S-waves.%s", sep));
        writer.print(String.format("RequireMinimumVelocity = false%s", sep));
        writer.print(String.format("MinimumVelocity = 5.0%s", sep));


        writer.print(String.format("# If the next parameter is set to true, power triggers will be required to be less than a maximum velocity.%s", sep));
        writer.print(String.format("RequireMaximumVelocity = false%s", sep));
        writer.print(String.format("MaximumVelocity = 25.0%s", sep));
        
        
        writer.print(String.format("# ------------------------- End FK Trigger screening ----------------------%s", sep));

    }

    public boolean isRequireMinimumVelocity() {
        return requireMinimumVelocity;
    }

    /**
     * @return the minFKQual
     */
    public double getMinFKQual() {
        return minFKQual;
    }

    public boolean isComputeFKOnTriggers() {
        return isComputeFKParams() || isScreenPowerTriggers() || isRequireMinimumVelocity();
    }

    public boolean isScreenTrigger(DetectorType type) {

        return type == DetectorType.ARRAYPOWER && isScreenPowerTriggers();
    }

    public double getMinVelocity() {
        return minVelocity;
    }

    /**
     * @return the computeFKParams
     */
    public boolean isComputeFKParams() {
        return computeFKParams;
    }

    /**
     * @return the screenPowerTriggers
     */
    public boolean isScreenPowerTriggers() {
        return screenPowerTriggers;
    }

    /**
     * @return the maxVelocity
     */
    public double getMaxVelocity() {
        return maxVelocity;
    }

    /**
     * @return the requireMaximumVelocity
     */
    public boolean isRequireMaximumVelocity() {
        return requireMaximumVelocity;
    }

 
}
