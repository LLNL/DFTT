/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.core.dataObjects.ArrayConfiguration;
import llnl.gnem.apps.detection.core.dataObjects.ArrayElement;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.SlownessRangeSpecification;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import llnl.gnem.apps.detection.core.framework.detectors.array.FKScreenConfiguration;
import llnl.gnem.core.util.StreamKey;

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
    private final HashMap<StreamKey, ArrayElement> ourElements;
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
            Map<StreamKey, ArrayElement> ourElements) {
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

    public Collection<? extends StreamKey> getStaChanList() {
        return specification.getStaChanList();
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

    /**
     * @return the arrayConfiguration
     */
    public ArrayConfiguration getArrayConfiguration() {
        return arrayConfiguration;
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
