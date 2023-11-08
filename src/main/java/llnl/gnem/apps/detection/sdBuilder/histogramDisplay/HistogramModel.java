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
package llnl.gnem.apps.detection.sdBuilder.histogramDisplay;

import java.util.logging.Level;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.statistics.DistributionFitter;
import llnl.gnem.apps.detection.statistics.HistogramData;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.TimeT;
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
    private double minAllowableThreshold;
    private double maxAllowableThreshold;

    private HistogramModel() {
        minAllowableThreshold = .02;
        maxAllowableThreshold = 0.8;
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

    public void setMinAllowableThreshold(double minAllowableThreshold) {
        this.minAllowableThreshold = minAllowableThreshold;
    }

    public void setMaxAllowableThreshold(double maxAllowableThreshold) {
        this.maxAllowableThreshold = maxAllowableThreshold;
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

    public  void updateSingleDetectorThreshold(SubspaceDetector sd, TimeT streamTime,int runid) {
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
                if (newThreshold > maxAllowableThreshold) {
                    newThreshold = maxAllowableThreshold;
                }
                if (newThreshold < minAllowableThreshold) {
                    newThreshold = minAllowableThreshold;
                }
                sd.getSpecification().setThreshold((float) newThreshold);
                ApplicationLogger.getInstance().log(Level.FINE,
                        String.format("Detector %d threshold changed from %f to %f", sd.getdetectorid(), threshold, newThreshold));
                double percentChange = Math.abs(newThreshold - threshold)/threshold * 100;
                if( percentChange >= 1){
                    DetectionDAOFactory.getInstance().getDetectorDAO().writeChangedThreshold(runid,sd.getdetectorid(),  streamTime , newThreshold );
                }

            } catch (DataAccessException ex) {
                ApplicationLogger.getInstance().log(Level.SEVERE, "Failed updating threshold history table!", ex);
            }
        }
    }

}
