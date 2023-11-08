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
package llnl.gnem.apps.detection.core.framework.detectors.array;

import com.oregondsp.signalProcessing.SimpleSTALTA;


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
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.dftt.core.util.StreamKey;

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
        Collection<StreamKey> channels = specification.getStreamKeys();
        ArrayDetectorSpecification spec = (ArrayDetectorSpecification) getSpecification();
        SlownessSpecification slowness = spec.getSlownessSpecification();
        int jdate = ProcessingPrescription.getInstance().getMinJdateToProcess();
        Map<StreamKey, ArrayElementInfo> ourElements = configuration.getElements(channels,jdate);

        double[] delaysInSeconds = new double[channels.size()];
        int j = 0;
        for(StreamKey key : channels){
            ArrayElementInfo aei = ourElements.get(key);
           double delay =  aei.delayInSeconds(specification.getSlownessSpecification().getSlownessVector());
           delaysInSeconds[j++] = delay;
        }
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
        // This would be a good place to write out a debug SAC file
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
        Collection<StreamKey> channels = spec.getStreamKeys();
        SlownessSpecification slowness = spec.getSlownessSpecification();
        SlownessRangeSpecification srs = new SlownessRangeSpecification(slowness, screenParams.getFKScreenRange());
        int jdate = ProcessingPrescription.getInstance().getMinJdateToProcess();
        Map<StreamKey, ArrayElementInfo> ourElements = configuration.getElements(channels,jdate);
        return new FKScreenConfiguration(screenParams, srs, ourElements);
    }

    
    
    public ArrayConfiguration getConfiguration() {
        return configuration;
    }

}
