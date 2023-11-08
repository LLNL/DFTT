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
package llnl.gnem.apps.detection.triggerProcessing;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.classify.LabeledFeature;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.FKScreenResults;
import llnl.gnem.dftt.core.waveform.qc.DataDefect;

/**
 *
 * @author dodge
 */
public class EvaluatedTrigger {
    private final TriggerData triggerData;
    private final boolean usable;
    private final FKStatus fkStatus;
    private final LabeledFeature features;
    private final FKScreenResults fkResults;
    private final boolean snrIsOK;
    private final boolean durationOK;
    private final boolean forceFixedDuration;
    private final double fixedDurationSeconds;
    private final boolean velocityOK;
    private final double relativeAmplitude;
    private final double signalDuration;

    public EvaluatedTrigger(TriggerData triggerData,
            boolean usable,
            FKStatus fkStatus,
            LabeledFeature features,
            double signalDuration,
            FKScreenResults fkResults,
            boolean snrIsOK,
            boolean durationOK,
            boolean velocityOK,
            boolean forceFixedDuration,
            double fixedDurationSeconds,
            double relativeAmplitude) {
        this.triggerData = triggerData;
        this.usable = usable;
        this.fkStatus = fkStatus;
        this.features = features;
        this.signalDuration = signalDuration;
        this.fkResults = fkResults;
        this.snrIsOK = snrIsOK;
        this.durationOK = durationOK;
        this.velocityOK = velocityOK;
        this.forceFixedDuration = forceFixedDuration;
        this.fixedDurationSeconds = fixedDurationSeconds;
        this.relativeAmplitude = relativeAmplitude;
    }

    public double getRelativeAmplitude() {
        return relativeAmplitude;
    }

    public FKScreenResults getFKScreenResults() {
        return fkResults;
    }

    public ScreenResult getSnrScreenResult() {
        return getNonFKResult(snrIsOK);
    }

    public ScreenResult getDurationScreenResult() {
        return getNonFKResult(durationOK);
    }

    public ScreenResult getVelocityScreenResult() {
        return getNonFKResult(velocityOK);
    }

    public ScreenResult getFKScreenResult() {
        switch (fkStatus) {
            case NOT_PERFORMED:
                return ScreenResult.NotPerformed;
            case IGNORED:
                return ScreenResult.Ignored;
            case FAILED:
                return ScreenResult.Failed;
            case PASSED:
                return ScreenResult.Passed;
            default:
                throw new IllegalStateException("Unsupported FK Status type: " + fkStatus);
        }
    }

    private ScreenResult getNonFKResult(boolean value) throws IllegalStateException {
        DetectorType type = triggerData.getDetectorInfo().getDetectorType();

        switch (type) {
            case SUBSPACE:
                return ScreenResult.Ignored;
            case ARRAY_CORRELATION:
                return ScreenResult.Ignored;
            case ARRAYPOWER:
            // fall through
            case BULLETIN:
            // fall through
            case STALTA:
                return value ? ScreenResult.Passed : ScreenResult.Failed;
            default:
                throw new IllegalStateException("Unsupported detector type: " + type);
        }
    }

    /**
     * @return the triggerData
     */
    public TriggerData getTriggerData() {
        return triggerData;
    }

    /**
     * @return the usable
     */
    public boolean isUsable() {
        return usable;
    }

    /**
     * @return the fkStatus
     */
    public FKStatus getFkStatus() {
        return fkStatus;
    }

    /**
     * @return the features
     */
    public LabeledFeature getFeatures() {
        return features;
    }

    /**
     * @return the forceFixedDuration
     */
    public boolean isForceFixedDuration() {
        return forceFixedDuration;
    }

    /**
     * @return the fixedDurationSeconds
     */
    public double getFixedDurationSeconds() {
        return fixedDurationSeconds;
    }

    public double getSignalDuration() {
        return signalDuration;
    }

}
