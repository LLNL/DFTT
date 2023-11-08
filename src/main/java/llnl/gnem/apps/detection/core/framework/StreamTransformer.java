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

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;

import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;

import llnl.gnem.apps.detection.core.signalProcessing.RFFTdp;
import llnl.gnem.dftt.core.util.PairT;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;

public class StreamTransformer {

    private final int numChannels;
    private final int decimatedBlockSize;
    private final int maxTemplateLength;
    private final int nfft;
    private final RFFTdp fft;

    public StreamTransformer(PreprocessorParams params, int numChannels, int maxTemplateLength) {

        this.numChannels = numChannels;
        this.decimatedBlockSize = params.getDecimatedDataBlockSize();
        this.maxTemplateLength = maxTemplateLength;
        
        int log2nfft = getDFTSize();
        nfft = 1 << log2nfft;
        fft  = new RFFTdp( log2nfft );
    }

    private int getDFTSize() {
        // calculate FFT size
        int N = 1;
        int log2N = 0;
        int n1 = getDecimatedBlockSize();
        int n2 = getMaxTemplateLength();
        while (N < n1 + n2 - 1) {
            N *= 2;
            log2N++;
        }
        return log2N;
    }

    public TransformedStreamSegment transform(StreamSegment segment) {

        int nch = segment.getNumChannels();
        if (nch != getNumChannels()) {
            throw new IllegalStateException(String.format("Expected segment with %d channels but got %d instead!", getNumChannels(), nch));
        }


        Collection<PairT<WaveformSegment, double[]>> result = new ArrayList<>();
        for (WaveformSegment seg : segment.getWaveforms()) {
            float[] cdata = seg.getData();
            double[] fftd = new double[nfft];
            for (int i = 0; i < cdata.length; i++) {
                fftd[i] = cdata[i];
            }
            fft.dft(fftd);
            result.add(new PairT<>(seg, fftd));
        }
        return new TransformedStreamSegment(result);
    }

    /**
     * @return the numChannels
     */
    public int getNumChannels() {
        return numChannels;
    }

    /**
     * @return the decimatedBlockSize
     */
    public int getDecimatedBlockSize() {
        return decimatedBlockSize;
    }

    /**
     * @return the maxTemplateLength
     */
    public int getMaxTemplateLength() {
        return maxTemplateLength;
    }
    
    
    public int getFFTSize() {
        return nfft;
    }

}
