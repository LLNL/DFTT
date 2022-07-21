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
package llnl.gnem.apps.detection.gaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import llnl.gnem.core.waveform.qc.DataDefect;
import llnl.gnem.core.waveform.qc.DataGap;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.merge.IntWaveform;
import llnl.gnem.core.waveform.merge.NamedIntWaveform;

/**
 *
 * @author dodge1
 */
public class GapManager {

    private static GapManager instance = null;
    private final Random generator;
    private final Map<StreamKey, Param> paramLookup;
    private static final int MIN_BUFFER_LENGTH = 20;
    private static final int PREF_BUFFER_LENGTH = 100;

    public static GapManager getInstance() {
        if (instance == null) {
            instance = new GapManager();
        }
        return instance;
    }

    private GapManager() {
        generator = new Random();
        paramLookup = new ConcurrentHashMap<>();

    }

    private Collection<GapFillData> findDataGaps(StreamKey staChan, int[] data) {
        ArrayList<PairT<Integer, Integer>> gapIndices = new ArrayList<>();
        int minGapLength = 5;
        boolean inGap = false;
        int gapStart = -1;
        int gapEnd = -1;
        for (int j = 0; j < data.length; ++j) {
            int value = data[j];
            if (value == 0) {
                if (!inGap) {

                    inGap = true;
                    gapStart = j;
                }
            } else {
                if (inGap) {
                    gapEnd = j - 1;
                    if (gapEnd >= 0 && gapStart >= 0 && gapEnd - gapStart >= minGapLength) {
                        gapIndices.add(new PairT<>(gapStart, gapEnd));
                    }
                }
                inGap = false;
                gapStart = -1;
                gapEnd = -1;
            }
        }
        if (inGap && gapStart >= 0) {
            gapEnd = data.length - 1;
            gapIndices.add(new PairT<>(gapStart, gapEnd));
        }

        Collection<GapFillData> result = new ArrayList<>();
        for (int k = 0; k < gapIndices.size(); ++k) {
            Param pre = null;
            Param post = null;
            PairT<Integer, Integer> gap = gapIndices.get(k);
            int idx1 = gap.getFirst();
            int idx2 = gap.getSecond();
            int idx0 = getPreBufferStart(k, gapIndices, idx1);
            if (idx1 - idx0 >= MIN_BUFFER_LENGTH) {
                pre = computeParam(idx0, idx1, data);

            }
            if (pre == null) {
                pre = getLastEstimate(staChan);
            } else {
                paramLookup.remove(staChan);
                paramLookup.put(staChan, pre);
            }
            int idx3 = getPostBufferEnd(k, data.length - 1, gapIndices, idx2);
            if (idx3 - idx2 >= MIN_BUFFER_LENGTH) {
                post = computeParam(idx2, idx3, data);

            }
            if (post == null) {
                post = getLastEstimate(staChan);
            } else {
                paramLookup.remove(staChan);
                paramLookup.put(staChan, post);
            }
            result.add(new GapFillData(new PrePostParamPair(pre, post), idx1, idx2));
        }
        return result;

    }

    public NamedIntWaveform maybeFillGaps(NamedIntWaveform waveform) {
        int[] data = waveform.getData();
        double samprate = waveform.getRate();
        TimeT start = new TimeT(waveform.getStart());
        StreamKey staChan = waveform.getKey();
        Collection<GapFillData> gaps = findDataGaps(staChan, data);
        boolean hasGaps = !gaps.isEmpty();
        Collection<DataDefect> dataGaps = new ArrayList<>();
        for (GapFillData epoch : gaps) {
            int startSample = epoch.getGapStart();
            int lastSample = epoch.getGapEnd();
            PrePostParamPair boundaryStats = epoch.getPrePostStats();
            double significance = boundaryStats.getShiftSignificance();
            Epoch gapEpoch = new Epoch(start.getEpochTime() + startSample / samprate, start.getEpochTime() + lastSample / samprate);
            if (significance >= 2 || gapEpoch.duration() > 10.0) {
                dataGaps.add(new DataGap(gapEpoch));
            }
            for (int j = startSample; j <= lastSample; ++j) {
                double value = generator.nextGaussian();
                Param param = boundaryStats.getWeightedAvgParam(startSample, lastSample, j);
                data[j] = (int) (value * param.getStd() + param.getMean());
            }
        }
        if (hasGaps) {
            return new NamedIntWaveform(waveform.getKey(), waveform.getWfid(), data, start.getEpochTime(), samprate, waveform.getCalib(), waveform.getCalper(), dataGaps);
        } else {
            return waveform;
        }
    }

    public IntWaveform createRandomTrace(String sta, String chan, TimeT startTime, int npts, double rate) {
        Param pre = getLastEstimate(new StreamKey(sta, chan));

        int[] data = new int[npts];
        for (int j = 0; j < npts; ++j) {
            double value = generator.nextGaussian();
            data[j] = (int) (value * pre.getStd() + pre.getMean());
        }
        return new IntWaveform(1, startTime.getEpochTime(), rate, data);
    }

    private Param getLastEstimate(StreamKey key) {
        Param result = paramLookup.get(key);
        if (result == null) { // Have never computed statistics for this StaChan
            result = new Param(0.0, 10.0);
            paramLookup.put(key, result);
        }
        return result;
    }

    private static int getPreBufferStart(int k, ArrayList<PairT<Integer, Integer>> gapIndices, int idx1) {
        int idx0 = Math.max(Math.min(idx1 - MIN_BUFFER_LENGTH, idx1 - PREF_BUFFER_LENGTH), 0);
        if (k == 0) {
            return idx0;
        } else {
            int tmp = gapIndices.get(k - 1).getSecond(); // end index of previous gap.
            return Math.max(tmp, idx0);
        }
    }

    private int getPostBufferEnd(int k, int lastArrayIndex, ArrayList<PairT<Integer, Integer>> gapIndices, int idx2) {
        int idx3 = Math.min(Math.max(idx2 + MIN_BUFFER_LENGTH, idx2 + PREF_BUFFER_LENGTH), lastArrayIndex);
        if (k == gapIndices.size() - 1) {
            return idx3;
        } else {
            int tmp = gapIndices.get(k + 1).getFirst(); // start index of next gap.
            return Math.min(idx3, tmp);
        }
    }

    private Param computeParam(int idx0, int idx1, int[] data) {
        double mean = 0;
        double variance = 0;
        int windowLength = idx1 - idx0 - 1;
        for (int j = idx0; j < idx1; ++j) {
            mean += data[j];
        }

        mean /= windowLength;

        for (int j = idx0; j < idx1; ++j) {
            double v = data[j] - mean;
            variance += v * v;
        }
        variance /= (windowLength - 1);
        return new Param(mean, Math.sqrt(variance));
    }
}
