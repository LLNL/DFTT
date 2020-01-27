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
package llnl.gnem.core.waveform.classification;

import java.util.ArrayList;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.waveform.classification.peaks.BasePeak;
import llnl.gnem.core.waveform.classification.peaks.PeakFinder;

/**
 *
 * @author dodge1
 */
public class ModeEstimator {

    private final int NUM_BINS = 10;

    public int getModeCount(float[] data) {
        if (data.length < NUM_BINS) {
            return 0;
        }
        float[] bins = new float[NUM_BINS];
        double min = SeriesMath.getMin(data);
        double max = SeriesMath.getMax(data);
        double range = max - min;
        double binSize = range / NUM_BINS;
        float[] tmp = data.clone();
        for (int j = 0; j < tmp.length; ++j) {
            float v = (float) (tmp[j] - min);
            int bin = (int) (v / binSize);
            bin = Math.min(bin, NUM_BINS - 1);
            bin = Math.max(bin, 0);
            bins[bin] += 1;
        }
        PeakFinder finder = new PeakFinder();
        ArrayList<BasePeak> peaks = finder.findPeaks(bins, 1.0);
        return peaks.size();
    }
}
