// Software developed for AFRL BAA10-20 by Deschutes Signal Processing LLC
// Author:  David B. Harris
// Modified:  December 15, 2015
//
package llnl.gnem.apps.detection.core.framework.detectors;

import java.io.Serializable;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.core.util.FileSystemException;

public abstract class AbstractDetector implements Detector, Serializable {

    protected float[] detectionStatistic;
    protected boolean[] triggerMask;
    protected int decimatedSegmentLength;
    protected TriggerPositionType triggerPositionType;
    protected double detectorDelayInSeconds;

    private final DetectorSpecification specification;
    private final double sampleRate;
    private final String streamName;
    private final int detectorid;
    private static final long serialVersionUID = -6634808146892880195L;
    private String name = null;

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        if (name == null) {
            name = String.format("%s_detector_%05d", getDetectorType(), getdetectorid());
        }
        return name;
    }

    public AbstractDetector(int detectorid,
            DetectorSpecification specification,
            double sampleRate,
            String streamName,
            int decimatedBlockSize) throws FileSystemException {

        this.sampleRate = sampleRate;
        this.streamName = streamName;
        this.specification = specification;
        detectorDelayInSeconds = 0.0;
        this.detectorid = detectorid;
        decimatedSegmentLength = decimatedBlockSize;
        triggerPositionType = specification.getTriggerPositionType();
        detectionStatistic = new float[decimatedSegmentLength];
    }

    @Override
    public DetectionStatistic calculateDetectionStatistic(TransformedStreamSegment segment) {
        DetectionStatistic retval = produceStatistic(segment);
        return retval;
    }

    public abstract DetectionStatistic produceStatistic(TransformedStreamSegment segment);

    @Override
    public double getProcessingDelayInSeconds() {
        return getDetectorDelayInSeconds();
    }

    @Override
    public int getdetectorid() {
        return detectorid;
    }

    @Override
    public DetectorType getDetectorType() {
        return getSpecification().getDetectorType();
    }

    public String getStreamName() {
        return streamName;
    }

    /**
     * @return the specification
     */
    @Override
    public DetectorSpecification getSpecification() {
        return specification;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public double getSampleInterval() {
        return 1 / sampleRate;
    }

    /**
     * @return the detectorDelayInSeconds
     */
    @Override
    public double getDetectorDelayInSeconds() {
        return detectorDelayInSeconds;
    }

}
