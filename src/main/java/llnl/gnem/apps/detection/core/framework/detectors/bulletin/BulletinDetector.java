package llnl.gnem.apps.detection.core.framework.detectors.bulletin;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.AbstractSimpleDetector;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.core.util.Epoch;

public class BulletinDetector extends AbstractSimpleDetector {

    private final Bulletin bulletin;

    public BulletinDetector( int                   detectorid,
            BulletinSpecification specification,
                             double                sampleRate,
                             String                streamName,
                             int                   decimatedBlockSize ) throws IOException {

        super( detectorid, sampleRate, streamName, decimatedBlockSize, specification );
        bulletin = specification.getBulletin();
        detectorDelayInSeconds = 0;
    }

    
    
    @Override
    public DetectionStatistic produceStatistic( TransformedStreamSegment segment ) {

        Epoch epoch = segment.getEpoch();
        Collection<BulletinRecord> bulletins = bulletin.getBulletinRecords(epoch);
        ArrayList<Integer> triggerIndexes = new ArrayList<>();
        double lastTriggerTime = -Double.MAX_VALUE;
        for (BulletinRecord record : bulletins) {
            double ptime = record.getExpectedPTime();
            double timeSinceLast = ptime - lastTriggerTime;
            if ( timeSinceLast > this.getSpecification().getBlackoutPeriod() ) {
                int idx = (int) Math.round((ptime - segment.getStartTime().getEpochTime()) * segment.getSamplerate());
                triggerIndexes.add(idx);
            }
            lastTriggerTime = ptime;

        }
        int offset = detectionStatistic.length - decimatedSegmentLength;
        for (int i = 0; i < decimatedSegmentLength; i++) {
            detectionStatistic[i + offset] = 0.0f;
        }
        triggerIndexes.stream().forEach((idx) -> {
            detectionStatistic[idx + offset] = 1.0f;
        });

        DetectorInfo detectorInfo = new DetectorInfo(getdetectorid(), getName(), getDetectorType(),
                getProcessingDelayInSeconds(), getSpecification(), this.getDetectorDelayInSeconds(), null, null, null);
        return new DetectionStatistic(detectionStatistic,
                segment.getStartTime(),
                segment.getSamplerate(),
                detectorInfo);
    }

}
