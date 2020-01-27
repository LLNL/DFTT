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

import java.io.Serializable;
import llnl.gnem.apps.detection.core.signalProcessing.RFFTdp;
import llnl.gnem.apps.detection.core.signalProcessing.Sequence;
import java.util.ArrayList;

/**
 * Copyright (c) 2009 Lawrence Livermore National Laboratory    <br/>
 * All rights reserved                                           <br/>
 * Author: Dave Harris                                          <br/>
 * Created: Jan 13, 2008                                         <br/>
 * Time: 10:21 AM                                                <br/>
 * Last Modified: November 24, 2012                              <br/>
 * <p>
 * This class implements a multichannel correlator with a collection of
 * single-channel correlators.
 * </p>
 */
public class MultichannelCorrelator implements Serializable {

    private static final long serialVersionUID = -635313981422063791L;

    private final int nchannels;
    private final int segmentLength;
    private final int templateLength;

    private final double[] shiftRegister;
    private final RFFTdp fft;
    private final int fftsize;

    private final double[][] templateTransforms;
    private final double[] product;
    private final double[] sum;

    /**
     *
     * @param template multichannel template - first dimension, channel; second
     * dimension, time
     * @param nchannels the number of channels in the template
     * @param templateLength The template length in samples.
     * @param segmentLength the length of the data block used in processing
     * @param log2FFTSize Power of two for the fft length.
     */
    public MultichannelCorrelator(float[][] template,
            int nchannels,
            int templateLength,
            int segmentLength,
            int log2FFTSize) {

        this.nchannels = nchannels;
        this.segmentLength = segmentLength;
        this.templateLength = templateLength;

        fft = new RFFTdp(log2FFTSize);
        fftsize = fft.fftsize();
        shiftRegister = new double[fftsize];
        product = new double[fftsize];
        sum = new double[fftsize];

        templateTransforms = new double[nchannels][fftsize];

        /*
         *  Time-reverse template and compute DFTs
         */
        for (int ich = 0; ich < nchannels; ich++) {

            double[] proxyXfm = templateTransforms[ich];
            float[] proxyT = template[ich];
            for (int i = 0; i < templateLength; i++) {
                proxyXfm[i] = proxyT[templateLength - 1 - i];
    }

            fft.dft(proxyXfm);
        }

    }

    public void init() {
        Sequence.zero(shiftRegister);
    }

    /**
     *
     * @param dataDFT collection of single-channel waveforms.
     * @param result double[] multichannel correlation result.
     */
    public void correlate(ArrayList< double[]> dataDFT,
            double[] result) {

        Sequence.zero(result);
        Sequence.zero(sum);

        for (int ich = 0; ich < nchannels; ich++) {
            fft.dftproduct(dataDFT.get(ich), templateTransforms[ich], product, 1.0);
            for (int i = 0; i < fftsize; i++) {
                sum[i] += product[i];
        }
        }

        // inverse fft
        fft.idft(sum);

        // overlap add
        Sequence.zshift(shiftRegister, -segmentLength);

        for (int i = 0; i < segmentLength + templateLength - 1; i++) {
            shiftRegister[i] += sum[i];
  }
        System.arraycopy(shiftRegister, 0, result, 0, segmentLength);

}

    public void flush(double[] result, int offset) {
        Sequence.zshift(shiftRegister, -segmentLength);
        System.arraycopy(shiftRegister, 0, result, offset, templateLength);
    }

}