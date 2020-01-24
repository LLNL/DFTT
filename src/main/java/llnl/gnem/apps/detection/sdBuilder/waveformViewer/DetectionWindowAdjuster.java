/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.signalprocessing.TimeFreqStatistics;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class DetectionWindowAdjuster {
    public void autoResizeWindow(Collection<CorrelationComponent> data) {
        if (data.isEmpty()) {
            return;
        }
        double oldWindowStart = ParameterModel.getInstance().getWindowStart();
        double oldWindowDuration = ParameterModel.getInstance().getCorrelationWindowLength();
        double delta = getDelta(data);
        if (delta <= 0) {
            return;
        }

        double stackWindowStart = oldWindowStart;

        ArrayList<float[]> envelopes = new ArrayList<>();
        int j0Avg = 0;
        int count = 0;
        double averageOffset = 0;
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getTraceData();
            float[] plotData = td.getPlotData();

            double offset = td.getNominalPick().getTime() - td.getTime().getEpochTime();
            averageOffset += offset;
            double thisWindowStart = stackWindowStart + offset;
            int j0 = (int) (thisWindowStart / delta);
            j0Avg += j0;
            ++count;
            int j1 = (int) ((thisWindowStart + oldWindowDuration) / delta);
            if (j1 > plotData.length - 1) {
                j1 = plotData.length - 1;
            }
            int nlen = j1 - j0 + 1;
            float[] tmp = new float[nlen];
            System.arraycopy(plotData, j0, tmp, 0, nlen);
            SeriesMath.RemoveMean(tmp);
            envelopes.add(SeriesMath.envelope(tmp));

        }

        j0Avg /= count;
        averageOffset /= count;
        float[] stack = buildStack(envelopes);
        int N = stack.length;

        Integer[] indexes = SeriesMath.getSortedIndices(stack);

        int n1 = (int) (.1 * N);
        PairT<Double, Double> stats = getRestrictedStats(n1, stack, indexes);
        double median = stats.getFirst();
        double std = stats.getSecond();

        TimeFreqStatistics tfs = new TimeFreqStatistics(stack, delta);

        int peakIndex = stack.length / 2;
        for (int j = 0; j < stack.length; ++j) {
            if (stack[j] > stack[peakIndex]) {
                peakIndex = j;
            }
        }
        double stopThreshold = median + std * 2;
        int i = peakIndex - 1;
        float previous = stack[i];
        while (i > 0 && previous > stopThreshold) {
            previous = stack[--i];
        }
        int startIndex = i + j0Avg;

        int endIndex = peakIndex + (int) (2 * tfs.getSigma() / delta) + j0Avg;

        double windowStart = startIndex * delta - .25 - averageOffset;
        double winLen = Math.max((endIndex - startIndex) * delta + .25, 5.0);
        ParameterModel.getInstance().setWindowStart(windowStart);
        ParameterModel.getInstance().setCorrelationWindowLength(winLen);
        CorrelatedTracesModel.getInstance().adjustAllWindows(windowStart, winLen);
    }
    
    
    private double getDelta(Collection<CorrelationComponent> data) {
        double delta = -1;
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getTraceData();
            delta = td.getDelta();
            return delta;
        }
        return delta;
    }

    private float[] buildStack(ArrayList<float[]> envelopes) {
        int maxLength = 0;
        for (float[] tmp : envelopes) {
            if (tmp.length > maxLength) {
                maxLength = tmp.length;
            }
        }
        float[] stack = new float[maxLength];
        for (float[] tmp : envelopes) {
            for (int i = 0; i < tmp.length; ++i) {
                stack[i] += tmp[i];
            }
        }
        SeriesMath.MeanSmooth(stack, 6);
        return stack;
    }

    private PairT<Double, Double> getRestrictedStats(int nPercent, float[] tmp, Integer[] indexes) {
        float[] subset = new float[nPercent];
        for (int j = 0; j < nPercent; ++j) {
            subset[j] = tmp[indexes[j]];
        }
        double mean = SeriesMath.getMedian(subset);
        double std = SeriesMath.getStDev(subset);
        return new PairT<>(mean, std);
    }

}
