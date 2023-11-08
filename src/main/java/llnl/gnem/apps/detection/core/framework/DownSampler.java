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
package llnl.gnem.apps.detection.core.framework;

import com.oregondsp.signalProcessing.filter.iir.Butterworth;
import com.oregondsp.signalProcessing.filter.iir.IIRFilter;
import com.oregondsp.signalProcessing.filter.iir.PassbandType;
import java.util.ArrayList;
import java.util.List;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;

import llnl.gnem.dftt.core.signalprocessing.Sequence;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.dftt.core.waveform.qc.DataDefect;

public class DownSampler {

    private final int numChannels;
//    private final int blockSize;

    private final IIRFilter[] filters;
    private final IIRFilter filter;
    private final int order;

    private final int decimationRate;
    private final int decimatedBlockSize;

    public DownSampler(PreprocessorParams params,
            int numChannels,
            double samplingRate) {

        this.numChannels = numChannels;
        this.decimatedBlockSize = params.getDecimatedDataBlockSize();
        this.decimationRate = params.getDecimationRate();
        this.order = params.getPreprocessorFilterOrder();

        //       blockSize = decimatedBlockSize * decimationRate;
        // anti-alias filters
        filters = new IIRFilter[numChannels];
        for (int ich = 0; ich < numChannels; ich++) {
            filters[ich] = new Butterworth(order,
                    PassbandType.BANDPASS,
                    params.getPassBandLowFrequency(),
                    params.getPassBandHighFrequency(),
                    1.0 / samplingRate);
        }

        // Single anti-alias filter for creating templates
        filter = new Butterworth(order,
                PassbandType.BANDPASS,
                params.getPassBandLowFrequency(),
                params.getPassBandHighFrequency(),
                1.0 / samplingRate);

    }

    public StreamSegment process(StreamSegment segment) {

        int nch = segment.getNumChannels();
        if (nch != numChannels) {
            throw new IllegalStateException(String.format("Expected segment with %d channels but got %d instead!", numChannels, nch));
        }

        ArrayList<WaveformSegment> result = new ArrayList<>();
        for (int ich = 0; ich < segment.getNumChannels(); ++ich) {
            WaveformSegment ws = segment.getWaveformSegment(ich);
            List<DataDefect> defects = new ArrayList<>(ws.getDefects());
            float[] tmp = ws.getData();
            filters[ich].filter(tmp);
            float[] decimated = new float[decimatedBlockSize];
            Sequence.decimate(tmp, decimated, decimationRate);
            WaveformSegment wsd = new WaveformSegment(ws.getStreamKey(),
                    ws.getTimeAsDouble(),
                    ws.getSamprate() / decimationRate,
                    decimated,
                    defects);
            result.add(wsd);
        }

        return new StreamSegment(result);
    }

    /**
     * method to preprocess master event segments one channel at a time
     *
     * @param x float[] containing one-channel segment
     * @return
     */
    public float[] downSample(float[] x) {

        int n = x.length;
        float[] tmp = new float[n];

        filter.initialize();
        filter.filter(x, tmp);

        int m = n / decimationRate;
        if (n - m * decimationRate > 0) {
            m++;
        }
        float[] retval = new float[m];
        Sequence.decimate(tmp, retval, decimationRate);

        return retval;
    }

}
