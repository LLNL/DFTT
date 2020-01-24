/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors.array;

import com.oregondsp.signalProcessing.SimpleSTALTA;
import llnl.gnem.apps.detection.core.dataObjects.ArrayConfiguration;
import llnl.gnem.apps.detection.core.dataObjects.ArrayElement;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.SlownessRangeSpecification;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import llnl.gnem.apps.detection.core.signalProcessing.Delay;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.AbstractSimpleDetector;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author harris2
 */
public class ArrayPowerDetector extends AbstractSimpleDetector {

    private static final long serialVersionUID = 4359966268914836082L;

    // instance variables
    
    private final SimpleSTALTA       STALTA;
    private final Delay[]            delays;
    private final float[]            beam;
    private final int                nch;
    private final float[]            buffer;
    private final ArrayConfiguration configuration;
    private final DetectorInfo       detectorInfo;

    
    
    // ArrayDetector constructor
    
    public ArrayPowerDetector( int                        detectorid,
            ArrayDetectorSpecification specification,
                               double                     sampleRate,
                               String                     streamName,
                               int                        decimatedBlockSize ) throws IOException {

        super( detectorid, sampleRate, streamName, decimatedBlockSize, specification );

        configuration = specification.getArrayConfiguration();
        Collection< ? extends StreamKey> channels = specification.getStaChanList();
        ArrayDetectorSpecification spec = (ArrayDetectorSpecification) getSpecification();
        SlownessSpecification slowness = spec.getSlownessSpecification();
        Map<StreamKey, ArrayElement> ourElements = configuration.getElements(channels);

        double[] delaysInSeconds = configuration.delaysInSeconds(channels, specification.getSlownessSpecification());
        nch = channels.size();

        for (int i = 0; i < nch; i++) {
            delaysInSeconds[i] *= -1.0;
        }
        double minDelay = 100000000.0;
        for (int ich = 0; ich < nch; ich++) {
            minDelay = Math.min(minDelay, delaysInSeconds[ich]);
        }

        for (int ich = 0; ich < nch; ich++) {
            delaysInSeconds[ich] -= minDelay;
        }

        delays = new Delay[nch];
        double delta = 1 / sampleRate;
        float avgDelayInSamples = 0.0f;
        for (int ich = 0; ich < nch; ich++) {
            float delayInSamples = (float) (delaysInSeconds[ich] / delta) + 3.0f;
            avgDelayInSamples += delayInSamples;
            delays[ich] = new Delay(delayInSamples);
        }
        avgDelayInSamples /= nch;

        STALTA = new SimpleSTALTA(specification.getSTADuration(),
                specification.getLTADuration(),
                specification.getGapDuration(),
                delta,
                1.0);

        detectorDelayInSeconds = avgDelayInSamples * delta + specification.getSTADuration() / 2.0;
        detectorInfo = new DetectorInfo(getdetectorid(), getName(), getDetectorType(),
                getProcessingDelayInSeconds(), getSpecification(), this.getDetectorDelayInSeconds(), configuration, slowness, ourElements);
        beam = new float[decimatedSegmentLength];

        buffer = new float[decimatedSegmentLength];
    }

    
    
    @Override
    public DetectionStatistic produceStatistic(TransformedStreamSegment segment) {

        if (nch != segment.getNumChannels()) {
            throw new IllegalStateException("Number of channels expected by beamformer not equal to number of data channels");
        }

        int offset = 0;//detectionStatistic.length - decimatedSegmentLength;

        Arrays.fill(beam, 0.0f);
        for (int ich = 0; ich < nch; ich++) {
            delays[ich].delay(segment.getChannelData(ich), buffer);
            for (int i = 0; i < decimatedSegmentLength; i++) {
                beam[i] += buffer[i];
            }
        }
        for (int i = 0; i < decimatedSegmentLength; i++) {
            beam[i] /= nch;
        }
        for (int i = 0; i < decimatedSegmentLength; i++) {
            detectionStatistic[i + offset] = STALTA.filter(beam[i] * beam[i]);
        }

        return new DetectionStatistic(detectionStatistic,
                segment.getStartTime(),
                segment.getSamplerate(),
                detectorInfo);
    }

    
    
    public FKScreenConfiguration createFKScreen(FKScreenParams screenParams) {
        ArrayDetectorSpecification spec = (ArrayDetectorSpecification) getSpecification();
        Collection< ? extends StreamKey> channels = spec.getStaChanList();
        SlownessSpecification slowness = spec.getSlownessSpecification();
        SlownessRangeSpecification srs = new SlownessRangeSpecification(slowness, screenParams.getFKScreenRange());
        Map<StreamKey, ArrayElement> ourElements = configuration.getElements(channels);
        return new FKScreenConfiguration(screenParams, srs, ourElements);

    }

    
    
    public ArrayConfiguration getConfiguration() {
        return configuration;
    }

}
