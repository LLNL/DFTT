/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.histogramDisplay;

import java.util.logging.Level;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.statistics.DistributionFitter;
import llnl.gnem.apps.detection.statistics.HistogramData;
import llnl.gnem.core.util.ApplicationLogger;
import org.apache.commons.math3.distribution.BetaDistribution;

/**
 *
 * @author dodge1
 */
public class HistogramModel {

    /**
     * @return the detectorid
     */
    public int getDetectorid() {
        return detectorid;
    }

    /**
     * @return the runid
     */
    public int getRunid() {
        return runid;
    }

    private HistogramView view;
    private HistogramData histogram;
    private BetaDistribution betaDist;
    private int detectorid;
    private int runid;

    private HistogramModel() {
        histogram = null;
        betaDist = null;
    }

    public static HistogramModel getInstance() {
        return TemplateModelHolder.INSTANCE;
    }

    void setHistogram(HistogramData result,int detectorid, int runid) {
        clear();
        this.detectorid = detectorid;
        this.runid = runid;
        histogram = result;
        betaDist = null;
        if (result != null && view != null) {
            view.displayHistogram();
        }

    }

    public HistogramData getHistogram() {
        return histogram;
    }

    public BetaDistribution getHistogramFitBetaDistribution() {
        if (betaDist == null) {
            betaDist = DistributionFitter.getBetaDistribution(histogram);
        }
        return betaDist;
    }

    public double getThreshold() {
        BetaDistribution adist = getHistogramFitBetaDistribution();
        if (adist != null) {
            return getInverseCumulativeDensity(adist, 1.0 - DistributionFitter.getFixedFalseAlarmRate());
        } else {
            throw new IllegalStateException("Failed to getDistribution function!");
        }
    }

    private static class TemplateModelHolder {

        private static final HistogramModel INSTANCE = new HistogramModel();
    }

    public void setView(HistogramView view) {
        this.view = view;
    }

    public void clear() {
        histogram = null;
        if (view != null) {
            view.clear();
        }
    }

    public static double getInverseCumulativeDensity(BetaDistribution adist, double p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Argument is out of range!");
        }
        int nMax = 100;
        double tol = 0.00001;
        double lower = 0;
        double mid = 0.5;
        double upper = 1;

        int j = 0;
        while (j < nMax) {
            mid = (lower + upper) / 2;
            double pMid = adist.cumulativeProbability(mid);
            if ((upper - lower) / 2 < tol) {
                return mid;
            }
            if (pMid < p) {
                lower = mid;
            } else if (pMid > p) {
                upper = mid;
            } else {
                return mid;
            }
            ++j;
        }

        return mid;
    }

    public static void updateSingleDetectorThreshold(SubspaceDetector sd) {
        double threshold = sd.getSpecification().getThreshold();
        long[] binVals = sd.getHistogramValues();

        float sum = 0;

        int nUseful = 0;
        float[] bins = new float[binVals.length];
        float[] values = new float[binVals.length];
        for (int j = 0; j < bins.length; ++j) {
            if (binVals[j] > 0) {
                ++nUseful;
            }
            sum += binVals[j];
        }
        float binWidth = 1.0f / bins.length;
        if (nUseful >= 5) {
            try {
                for (int j = 0; j < binVals.length; ++j) {
                    float v1 = j;
                    float v2 = binVals[j];
                    bins[j] = v1 * binWidth + binWidth / 2;
                    values[j] = v2 / sum * bins.length;
                }
                HistogramData hist = new HistogramData(bins, values);
                BetaDistribution betaDist = DistributionFitter.getBetaDistribution(hist);

                double tmpThresh = getInverseCumulativeDensity(betaDist, 1.0 - DistributionFitter.getFixedFalseAlarmRate());
                double newThreshold = (threshold *8 + tmpThresh * 2) / 10;
                if (newThreshold > 0.8) {
                    newThreshold = 0.8;
                }
                if (newThreshold < .02) {
                    newThreshold = .02;
                }
                sd.getSpecification().setThreshold((float) newThreshold);
                ApplicationLogger.getInstance().log(Level.FINE,
                        String.format("Detector %d threshold changed from %f to %f", sd.getdetectorid(), threshold, newThreshold));

            } catch (Exception ex) {
                System.out.println("still have not fixed problem!");
            }
        }
    }

}
