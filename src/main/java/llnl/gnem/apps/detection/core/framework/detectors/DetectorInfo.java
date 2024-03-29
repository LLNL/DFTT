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
package llnl.gnem.apps.detection.core.framework.detectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.SlownessRangeSpecification;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import llnl.gnem.apps.detection.core.framework.detectors.array.FKScreenConfiguration;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge
 */
public class DetectorInfo {

    private final int detectorid;
    private final String detectorName;
    private final double processingDelay;
    private final DetectorSpecification specification;
    private final ArrayConfiguration arrayConfiguration;
    private final SlownessSpecification slowness;
    private final HashMap<StreamKey, ArrayElementInfo> ourElements;
    private final double detectorDelayInSeconds;
    private final DetectorType detectorType;

    public DetectorInfo(int detectorid,
            String detectorName,
            DetectorType detectorType,
            double processingDelay,
            DetectorSpecification specification,
            double detectorDelayInSeconds,
            ArrayConfiguration arrayConfiguration,
            SlownessSpecification slowness,
            Map<StreamKey, ArrayElementInfo> ourElements) {
        this.detectorid = detectorid;
        this.detectorType = detectorType;
        this.detectorName = detectorName;
        this.processingDelay = processingDelay;
        this.specification = specification;
        this.detectorDelayInSeconds = detectorDelayInSeconds;
        this.arrayConfiguration = arrayConfiguration;
        this.slowness = slowness;
        this.ourElements = ourElements != null ? new HashMap<>(ourElements) : new HashMap<>();
    }

    @Override
    public String toString() {
        return "DetectorInfo{" + "detectorid=" + detectorid + ", detectorName=" + detectorName + ", processingDelay=" + processingDelay + ", detectorDelayInSeconds=" + detectorDelayInSeconds + ", detectorType=" + detectorType + '}';
    }

    public Collection<StreamKey> getStaChanList() {
        return specification.getStreamKeys();
    }

    /**
     * @return the detectorid
     */
    public int getDetectorid() {
        return detectorid;
    }

    /**
     * @return the detectorName
     */
    public String getDetectorName() {
        return detectorName;
    }

    /**
     * @return the processingDelay
     */
    public double getProcessingDelay() {
        return processingDelay;
    }

    /**
     * @return the triggerPositionType
     */
    public TriggerPositionType getTriggerPositionType() {
        return specification.getTriggerPositionType();
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return specification.getThreshold();
    }

    /**
     * @return the blackoutInterval
     */
    public float getBlackoutInterval() {
        return specification.getBlackoutPeriod();
    }



    public FKScreenConfiguration createFKScreen(FKScreenParams screenParams) {
        if ( slowness == null || ourElements == null) {
            return null;
        }
        SlownessRangeSpecification srs = new SlownessRangeSpecification(slowness, screenParams.getFKScreenRange());
        return new FKScreenConfiguration(screenParams, srs, ourElements);
    }

    /**
     * @return the detectorDelayInSeconds
     */
    public double getDetectorDelayInSeconds() {
        return detectorDelayInSeconds;
    }

    /**
     * @return the detectorType
     */
    public DetectorType getDetectorType() {
        return detectorType;
    }
    /**
     * @return the specification
     */
    public DetectorSpecification getSpecification() {
        return specification;
    }

}
