/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
