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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import java.util.ArrayList;
import java.util.List;
import llnl.gnem.apps.detection.statistics.HistogramData;

/**
 *
 * @author dodge1
 */
public class ProjectionCollection {

    private final int detectorid;
    private final ArrayList<DetectorProjection> projections;
    private static final int NUM_BINS = 100;
    private final float[] bins;

    public ProjectionCollection(int detectorid, List<DetectorProjection> projections) {
        this.detectorid = detectorid;
        this.projections = new ArrayList<>(projections);
        bins = new float[NUM_BINS];
        float binWidth = 1.0f / NUM_BINS;
        for (int j = 0; j < NUM_BINS; ++j) {
            bins[j] = j * binWidth + binWidth / 2;
        }
    }

    public int getDetectorid() {
        return detectorid;
    }

    public ArrayList<DetectorProjection> getProjections() {
        return new ArrayList<>(projections);
    }

    public HistogramData getHistogram() {
        float[] values = new float[NUM_BINS];
        for (DetectorProjection dp : projections) {
            double p = dp.getProjection();
            int idx = (int) (p * 100) - 1;
            if (idx < 0) {
                idx = 0;
            }
            values[idx] += 1;
        }
        return new HistogramData(bins, values);
    }

}
