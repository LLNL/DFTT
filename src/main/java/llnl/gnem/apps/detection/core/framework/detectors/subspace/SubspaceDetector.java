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
package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.AbstractEmpiricalDetector;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.apps.detection.core.signalProcessing.OverlapAdd_dp;
import llnl.gnem.dftt.core.signalprocessing.Sequence;

/**
 * Revised by D. Harris from earlier versions on November 24 - Dec 3, 2012
 *
 */
public class SubspaceDetector extends AbstractEmpiricalDetector {

    private static final long serialVersionUID = 3232634473497941826L;

    private ArrayList<MultichannelCorrelator> correlators;
    private OverlapAdd_dp envelopeSmoother;
    private int ndim;
    private int nfft;
    private long[] statBin;
    private final int nBins = 100;
    private long numHistogramSamples = 0;
    private float maxStatistic;
    private int maxIndex;

    public float getMaxStatistic() {
        return maxStatistic;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    @Override
    public String toString() {
        return String.format("Rank: %d subspace detector with ID: %d", ((SubspaceTemplate) template).getSingularValues().length, this.getdetectorid());
    }

    public SubspaceDetector(int detectorid,
            SubspaceSpecification spec,
            PreprocessorParams params,
            double sampleRate,
            String streamName,
            int streamFFTSize,
            int decimatedBlockSize) throws IOException {

        super(detectorid, sampleRate, streamName, decimatedBlockSize, new SubspaceTemplate(spec, params));

        initialize(streamFFTSize);

    }

    public double getWindowDurationSeconds() {
        return this.getTemplate().getWindowDurationSeconds();
    }

    public SubspaceDetector(int detectorid,
            SubspaceTemplate template,
            double sampleRate,
            String streamName,
            int streamFFTSize,
            int decimatedBlockSize) throws IOException {

        super(detectorid, sampleRate, streamName, decimatedBlockSize, new SubspaceTemplate(template));

        initialize(streamFFTSize);
    }

    private void initialize(int streamFFTSize) {
        statBin = new long[nBins];
        Arrays.fill(statBin, 0);

        SubspaceTemplate SST = (SubspaceTemplate) template;

        ndim = SST.getdimension();
        int templateLength = SST.getTemplateLength();

        ArrayList< float[][]> multidimensionalTemplate = SST.getRepresentation();

        // calculate FFT size
        int n = Math.max(streamFFTSize, decimatedSegmentLength + templateLength - 1);
        nfft = 1;
        int log2n = 0;
        while (nfft < n) {
            nfft *= 2;
            log2n++;
        }
        if (nfft > streamFFTSize) {
            throw new IllegalStateException("Subspace fft size and stream fft size mismatch");
        }

        // instantiate MultichannelCorrelators
        int nchannels = getNumChannels();

        correlators = new ArrayList<>();
        for (int idim = 0; idim < ndim; idim++) {
            correlators.add(
                    new MultichannelCorrelator(multidimensionalTemplate.get(idim),
                            nchannels,
                            templateLength,
                            decimatedSegmentLength,
                            log2n)
            );
        }

        // detector delay
        // The following is an approximation to get the time correction about right.
        detectorDelayInSeconds = templateLength * getSampleInterval();

        for (MultichannelCorrelator MC : correlators) {
            MC.init();
        }

        double[] kernel = new double[templateLength];
        Arrays.fill(kernel, 1.0f);
        envelopeSmoother = new OverlapAdd_dp(kernel, decimatedSegmentLength);
        envelopeSmoother.initialize(0.0);
    }

    @Override
    public SubspaceTemplate getTemplate() {
        return (SubspaceTemplate) template;
    }

    @Override
    public DetectionStatistic produceStatistic(TransformedStreamSegment segment) {

        // calculate power over all channels
        double[] sumsqr = new double[decimatedSegmentLength];
        Sequence.zero(sumsqr);
        int nchannels = getNumChannels();

        for (int ich = 0; ich < nchannels; ich++) {
            float[] dataref = segment.getWaveformSegment(getStaChanKey(ich)).getData();
            for (int j = 0; j < segment.size(); j++) {
                sumsqr[j] += dataref[j] * dataref[j];
            }
        }

        double[] multichannelCorrelation = new double[decimatedSegmentLength];
        double[] projection = new double[decimatedSegmentLength];
        double[] envelope = new double[decimatedSegmentLength];

        // projection calculation
        Sequence.zero(projection);
        ArrayList< double[]> dataDFT = segment.getDataDFTList(template.getStaChanList());
        if (dataDFT.get(0).length != nfft) {
            throw new IllegalStateException("Mismatch in dft sizes");
        }
        for (int idim = 0; idim < ndim; idim++) {
            correlators.get(idim).correlate(dataDFT, multichannelCorrelation);
            for (int i = 0; i < decimatedSegmentLength; i++) {
                projection[i] += multichannelCorrelation[i] * multichannelCorrelation[i];
            }
        }

        // envelope calculation
        envelopeSmoother.filter(sumsqr, envelope, 0);

        int offset = detectionStatistic.length - decimatedSegmentLength;
        maxStatistic = -1;
        for (int i = 0; i < decimatedSegmentLength; i++) {
            float stat = envelope[i] <= 0.0f ? 0 : (float) (projection[i] / envelope[i]);
            if (stat > maxStatistic) {
                maxStatistic = stat;
                maxIndex = i;
            }
            detectionStatistic[i + offset] = stat;
        }
        updateHistogram(Math.sqrt(maxStatistic));

        DetectorInfo detectorInfo = new DetectorInfo(getdetectorid(),
                getName(),
                getDetectorType(),
                getProcessingDelayInSeconds(),
                getSpecification(),
                this.getDetectorDelayInSeconds(),
                null,
                null,
                null);

        return new DetectionStatistic(detectionStatistic,
                segment.getStartTime(),
                segment.getSamplerate(),
                detectorInfo);
    }

    private void updateHistogram(double stat) {
        int idx = (int) Math.round(stat * nBins);
        if (idx < 0) {
            idx = 0;
        }
        if (idx > nBins - 1) {
            idx = nBins - 1;
        }
        statBin[idx] += 1;
        ++numHistogramSamples;
    }

    public long[] getHistogramValues() {
        return statBin.clone();
    }

    public long getNumHistogramSamples() {
        return numHistogramSamples;
    }

}
