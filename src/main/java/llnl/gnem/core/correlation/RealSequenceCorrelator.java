package llnl.gnem.core.correlation;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.oregondsp.signalProcessing.fft.RDFT;
import static java.lang.Math.min;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import llnl.gnem.core.correlation.util.CorrelationMax;
import llnl.gnem.core.correlation.util.ShiftType;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.signalprocessing.Sequence;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 * Created by dodge1 Date: Apr 2, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class RealSequenceCorrelator {

    private static final float ALMOST_ONE = 0.9999f;
    private static final double MIN_ALLOWABLE_CORRELATION = 0.2;
//    private static final double OUTPUT_WRITER_THRESHOLD = 0;
    private static final double OUTPUT_WRITER_THRESHOLD = 0.9;
    private static final double eps = Math.pow(2, -52);
    private ExecutorCompletionService<CorrelationMaxResult> correlationCompService = null;
    private ExecutorService exec;

    public CorrelationResults buildCorrelationMatrices(ChannelDataCollection data, ProgressDialog progress) throws InterruptedException, ExecutionException {

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
        CorrelationResults mccr = computeCrossCorrelations(data.getData(), power, order,
                rdft, sequenceLength, progress);
        return mccr;
    }

    public void configureExecutorService() {
        int cores = Runtime.getRuntime().availableProcessors();

        exec = Executors.newFixedThreadPool(cores);

        correlationCompService = new ExecutorCompletionService<>(exec);
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
     * @param nTraces The number of traces to be adjusted
     * @param shifts
     * @param correlations
     * @param shiftType
     * @param progress
     * @param fixToZeroShifts
     * @return An adjustment matrix containing fractional adjustments.
     */
    public Matrix getAdjustmentVector(int nTraces, Matrix shifts, Matrix correlations, ShiftType shiftType, boolean fixToZeroShifts, ProgressDialog progress) {
        switch (shiftType) {
            case LEAST_SQUARES:
                return getLeastSquaresAdjustmentMatrix(nTraces, shifts, correlations, fixToZeroShifts, progress);
            case DIRECT:
                return shifts.getMatrix(0, shifts.getRowDimension() - 1, 0, 0);
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

    public Matrix getSigmaMatrix(Matrix shifts, Matrix adjustments) {
        int numCols = shifts.getColumnDimension();
        Matrix S = new Matrix(numCols, 1);
        for (int i = 0; i < adjustments.getRowDimension(); ++i) {
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
        return S;
    }

    public CorrelationResults optimizeShifts(ChannelDataCollection data, ShiftType shiftType, ProgressDialog progress) throws InterruptedException, ExecutionException {
        CorrelationResults cmp = buildCorrelationMatrices(data, progress);

        if (progress != null) {
            progress.setText("Computing Adjustment Vector...");
        }
        Matrix adjustments = getAdjustmentVector(data.size(), cmp.getShifts(), cmp.getCorrelations(), shiftType, false, progress);
        if (progress != null) {
            progress.setText("Adjustment Vector Computed.");
        }
        Matrix shifts = cmp.getShifts();
        Matrix S = getSigmaMatrix(shifts, adjustments);
        int idx = 0;
        for (StationEventChannelData secd : data.getData()) {
            secd.addSigma(S.get(idx, 0));
            secd.addShift(adjustments.get(idx++, 0));
        }
        return cmp;
    }

    public void shutCompServicedown() throws InterruptedException {
        exec.shutdown();
        exec.awaitTermination(10, TimeUnit.SECONDS);
        ApplicationLogger.getInstance().log(Level.FINE, "Thread pool 1 shut down.");
        correlationCompService = null;
        exec = null;
    }

    private Matrix buildATWAMatrix(int nTraces, int nt, float[] w) {
        Matrix ATWA = new Matrix(nTraces, nTraces);
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
        return ATWA;
    }

    private Matrix buildATWDTMatrix(int nTraces, float[] w, Matrix CCshift) {
        Matrix ATWDT = new Matrix(nTraces, 1);
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
        return ATWDT;
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

    private CorrelationResults computeCrossCorrelations(List<StationEventChannelData> secdList, int power, int order, RDFT RDFT, int sequenceLength, ProgressDialog progress) throws InterruptedException, ExecutionException {
        // Now for each event-station pair compute the cross-correlation sequence for each channel
        // and sum those to produce the cross correlation sum.

        int N = secdList.size();
        Matrix correlations = new Matrix(N, N);
        Matrix shifts = new Matrix(N, N);
        int m = N * (N - 1) / 2;
        if (progress != null) {
            progress.setProgressBarIndeterminate(false);
            progress.setMinMax(0, m);
            progress.setValue(0);
        }
        int processed = 0;
        int submitted = 0;
        int chunkSize = 1000;
        for (int j = 0; j < N - 1; ++j) {
            for (int k = j + 1; k < N; ++k) {

                StationEventChannelData xData = secdList.get(j);
                StationEventChannelData yData = secdList.get(k);
                if (correlationCompService != null) {
                    RDFT rdft = new RDFT(order);// RDFT is not thread safe!
                    correlationCompService.submit(new CorrelatePairTask(power, xData, yData, rdft, sequenceLength, j, k));
                    ++submitted;
                    if (progress != null) {
                        progress.setText("Processing correlation tasks...");
                        progress.setValue(++processed);
                    }
                    if (submitted == chunkSize) {
                        retrieveTaskResults(submitted, correlations, shifts, secdList);
                        submitted = 0;
                    }
                } else {
                    CorrelationMax correlationResult = correlateStationEventPair(power, xData, yData, RDFT, sequenceLength);
                    updateMatrices(correlationResult, correlations, j, k, shifts, xData, yData);
                    if (progress != null) {
                        progress.setValue(++processed);
                    }
                }

            }
        }
        if (correlationCompService != null && submitted > 0) {
            retrieveTaskResults(submitted, correlations, shifts, secdList);

        }
        for (int j = 0; j < N; ++j) {
            correlations.set(j, j, 1);
            shifts.set(j, j, 0);
        }
        return new CorrelationResults(shifts, correlations, secdList);
    }

    private void computeFFTs(StationEventChannelData secd, double prePickSeconds, double postPickSeconds, TimeSeries seis, int power, RDFT RDFT) {
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

    private CorrelationMax correlateStationEventPair(int power, StationEventChannelData xData, StationEventChannelData yData, RDFT RDFT, int sequenceLength) {
        return correlateStationEventPair(xData, yData, RDFT, sequenceLength);
    }

    private CorrelationMax correlateStationEventPair(StationEventChannelData xData, StationEventChannelData yData, RDFT rdft, int sequenceLength) {

        float[] x = xData.getTransform();
        float[] y = yData.getTransform();
        float[] xy = getCrossCorrelation(x, y, rdft);
        Sequence.cshift(xy, sequenceLength - 1);
        CorrelationMax correlationResult = getCCMax(xData, yData, xy, sequenceLength);
        return correlationResult;
    }

    private CorrelationMax getCCMax(StationEventChannelData xData, StationEventChannelData yData, float[] xy, int sequenceLength) {
        return getCCMax(xData.getMaxCC(), yData.getMaxCC(), xy, sequenceLength);
    }

    private Matrix getLeastSquaresAdjustmentMatrix(int nTraces, Matrix shifts, Matrix correlations, boolean FixToZeroShift, ProgressDialog progress) {
        if (nTraces == 1) {
            return new Matrix(1, 1, 0);
        }
        if (nTraces == 2) {
            return shifts.getMatrix(0, 1, 0, 0);
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
                float ccTmp = (float) correlations.get(j, k);
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
        Matrix ATWA = buildATWAMatrix(nTraces, nt, w);
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Building matrices (ATWDT)...");
        }
        Matrix ATWDT = buildATWDTMatrix(nTraces, w, shifts);
        if (progress != null) {
            progress.setText("Solving system for adjustments...");
        }
        Matrix result = pinv(ATWA,progress).times(ATWDT);
        if (progress != null) {
            progress.setText("Inversion complete.");
        }
        if (result.getRowDimension() > 1) {
            double shift0 = result.get(0, 0);
            for (int j = 0; j < result.getRowDimension(); ++j) {
                if (FixToZeroShift) {
                    result.set(j, 0, 0);
                } else {
                    result.set(j, 0, result.get(j, 0) - shift0);
                }

            }
        }
        return result;
    }

    private static float getTransform(float[] x, RDFT rdft, float[] transform) {
        System.arraycopy(x, 0, transform, 0, x.length);
        rdft.evaluate(transform, transform);
        return getAutocorrelationMax(transform, rdft);
    }

    private static Matrix pinv(Matrix A, ProgressDialog progress) {
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Computing pinv (SVD)...");
        }
        SingularValueDecomposition svd = A.svd();
        if (progress != null) {
            progress.setProgressBarIndeterminate(true);
            progress.setText("Computing pinv (determine effective rank)...");
        }
        Matrix S = svd.getS();
        Matrix U = svd.getU();
        Matrix V = svd.getV();
        double norm2 = svd.norm2();
        double[] sv = svd.getSingularValues();
        int maxA = Math.max(A.getRowDimension(), A.getColumnDimension());
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
        return (U.times(S)).times(V.transpose());
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

    private static CorrelationMax refineEstimate(int indexOfMax, float[] xy, double sum, double ccMax, int sequenceLength) {
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

        double a = ((y1 - y2) * (x2 - x3) - (y2 - y3) * (x1 - x2)) / ((x1 * x1 - x2 * x2) * (x2 - x3) - (x2 * x2 - x3 * x3) * (x1 - x2));
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

    private void retrieveTaskResults(int blockSize, Matrix correlations, Matrix shifts, List<StationEventChannelData> secdList) throws InterruptedException, ExecutionException {
        for (int l = 0; l < blockSize; ++l) {
            Future<CorrelationMaxResult> future = correlationCompService.take();
            CorrelationMaxResult result = future.get();
            CorrelationMax correlationResult = result.getCcmax();
            int j = result.getJ();
            int k = result.getK();
            updateMatrices(correlationResult, correlations, j, k, shifts, secdList.get(j), secdList.get(k));

        }
    }

    private void updateMatrices(CorrelationMax correlationResult, Matrix correlations, int j, int k, Matrix shifts, StationEventChannelData xData, StationEventChannelData yData) {
        double maxCC = min(1.0, correlationResult.getCcMax());
        correlations.set(j, k, maxCC);
        correlations.set(k, j, maxCC);
        double shift = correlationResult.getShift();
        shifts.set(j, k, shift);
        shifts.set(k, j, -1 * shift);
        double shiftSeconds = shifts.get(j, k) / xData.getSampleRate();
        if (maxCC >= OUTPUT_WRITER_THRESHOLD) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("\nSta: %s, Phase: %s, Evid1: %d, Pick1: %f, Evid2: %d, Pick2: %f, MaxCC: %f, Shift: %f",
                    xData.getSta(), xData.getPhase(), xData.getEvid(),
                    xData.getNominalPickTime(), yData.getEvid(),
                    yData.getNominalPickTime(), maxCC, shiftSeconds));
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

        CorrelatePairTask(int power, StationEventChannelData xData, StationEventChannelData yData, RDFT RDFT, int sequenceLength, int j, int k) {
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
