/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
