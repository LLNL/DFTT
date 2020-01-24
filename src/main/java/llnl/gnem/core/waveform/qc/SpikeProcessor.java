/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.qc;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.seriesMathHelpers.RollingStats;
import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class SpikeProcessor {

    private static final int MAX_SEPARATION_SAMPLES = 12;
    private static final int MIN_NOISE_WINDOW_SAMPS = 50;
    private static final double STATS_WIN_LENGTH_SECONDS = 5;
    final double globalZScoreThreshold;
    final double maxSpikeDuration;
    final double localZScoreThreshold;

    public SpikeProcessor() {
        globalZScoreThreshold = 25;
        maxSpikeDuration = 3.0;
        localZScoreThreshold = 20;
    }

    public SpikeProcessor(double globalZScore, double maxSpikeDuration, double localZScore) {
        this.globalZScoreThreshold = globalZScore;
        this.maxSpikeDuration = maxSpikeDuration;
        this.localZScoreThreshold = localZScore;
    }

    public Collection<DataSpike> scanForSpikes(TimeSeries inSeis) {

        TimeSeries seis = new TimeSeries(inSeis);
        Collection<DataSpike> results = new ArrayList<>();
        double seisStart = seis.getTimeAsDouble();
        double delta = seis.getDelta();
        seis.RemoveMedian();
        seis.Taper(1.0);
        seis.toEnvelope();
        seis.Smooth(3);
        seis.Smooth(3);
        float[] tmp = seis.getData();
        int N = tmp.length;
        int n2 = N / 2;

        ApplicationLogger.getInstance().log(Level.FINE, "building indexes");
        Integer[] indexes = SeriesMath.getSortedIndices(tmp);
        float median = tmp[indexes[n2]];
        int n99 = (int) (.99 * N);
        double nonExtremaStd = getRestrictedStd(n99, tmp, indexes);

        double minSpikeAmplitude = median + globalZScoreThreshold * nonExtremaStd;
        TreeMap<Integer, Candidate> candidates = buildCandidateMap(n99, N, indexes,
                tmp, minSpikeAmplitude, median, nonExtremaStd);
        Collection<Collection<Candidate>> grouped = mergeAdjacentSpikes(candidates);

        ArrayList<Candidate> durationPruned = pruneByDuration(grouped, tmp, delta, maxSpikeDuration);
        int statsWinSamples = (int) (STATS_WIN_LENGTH_SECONDS / delta);
        int M = durationPruned.size();
        for (int j = 0; j < M; ++j) {
            Candidate candidate = durationPruned.get(j);
            int preWinStart = candidate.begin - statsWinSamples;
            if (preWinStart < 0) {
                preWinStart = 0;
            }
            int npts = candidate.begin - preWinStart;
            if (npts < MIN_NOISE_WINDOW_SAMPS) {
                continue;
            }
            float[] window = new float[npts];
            System.arraycopy(tmp, preWinStart, window, 0, npts);
            RollingStats preStats = new RollingStats(window);
            int postWinEnd = candidate.end + statsWinSamples;
            if (postWinEnd > N - 1) {
                postWinEnd = N - 1;
            }
            if (j < M - 1) {
                Candidate next = durationPruned.get(j + 1);
                if (next.begin <= postWinEnd) {
                    postWinEnd = next.begin - 1;
                }
            }
            npts = postWinEnd - candidate.end;
            if (npts < MIN_NOISE_WINDOW_SAMPS) {
                continue;
            }
            window = new float[npts];
            System.arraycopy(tmp, candidate.end, window, 0, npts);
            RollingStats postStats = new RollingStats(window);

            double maxDeviation = tmp[candidate.max] - preStats.getMean();

            double localPreZScore = maxDeviation / preStats.getStandardDeviation();
            if (localPreZScore < 0) {
                continue;
            }

            double postTrigStd = postStats.getStandardDeviation();
            double localPostZScore = maxDeviation / postTrigStd;
            if (localPostZScore < 0) {
                continue; // signal following candidate is larger than spike amplitude
            }

            double localZ = Math.min(localPreZScore, localPostZScore);
            DataSpike spike = new DataSpike(seisStart, delta * candidate.begin,
                    delta * candidate.end, preStats.getMean(),
                    preStats.getStandardDeviation(), maxDeviation,
                    localZ);
            if (localZ > localZScoreThreshold && candidate.durationOk) {
                results.add(spike);
            }

        }

        return results;
    }

    private ArrayList<Candidate> pruneByDuration(Collection<Collection<Candidate>> grouped, float[] tmp, double delta, double maxSpikeDuration) {
        ArrayList<Candidate> durationPruned = new ArrayList<>();
        for (Collection<Candidate> group : grouped) {
            int minIndex = Integer.MAX_VALUE;
            int maxIndex = -minIndex;
            double maxValue = -Double.MAX_VALUE;
            int centerIndex = -1;
            for (Candidate candidate : group) {
                if (candidate.begin < minIndex) {
                    minIndex = candidate.begin;
                }
                if (candidate.end > maxIndex) {
                    maxIndex = candidate.end;
                }
                double value = tmp[candidate.max];
                if (value > maxValue) {
                    maxValue = value;
                    centerIndex = candidate.max;
                }
            }
            double duration = (maxIndex - minIndex) * delta;
            if (duration <= maxSpikeDuration && centerIndex >= 0) {
                durationPruned.add(new Candidate(minIndex, centerIndex, maxIndex));
            } else {
                durationPruned.add(new Candidate(minIndex, centerIndex, maxIndex, false));
            }
        }
        return durationPruned;
    }

    private Collection<Collection<Candidate>> mergeAdjacentSpikes(TreeMap<Integer, Candidate> candidates) {
        Collection<Collection<Candidate>> grouped = new ArrayList<>();
        if (candidates.isEmpty()) {
            return grouped;
        }
        if (candidates.size() == 1) {
            grouped.add(candidates.values());
            return grouped;
        }
        ArrayList<Integer> startIndexes = new ArrayList<>(candidates.keySet());
        int m = 0;
        Collection<Candidate> adjacentSpikes = new ArrayList<>();
        Candidate candidate = candidates.get(startIndexes.get(m));
        adjacentSpikes.add(candidate);
        while (m < startIndexes.size() - 1) {
            Candidate next = candidates.get(startIndexes.get(m + 1));
            if (next.begin - candidate.end < MAX_SEPARATION_SAMPLES) {
                adjacentSpikes.add(next);
            } else {
                grouped.add(adjacentSpikes);
                adjacentSpikes = new ArrayList<>();
                adjacentSpikes.add(next);
            }
            candidate = next;
            ++m;
        }
        return grouped;
    }

    private TreeMap<Integer, Candidate> buildCandidateMap(int n99,
            int N, Integer[] indexes, float[] tmp,
            double minSpikeAmplitude, float median, double nonExtremaStd) {
        Map<Integer, Float> indexValMap = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        findSpikePeaks(n99, N, indexes, tmp, minSpikeAmplitude, stack, indexValMap);
        TreeMap<Integer, Candidate> candidates = getSpikeBoundaries(median, nonExtremaStd, stack, indexValMap, tmp, N);
        return candidates;
    }

    private void findSpikePeaks(int n99, int N, Integer[] indexes, float[] tmp,
            double minSpikeAmplitude, Deque<Integer> stack, Map<Integer, Float> indexValMap) {
        for (int i = n99; i < N; ++i) {
            int k = indexes[i];
            if (k > 0 && k < N-1) {
                float left = tmp[k - 1];
                float center = tmp[k];
                float right = tmp[k + 1];
                if (center >= minSpikeAmplitude && center >= left && center >= right) {
                    stack.push(k);
                    indexValMap.put(k, center);
                }
            }
        }
    }

    private TreeMap<Integer, Candidate> getSpikeBoundaries(float median,
            double std, Deque<Integer> stack, Map<Integer, Float> indexValMap,
            float[] tmp, int N) {
        int i;
        double stopThreshold = median + 3 * std;
        TreeMap<Integer, Candidate> candidates = new TreeMap<>();
        while (!stack.isEmpty()) {
            int peakIndex = stack.pop();
            Float peakValue = indexValMap.remove(peakIndex);
            if (peakValue == null) {
                continue;
            }
            i = peakIndex - 1;
            float value = peakValue;
            float previous = tmp[i];
            while (i > 0 && previous < value && previous > stopThreshold) {
                value = previous;
                previous = tmp[--i];
            }
            int beginIdx = i;
            for (i = beginIdx; i <= peakIndex; ++i) {
                indexValMap.remove(i);
            }
            i = peakIndex + 1;
            value = peakValue;
            float next = tmp[i];
            while (i < N - 1 && next < value && next > stopThreshold) {
                value = next;
                next = tmp[++i];
            }
            int endIndex = i;
            for (i = peakIndex + 1; i <= endIndex; ++i) {
                indexValMap.remove(i);
            }
            candidates.put(beginIdx, new Candidate(beginIdx, peakIndex, endIndex));
        }
        return candidates;
    }

    class Candidate {

        int begin;
        int max;
        int end;
        boolean durationOk;

        Candidate(int a, int b, int c) {
            begin = a;
            max = b;
            end = c;
            durationOk = true;
        }

        Candidate(int a, int b, int c, boolean ok) {
            begin = a;
            max = b;
            end = c;
            durationOk = ok;
        }

    }

    private double getRestrictedStd(int n99, float[] tmp, Integer[] indexes) {
        ApplicationLogger.getInstance().log(Level.FINE, "retrieving lower 95% values");
        float[] noSpikes = new float[n99];
        for (int j = 0; j < n99; ++j) {
            noSpikes[j] = tmp[indexes[j]];
        }
        double std = SeriesMath.getStDev(noSpikes);
        return std;
    }
}
