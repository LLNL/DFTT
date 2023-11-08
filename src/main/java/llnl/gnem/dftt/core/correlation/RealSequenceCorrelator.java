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
package llnl.gnem.dftt.core.correlation;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.ojalgo.matrix.Primitive32Matrix;
import org.ojalgo.matrix.Primitive32Matrix.DenseReceiver;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.random.Deterministic;

import com.oregondsp.signalProcessing.fft.RDFT;

import llnl.gnem.dftt.core.correlation.util.CorrelationMax;
import llnl.gnem.dftt.core.correlation.util.ShiftType;
import llnl.gnem.dftt.core.gui.util.ProgressDialog;
import llnl.gnem.dftt.core.signalprocessing.Sequence;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.PairT;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.seismogram.TimeSeries;

/**
 * Created by dodge1 Date: Apr 2, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class RealSequenceCorrelator {

    private static final float ALMOST_ONE = 0.9999f;
    private static final double MIN_ALLOWABLE_CORRELATION = 0.2;
    // private static final double OUTPUT_WRITER_THRESHOLD = 0;
    private static final double OUTPUT_WRITER_THRESHOLD = 0.9;
    private static final double eps = Math.pow(2, -52);

    public CorrelationResults buildCorrelationMatrices(ChannelDataCollection data, ProgressDialog progress)
            throws InterruptedException, ExecutionException {

        if (data.size() < 2) {
            throw new IllegalStateException("Must be at least two elements in ChannelDataCollection!");
        }

        double actualPrePick = data.getPrepickSeconds();

        double actualPostPick = data.getPostPickSeconds();

        int sequenceLength = data.getCommonWindowLengthSamples();
        int order = getOrder(sequenceLength);
        int power = (int) Math.pow(2, order);
        RDFT rdft = new RDFT(order);

        // Compute and save all auto-correlations and transformed sequences
        List<StationEventChannelData> lsecd = data.getData();
        if (progress != null) {
            progress.setText("Computing FFTs...");
            progress.setMinMax(0, lsecd.size());
            progress.setValue(0);
            progress.setProgressBarIndeterminate(false);
        }
        int completed = 0;
        for (StationEventChannelData secd : lsecd) {
            computeFFTs(secd, actualPrePick, actualPostPick, secd.getSeismogram(), power, rdft);
            if (progress != null) {
                progress.setValue(++completed);
            }
        }
        if (progress != null) {
            progress.setText("Creating Cross-correlation Arrays...");
        }
        CorrelationResults mccr = computeCrossCorrelations(data.getData(), power, order, rdft, sequenceLength,
                progress);
        return mccr;
    }

    public static CorrelationMax correlate(TimeSeries seis1, TimeSeries seis2) {
        int sequenceLength = Math.max(seis1.getLength(), seis2.getLength());
        int order = getOrder(sequenceLength);
        int power = (int) Math.pow(2, order);
        RDFT rdft = new RDFT(order);

        float[] transformX = new float[power];
        float ccx = getTransform(seis1.getData(), rdft, transformX);

        float[] transformY = new float[power];
        float ccy = getTransform(seis2.getData(), rdft, transformY);

        float[] xy = getCrossCorrelation(transformX, transformY, rdft);
        Sequence.cshift(xy, sequenceLength - 1);
        CorrelationMax correlationResult = getCCMax(ccx, ccy, xy, sequenceLength);
        return correlationResult;
    }

    public static CorrelationMax correlate(float[] data1, float[] data2) {
        int sequenceLength = Math.max(data1.length, data2.length);
        int order = getOrder(sequenceLength);
        int power = (int) Math.pow(2, order);
        RDFT rdft = new RDFT(order);

        float[] transformX = new float[power];
        float ccx = getTransform(data1, rdft, transformX);

        float[] transformY = new float[power];
        float ccy = getTransform(data2, rdft, transformY);

        float[] xy = getCrossCorrelation(transformX, transformY, rdft);
        Sequence.cshift(xy, sequenceLength - 1);
        CorrelationMax correlationResult = getCCMax(ccx, ccy, xy, sequenceLength);
        return correlationResult;
    }

    /**
     * <p>
     * This method solves for a set of arrival time adjustments dt which when
     * added to the existing arrival times minimizes the inconsistency in
     * crosscorrelation-derived relative times. The method is outlined in
     * VanDecar and Crosson 1990 BSSA vol 80, no 1. p 150-169.
     * </p>
     * <p>
     * Although formally, we are solving a system of the form (A'*W*A) *dt =
     * A'*W*delT, it turns out that the A'WA array has special structure which
     * we can construct directly without forming either the A matrix, or the W
     * matrix. This results in a large saving in memory, and is the way it is
     * implemented here.
     * </p>
     *
     * @param nTraces         The number of traces to be adjusted
     * @param shifts
     * @param correlations
     * @param shiftType
     * @param progress
     * @param fixToZeroShifts
     * @return An adjustment Primitive32Matrix containing fractional
     *         adjustments.
     */
    public Primitive32Matrix getAdjustmentVector(int nTraces, Primitive32Matrix shifts, Primitive32Matrix correlations,
            ShiftType shiftType, boolean fixToZeroShifts, ProgressDialog progress) {
        switch (shiftType) {
            case LEAST_SQUARES:
                return getLeastSquaresAdjustmentMatrix(nTraces, shifts, correlations, fixToZeroShifts, progress);
            case DIRECT:
                return shifts.select(new long[] { 0, shifts.getRowDim() - 1 }, new long[] { 0, 0 });
            default:
                throw new IllegalStateException("Unknown shift type: " + shiftType);
        }

    }

    public static float getAutocorrelationMax(float[] xx, RDFT rdft) {
        float[] x = xx.clone();
        float[] y = xx.clone();
        computeCorrelation(rdft, x, y);
        float result = 0;
        for (float v : x) {
            if (v > result) {
                result = v;
            }
        }

        return result;
    }

    public Primitive32Matrix getSigmaMatrix(Primitive32Matrix shifts, Primitive32Matrix adjustments) {
        int numCols = shifts.getColDim();
        DenseReceiver S = Primitive32Matrix.FACTORY.makeDense(numCols, 1);
        for (int i = 0; i < adjustments.getRowDim(); ++i) {
            double sum1 = 0;
            double sum2 = 0;
            for (int j = 0; j < i - 1; ++j) {
                double res = shifts.get(j, i) - (adjustments.get(j, 0) - adjustments.get(i, 0));
                sum1 += res * res;
            }

            for (int j = i + 1; j < numCols; ++j) {
                double res = shifts.get(i, j) - (adjustments.get(i, 0) - adjustments.get(j, 0));
                sum2 += res * res;
            }
            int factor = Math.max(1, numCols - 2);
            double sigma = Math.sqrt(1.0 / factor * (sum1 + sum2));
            S.set(i, 0, sigma);
        }
        return S.get();
    }

    public CorrelationResults optimizeShifts(ChannelDataCollection data, ShiftType shiftType, ProgressDialog progress)
            throws InterruptedException, ExecutionException {
        CorrelationResults cmp = buildCorrelationMatrices(data, progress);

        if (progress != null) {
            progress.setText("Computing Adjustment Vector...");
        }
        Primitive32Matrix adjustments = getAdjustmentVector(data.size(), cmp.getShifts(), cmp.getCorrelations(),
                shiftType, false, progress);
        if (progress != null) {
            progress.setText("Adjustment Vector Computed.");
        }
        Primitive32Matrix shifts = cmp.getShifts();
        Primitive32Matrix S = getSigmaMatrix(shifts, adjustments);
        int idx = 0;
        for (StationEventChannelData secd : data.getData()) {
            secd.addSigma(S.get(idx, 0));
            secd.addShift(adjustments.get(idx++, 0));
        }
        return cmp;
    }

    private Primitive64Matrix buildATWAMatrix(int nTraces, int nt, float[] w) {
        Primitive64Matrix.DenseReceiver ATWA = Primitive64Matrix.FACTORY.makeDense(nTraces, nTraces);
        for (int j = 0; j < nTraces; ++j) {
            for (int k = 0; k < nTraces; ++k) {
                ATWA.set(j, k, 0);
            }
        }

        int index = 1;
        for (int j = 0; j < nTraces - 1; ++j) {
            int len = nTraces - j - 1;
            int mm = j + 1;
            for (int k = index - 1; k < index + len - 1; ++k) {
                ATWA.set(j, mm++, -w[k]);
            }
            index += len;
        }

        for (int j = 0; j < nTraces; ++j) {
            for (int k = 0; k < nTraces; ++k) {
                ATWA.set(k, j, ATWA.get(j, k));
            }
        }

        for (int j = 0; j < nTraces; ++j) {
            double sum = 0;
            for (int k = 0; k < nTraces; ++k) {
                sum += ATWA.get(j, k);
            }
            ATWA.set(j, j, -sum);
        }
        for (int j = 0; j < nTraces; ++j) {
            for (int k = 0; k < nTraces; ++k) {
                ATWA.set(j, k, ATWA.get(j, k) + w[nt - 1]);
            }
        }
        return ATWA.get();
    }

    private Primitive32Matrix buildATWDTMatrix(int nTraces, float[] w, Primitive32Matrix CCshift) {
        DenseReceiver ATWDT = Primitive32Matrix.FACTORY.makeDense(nTraces, 1);
        int index = 0;
        for (int j = 0; j < nTraces - 1; ++j) {
            for (int k = j + 1; k < nTraces; ++k) {
                w[index] *= CCshift.get(j, k);
                ++index;
            }
        }

        index = 1;
        for (int j = 0; j < nTraces - 1; ++j) {
            int len = nTraces - j - 1;
            double sum = 0;
            for (int k = index - 1; k < index + len - 1; ++k) {
                sum += w[k];
            }
            ATWDT.set(j, 0, sum);
            index += len;
        }

        index = 0;
        for (int j = 0; j < nTraces - 1; ++j) {
            int start = j + 1;
            for (int k = start; k < nTraces; ++k) {
                ATWDT.set(k, 0, ATWDT.get(k, 0) - w[index]);
                ++index;
            }
        }
        return ATWDT.get();
    }

    private static void computeCorrelation(RDFT rdft, float[] x, float[] y) {
        int n = x.length;
        int n2 = n / 2;
        x[0] *= y[0];
        for (int j = 1; j < n2; ++j) {
            float tmp = x[j] * y[j] + x[n - j] * y[n - j];
            float tmp2 = x[n - j] * y[j] - x[j] * y[n - j];
            x[j] = tmp;
            x[n - j] = tmp2;
        }
        x[n2] *= y[n2];

        rdft.evaluateInverse(x, x);
    }

    private CorrelationResults computeCrossCorrelations(List<StationEventChannelData> secdList, int power, int order,
            RDFT RDFT, int sequenceLength, ProgressDialog progress)
            throws InterruptedException, ExecutionException {
        // Now for each event-station pair compute the cross-correlation sequence for
        // each channel
        // and sum those to produce the cross correlation sum.

        int N = secdList.size();
        DenseReceiver correlations = Primitive32Matrix.FACTORY.makeDense(N, N);
        DenseReceiver shifts = Primitive32Matrix.FACTORY.makeDense(N, N);
        int m = N * (N - 1) / 2;
        if (progress != null) {
            progress.setText("Performing correlation tasks...");
            progress.setProgressBarIndeterminate(false);
            progress.setMinMax(0, m);
            progress.setValue(0);
        }
        int submitted = 0;
        int chunkSize = 100000;
        List<CorrelationMaxResult> results = new ArrayList<>();
        List<PairT<Integer, Integer>> indexList = new ArrayList<>();
        for (int j = 0; j < N - 1; ++j) {
            for (int k = j + 1; k < N; ++k) {
                indexList.add(new PairT<>(j, k));
                if (indexList.size() >= chunkSize) {
                    List<CorrelationMaxResult> results2 = indexList.parallelStream()
                            .map(t -> produceCorrelationResult(t, secdList, order, power, sequenceLength))
                            .filter(Objects::nonNull).collect(Collectors.toList());
                    if (progress != null) {
                        submitted += indexList.size();
                        progress.setValue(submitted);
                    }
                    results.addAll(results2);
                    indexList.clear();
                }
            }
        }
        if (!indexList.isEmpty()) {
            List<CorrelationMaxResult> results2 = indexList.parallelStream()
                    .map(t -> produceCorrelationResult(t, secdList, order, power, sequenceLength))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (progress != null) {
                submitted += indexList.size();
                progress.setValue(submitted);
            }
            results.addAll(results2);
            indexList.clear();
        }
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
        }

        for (CorrelationMaxResult result : results) {
            CorrelationMax correlationResult = result.getCcmax();
            int j = result.getJ();
            int k = result.getK();
            updateMatrices(correlationResult, correlations, j, k, shifts, secdList.get(j), secdList.get(k));
        }
        for (int j = 0; j < N; ++j) {
            correlations.set(j, j, 1);
            shifts.set(j, j, 0);
        }
        return new CorrelationResults(shifts.get(), correlations.get(), secdList);
    }

    private CorrelationMaxResult produceCorrelationResult(final PairT<Integer, Integer> pair,
            final List<StationEventChannelData> secdList, final int order, final int power, final int sequenceLength) {
        int j = pair.getFirst();
        int k = pair.getSecond();
        StationEventChannelData xData = secdList.get(j);
        StationEventChannelData yData = secdList.get(k);
        RDFT rdft = new RDFT(order);// RDFT is not thread safe!
        CorrelatePairTask cpt = new CorrelatePairTask(power, xData, yData, rdft, sequenceLength, j, k);
        CorrelationMaxResult result;
        try {
            result = cpt.call();
            return result;
        } catch (Exception ex) {
            Logger.getLogger(RealSequenceCorrelator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void computeFFTs(StationEventChannelData secd, double prePickSeconds, double postPickSeconds,
            TimeSeries seis, int power, RDFT RDFT) {
        double nominalPickTime = secd.getNominalPickTime();
        TimeT start = new TimeT(nominalPickTime - prePickSeconds);
        TimeT end = new TimeT(nominalPickTime + postPickSeconds);
        float[] x = seis.getSubSection(start, end);
        float[] xx = new float[power];
        System.arraycopy(x, 0, xx, 0, x.length);
        RDFT.evaluate(xx, xx);
        float acMax = getAutocorrelationMax(xx, RDFT);
        secd.addTransform(xx);
        secd.addCCMax(acMax);

    }

    private CorrelationMax correlateStationEventPair(int power, StationEventChannelData xData,
            StationEventChannelData yData, RDFT RDFT, int sequenceLength) {
        return correlateStationEventPair(xData, yData, RDFT, sequenceLength);
    }

    private CorrelationMax correlateStationEventPair(StationEventChannelData xData, StationEventChannelData yData,
            RDFT rdft, int sequenceLength) {

        float[] x = xData.getTransform();
        float[] y = yData.getTransform();
        float[] xy = getCrossCorrelation(x, y, rdft);
        Sequence.cshift(xy, sequenceLength - 1);
        CorrelationMax correlationResult = getCCMax(xData, yData, xy, sequenceLength);
        return correlationResult;
    }

    private CorrelationMax getCCMax(StationEventChannelData xData, StationEventChannelData yData, float[] xy,
            int sequenceLength) {
        return getCCMax(xData.getMaxCC(), yData.getMaxCC(), xy, sequenceLength);
    }

    private Primitive32Matrix getLeastSquaresAdjustmentMatrix(int nTraces, Primitive32Matrix shifts,
            Primitive32Matrix correlations, boolean FixToZeroShift, ProgressDialog progress) {
        if (nTraces == 1) {
            return Primitive32Matrix.FACTORY.makeFilled(1, 1, new Deterministic());
        }
        if (nTraces == 2) {
            return shifts.select(new long[] { 0, 1 }, new long[] { 0, 0 });
        }
        int nt = (nTraces * (nTraces - 1)) / 2 + 1;

        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Building matrices...");
        }
        float[] w = new float[nt];
        Arrays.fill(w, 1);
        int windex = 0;
        for (int j = 0; j < nTraces - 1; ++j) {
            for (int k = j + 1; k < nTraces; ++k) {
                float ccTmp = correlations.get(j, k).floatValue();
                if (ccTmp >= 1) {
                    ccTmp = ALMOST_ONE;
                }
                if (ccTmp < MIN_ALLOWABLE_CORRELATION) {
                    ccTmp = 0;
                }
                double term = ccTmp / (1 - ccTmp);
                w[windex++] = (float) (term * term);
            }
        }

        preventSingularMatrix(w);
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Building matrices (ATWA)...");
        }
        Primitive64Matrix atwa = buildATWAMatrix(nTraces, nt, w);

        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Building matrices (ATWDT)...");
        }
        Primitive32Matrix ATWDT = buildATWDTMatrix(nTraces, w, shifts);

        Primitive64Matrix atwdt = Primitive64Matrix.FACTORY.rows(ATWDT).transpose();
        if (progress != null) {
            progress.setText("Solving system for adjustments...");
        }
        Primitive64Matrix aresult = atwa.invert().multiply(atwdt);
        if (progress != null) {
            progress.setText("Inversion complete.");
        }
        DenseReceiver result = Primitive32Matrix.FACTORY.makeDense((int) aresult.countRows(),
                (int) aresult.countColumns());
        if (aresult.countRows() > 1) {
            double shift0 = aresult.get(0, 0);
            for (int j = 0; j < aresult.countRows(); ++j) {
                if (FixToZeroShift) {
                    result.set(j, 0, 0);
                } else {
                    result.set(j, 0, aresult.get(j, 0) - shift0);
                }
            }
        }
        return result.get();
    }

    private static float getTransform(float[] x, RDFT rdft, float[] transform) {
        System.arraycopy(x, 0, transform, 0, x.length);
        rdft.evaluate(transform, transform);
        return getAutocorrelationMax(transform, rdft);
    }

    private static Primitive32Matrix pinv(Primitive32Matrix A, ProgressDialog progress) {
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Computing pinv (SVD)...");
        }
        SingularValue<Double> svd = SingularValue.PRIMITIVE.make(A);
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Computing pinv (determine effective rank)...");
        }
        DenseReceiver S = Primitive32Matrix.FACTORY.makeWrapper(svd.getD()).copy();
        Primitive32Matrix U = Primitive32Matrix.FACTORY.make(svd.getU());
        Primitive32Matrix V = Primitive32Matrix.FACTORY.make(svd.getV());
        double norm2 = svd.getOperatorNorm();
        double[] sv = svd.getSingularValues().toRawCopy1D();
        int maxA = Math.max(A.getRowDim(), A.getColDim());
        double tolerance = maxA * norm2 * eps;
        for (int j = 0; j < sv.length; ++j) {
            if (sv[j] >= tolerance) {
                S.set(j, j, 1 / sv[j]);
            } else {
                S.set(j, j, 0);
            }
        }
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Computing pinv (re-compose)...");
        }
        return (U.multiply(S.get())).multiply(V.transpose());
    }

    private void preventSingularMatrix(float[] w) {
        int count = 0;
        for (int j = 0; j < w.length - 1; ++j) {
            if (w[j] > 0) {
                count++;
            }
        }
        if (count < 2) {
            Arrays.fill(w, 1);
        }
    }

    private static CorrelationMax refineEstimate(int indexOfMax, float[] xy, double sum, double ccMax,
            int sequenceLength) {
        int idx1 = indexOfMax - 1;
        int idx2 = indexOfMax + 1;
        if (idx1 < 0) { // Bad index. Return a shift of zero and CC of zero
            return new CorrelationMax(0, 0, sequenceLength);
        }
        double x1 = idx1;
        double x2 = indexOfMax;
        double x3 = idx2;

        double y1 = xy[idx1] / sum;
        double y2 = ccMax;
        double y3 = xy[idx2] / sum;

        double a = ((y1 - y2) * (x2 - x3) - (y2 - y3) * (x1 - x2))
                / ((x1 * x1 - x2 * x2) * (x2 - x3) - (x2 * x2 - x3 * x3) * (x1 - x2));
        double b = (y1 - y2) / (x1 - x2) - a * (x1 * x1 - x2 * x2) / (x1 - x2);
        double c = y1 - b * x1 - a * x1 * x1;
        double x0 = -b / 2 / a;
        double y0 = a * x0 * x0 + b * x0 + c;

        if (y0 > ccMax && y0 <= 1) {
            return new CorrelationMax(y0, x0 - sequenceLength + 1, sequenceLength);
        } else {
            return new CorrelationMax(ccMax, indexOfMax - sequenceLength + 1, sequenceLength);
        }
    }

    private void updateMatrices(CorrelationMax correlationResult, DenseReceiver correlations, int j, int k,
            DenseReceiver shifts, StationEventChannelData xData, StationEventChannelData yData) {
        double maxCC = min(1.0, correlationResult.getCcMax());
        correlations.set(j, k, maxCC);
        correlations.set(k, j, maxCC);
        double shift = correlationResult.getShift();
        shifts.set(j, k, shift);
        shifts.set(k, j, -1 * shift);
        double shiftSeconds = shifts.get(j, k) / xData.getSampleRate();
        if (maxCC >= OUTPUT_WRITER_THRESHOLD) {
            ApplicationLogger.getInstance()
                    .log(
                            Level.FINE,
                            String.format(
                                    "\nSta: %s, Phase: %s, Evid1: %d, Pick1: %f, Evid2: %d, Pick2: %f, MaxCC: %f, Shift: %f",
                                    xData.getSta(),
                                    xData.getPhase(),
                                    xData.getEvid(),
                                    xData.getNominalPickTime(),
                                    yData.getEvid(),
                                    yData.getNominalPickTime(),
                                    maxCC,
                                    shiftSeconds));
        }
    }

    private static CorrelationMax getCCMax(double ccx, double ccy, float[] xy, int sequenceLength) {
        double sum = Math.sqrt(ccx * ccy);

        double ccMax = 0;
        int idx = -1;
        int end = Math.min(2 * sequenceLength, xy.length);

        for (int j = 0; j < end; ++j) {
            float aXy = xy[j];
            double cc = aXy / sum;
            if (cc > ccMax) {
                ccMax = cc;
                idx = j;
            }
        }
        return refineEstimate(idx, xy, sum, ccMax, sequenceLength);
    }

    private static float[] getCrossCorrelation(float[] xx, float[] yy, RDFT rdft) {
        float[] x = xx.clone();
        float[] y = yy.clone();
        computeCorrelation(rdft, x, y);
        return x;
    }

    private static int getOrder(int n) {
        int requiredLength = 2 * n - 1; // Get all lags
        int order = 0;
        int power = 1;
        while (power < requiredLength) {
            order += 1;
            power *= 2;
        }
        return order;
    }

    private class CorrelationMaxResult {

        private final CorrelationMax ccmax;
        private final int j;
        private final int k;

        CorrelationMaxResult(CorrelationMax ccmax, int j, int k) {
            this.ccmax = ccmax;
            this.j = j;
            this.k = k;
        }

        /**
         * @return the ccmax
         */
        public CorrelationMax getCcmax() {
            return ccmax;
        }

        /**
         * @return the j
         */
        public int getJ() {
            return j;
        }

        /**
         * @return the k
         */
        public int getK() {
            return k;
        }
    }

    class CorrelatePairTask implements Callable<CorrelationMaxResult> {

        private final int power;
        private final StationEventChannelData xData;
        private final RDFT RDFT;
        private final StationEventChannelData yData;
        private final int sequenceLength;
        private final int j;
        private final int k;

        CorrelatePairTask(int power, StationEventChannelData xData, StationEventChannelData yData, RDFT RDFT,
                int sequenceLength, int j, int k) {
            this.power = power;
            this.xData = xData;
            this.yData = yData;
            this.RDFT = RDFT;
            this.sequenceLength = sequenceLength;
            this.j = j;
            this.k = k;
        }

        @Override
        public CorrelationMaxResult call() throws Exception {
            CorrelationMax correlationResult = correlateStationEventPair(power, xData, yData, RDFT, sequenceLength);
            return new CorrelationMaxResult(correlationResult, j, k);
        }
    }
}
