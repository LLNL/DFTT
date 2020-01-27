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
package llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation;

import com.oregondsp.signalProcessing.SimpleSTALTA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;

import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.AbstractEmpiricalDetector;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.core.util.PairT;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;

/**
 *
 * @author harris2
 */
public class ArrayCorrelationDetector extends AbstractEmpiricalDetector {

    private static final long serialVersionUID = -5601033793629935377L;

    private ArrayList< SingleChannelSubspace> subspaceOperators;
    private int                               nchannels;
    private double[]                          accumulator;
    private double[]                          detStatSegment;
    private final SimpleSTALTA                stalta;
    private final float                       staDuration;
    private final float                       ltaDuration;
    private final float                       gapDuration;

    
    @Override
    public String toString() {
        return String.format( "%s detector with ID: %d", getDetectorType().toString(), getdetectorid() );
    }

    

    public ArrayCorrelationDetector( int                           detectorid,
            ArrayCorrelationSpecification spec,
                                     PreprocessorParams            params,
                                     double                        sampleRate,
                                     String                        streamName,
                                     int                           decimatedBlockSize ) throws IOException {

        super( detectorid, sampleRate, streamName, decimatedBlockSize, new ArrayCorrelationTemplate(spec, params) );

        stalta = new SimpleSTALTA( spec.getStaDuration(),
                spec.getLtaDuration(),
                spec.getGapDuration(),
                sampleRate,
                                   1.0      );

        staDuration = spec.getStaDuration();
        ltaDuration = spec.getLtaDuration();
        gapDuration = spec.getGapDuration();
        initialize();
    }

    

    public ArrayCorrelationDetector( int                      detectorid,
            ArrayCorrelationTemplate template,
                                     PreprocessorParams       params,
                                     double                   sampleRate,
                                     String                   streamName,
                                     int                      decimatedBlockSize ) throws IOException {

        super( detectorid, sampleRate, streamName, decimatedBlockSize, template );

        this.template = new ArrayCorrelationTemplate(template);

        ArrayCorrelationSpecification spec = (ArrayCorrelationSpecification) template.getSpecification();

        stalta = new SimpleSTALTA( spec.getStaDuration(),
                spec.getLtaDuration(),
                spec.getGapDuration(),
                sampleRate,
                                   1.0  );
        staDuration = spec.getStaDuration();
        ltaDuration = spec.getLtaDuration();
        gapDuration = spec.getGapDuration();
        initialize();
    }

    

    private void initialize() {

        ArrayCorrelationTemplate ACT = (ArrayCorrelationTemplate) template;

        int ndim           = ACT.getdimension();
        nchannels          = ACT.getnchannels();
        int templateLength = ACT.getTemplateLength();

        detStatSegment = new double[decimatedSegmentLength];
        accumulator = new double[decimatedSegmentLength];

        ArrayList< float[][]> multidimensionalTemplate = ACT.getRepresentation();

        // calculate FFT size
        int N = 1;
        int log2N = 0;
        while (N < decimatedSegmentLength + templateLength - 1) {
            N *= 2;
            log2N++;
        }

        // instantiate subspace operators
        subspaceOperators = new ArrayList< >();
        for (int ich = 0; ich < nchannels; ich++) {
            subspaceOperators.add(new SingleChannelSubspace(multidimensionalTemplate.get(ich),
                    ndim,
                    templateLength,
                    decimatedSegmentLength,
                    log2N));
        }

        // detector delay
        // The following is an approximation to get the time correction about right. 
        detectorDelayInSeconds = templateLength * getSampleInterval();

        for (SingleChannelSubspace operator : subspaceOperators) {
            operator.init();
        }

    }

    

    public ArrayCorrelationTemplate getTemplate() {
        return (ArrayCorrelationTemplate) template;
    }


    
    @Override
    public DetectionStatistic produceStatistic(TransformedStreamSegment segment) {

        // accumulate subspace statistics into a single trace
        Arrays.fill(accumulator, 0.0);

        int ich = 0;
        for (SingleChannelSubspace operator : subspaceOperators) {
            PairT<WaveformSegment, double[]> data = segment.getSegmentAndDFT(ich);
            operator.calculateStatistic( data.getFirst().getData(), data.getSecond(), detStatSegment);
            for (int i = 0; i < decimatedSegmentLength; i++) {
                accumulator[i] += detStatSegment[i];
            }
            ich++;
        }

        int offset = detectionStatistic.length - decimatedSegmentLength;

        for (int i = 0; i < decimatedSegmentLength; i++) {
            detectionStatistic[ i + offset] = stalta.filter((float) accumulator[i]);
        }
        
        DetectorInfo detectorInfo = new DetectorInfo(getdetectorid(),getName(),getDetectorType(),
                getProcessingDelayInSeconds(), getSpecification(), this.getDetectorDelayInSeconds(),null, null, null);
        return new DetectionStatistic(detectionStatistic, 
                segment.getStartTime(), 
                segment.getSamplerate(),
                detectorInfo);
    }

    
    
    public double getStaDuration() {
        return staDuration;
    }

    
    
    /**
     * @return the ltaDuration
     */
    public float getLtaDuration() {
        return ltaDuration;
    }

    
    
    /**
     * @return the gapDuration
     */
    public float getGapDuration() {
        return gapDuration;
    }

}
