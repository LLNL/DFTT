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
package llnl.gnem.dftt.core.correlation.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.ojalgo.matrix.Primitive32Matrix;

import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.correlation.CorrelationResults;
import llnl.gnem.dftt.core.dataAccess.dataObjects.ProgressMonitor;

public class AdHocClusterer {

    private ClusterType clusterType = ClusterType.CORRELATION_THRESHOLD;
    private int numGroups = 10;

    public AdHocClusterer(ClusterType clusterType, int numGroups) {
        this.clusterType = clusterType;
        this.numGroups = numGroups;
    }

    public ClusterResult cluster(CorrelationResults correlationData,
            Collection<CorrelationComponent> matches,
            double threshold,
            boolean fixShiftsToZero,
            ProgressMonitor monitor) {
        Primitive32Matrix correlations = correlationData.getCorrelations();
        Primitive32Matrix shifts = correlationData.getShifts();

        Collection<GroupData> groups = new ArrayList<>();
        List<CorrelationComponent> ungrouped = new ArrayList<>(matches);

        int N = correlations.getRowDim();
        int totalRows = N;
        int rowsProcessed = 0;
        if (monitor != null) {
            monitor.setRange(0, totalRows);
        }
        while (N > 0) {
            String msg = String.format("N=%d, Getting largest group...", N);
            if (monitor != null) {
                monitor.setText(msg);
            }
            int row = getLargestGroup(correlations, threshold);
            if (row >= 0) {
                long[] ii = clusterType == ClusterType.CORRELATION_THRESHOLD ? findBestElements(correlations, row, threshold) : findBestElements(correlations, row, numGroups);
                msg = String.format("N=%d, Group size = %d", N, ii.length);
                if (monitor != null) {
                    monitor.setText(msg);
                }
                Primitive32Matrix group = correlations.select(ii, ii);
                Primitive32Matrix groupShift = shifts.select(ii, ii);
                List<CorrelationComponent> associatedInfo = getAssociatedDetectionInfo(ungrouped, ii);
                if (ii.length > 0) {
                    GroupData gd = new GroupData(group, groupShift, associatedInfo, fixShiftsToZero);
                    groups.add(gd);
                }
                ungrouped = getRemainderInfo(ungrouped, ii);

                if (ii.length < N) {
                    correlations = getRemainderMatrix(correlations, ii);
                    shifts = getRemainderMatrix(shifts, ii);
                    N = correlations.getRowDim();
                    rowsProcessed += ii.length;
                    if (monitor != null) {
                        monitor.setValue(rowsProcessed);
                    }
                } else {
                    N = 0;
                }
            } else {
                break;
            }
        }
        if (N > 0) {
            long[] ii = new long[N];
            for (int j = 0; j < N; ++j) {
                ii[j] = j;
            }
            Primitive32Matrix group = correlations.select(ii, ii);
            Primitive32Matrix groupShift = shifts.select(ii, ii);
            List<CorrelationComponent> associatedInfo = getAssociatedDetectionInfo(ungrouped, ii);
            groups.add(new GroupData(group, groupShift, associatedInfo, fixShiftsToZero, true));
        }
        rowsProcessed += N;
        if (monitor != null) {
            monitor.setText("Clustering complete.");
            monitor.setValue(rowsProcessed);
        }
        return new ClusterResult(groups);
    }

    private List<CorrelationComponent> getRemainderInfo(List<CorrelationComponent> info, long[] ii) {
        Set<Long> used = new HashSet<>();
        for (long i : ii) {
            used.add(i);
        }
        List<CorrelationComponent> result = new ArrayList<>();
        for (long j = 0; j < info.size(); ++j) {
            if (!used.contains(j)) {
                result.add(info.get((int) j));
            }

        }
        return result;
    }

    private List<CorrelationComponent> getAssociatedDetectionInfo(List<CorrelationComponent> ungrouped, long[] ii) {
        List<CorrelationComponent> result = new ArrayList<>();
        for (long j : ii) {
            result.add(ungrouped.get((int) j));
        }
        return result;
    }

    private Primitive32Matrix getRemainderMatrix(Primitive32Matrix correlations, long[] ii) {
        int N = correlations.getRowDim();
        Set<Long> used = new HashSet<>();
        for (long i : ii) {
            used.add(i);
        }
        long[] required = new long[N - ii.length];
        int m = 0;
        for (long j = 0; j < N; ++j) {
            if (!used.contains(j)) {
                required[m++] = j;
            }
        }
        return correlations.select(required, required);
    }

    private long[] findBestElements(Primitive32Matrix correlations, int row, double threshold) {
        int N = correlations.getRowDim();
        List<Integer> aresult = new ArrayList<>();
        for (int j = 0; j < N; ++j) {
            if (correlations.get(row, j) >= threshold) {
                aresult.add(j);
            }
        }
        long[] result = new long[aresult.size()];
        for (int j = 0; j < aresult.size(); ++j) {
            result[j] = aresult.get(j);
        }
        return result;
    }

    private long[] findBestElements(Primitive32Matrix correlations, int row, int elementsPerGroup) {
        int N = correlations.getRowDim();
        List<Integer> aresult = new ArrayList<>();

        TreeMap<Double, Collection<Integer>> correlationToIndexMap = new TreeMap<>();
        for (int j = 0; j < N; ++j) {
            double cc = correlations.get(row, j);
            Collection<Integer> tmp = correlationToIndexMap.get(cc);
            if (tmp == null) {
                tmp = new ArrayList<>();
                correlationToIndexMap.put(cc, tmp);
            }
            tmp.add(j);
        }
        for (Double v : correlationToIndexMap.descendingKeySet()) {
            Collection<Integer> tmp = correlationToIndexMap.get(v);
            for (int j : tmp) {
                aresult.add(j);
            }

            if (aresult.size() >= elementsPerGroup) {
                break;
            }
        }
        long[] result = new long[aresult.size()];
        for (int j = 0; j < aresult.size(); ++j) {
            result[j] = aresult.get(j);
        }
        return result;
    }

    private static int getLargestGroup(Primitive32Matrix correlations, double threshold) {

        int N = correlations.getRowDim();
        int row = -1;
        int maxNum = 0;
        Map<Integer, Integer> rowCount = new ConcurrentHashMap<>();
        IntStream.range(0, N - 1).parallel().forEach(c -> getGroupSize(c, N, correlations, threshold, rowCount));
        for (Integer j : rowCount.keySet()) {
            int num = rowCount.get(j);
            if (num > maxNum) {
                maxNum = num;
                row = j;
            }
        }
        return row;
    }

    private static void getGroupSize(int j, int N, Primitive32Matrix correlations, double threshold,
            Map<Integer, Integer> foo) {
        int num = 0;
        for (int k = j + 1; k < N; ++k) {
            double v = correlations.get(j, k);
            if (v >= threshold) {
                ++num;
            }
        }
        foo.put(j, num);
    }

}
