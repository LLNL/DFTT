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
package llnl.gnem.core.correlation.clustering;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import llnl.gnem.core.correlation.CorrelationResults;
import llnl.gnem.core.correlation.CorrelationComponent;

/**
 * Created by dodge1 Date: Apr 7, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class AdHocClusterer {

    public ClusterResult cluster(CorrelationResults correlationData, 
            Collection<CorrelationComponent> matches, double threshold, boolean fixShiftsToZero) {
        Matrix correlations = correlationData.getCorrelations();
        Matrix shifts = correlationData.getShifts();

        Collection<GroupData> groups = new ArrayList<>();
        ArrayList<CorrelationComponent> ungrouped = new ArrayList<>(matches);

        int N = correlations.getRowDimension();

        while (N > 1) {
            int row = getLargestGroup(correlations, threshold);
            if (row >= 0) {
                int[] ii = findBestElements(correlations, row, threshold);
                Matrix group = correlations.getMatrix(ii, ii);
                Matrix groupShift = shifts.getMatrix(ii, ii);
                ArrayList<CorrelationComponent> associatedInfo = getAssociatedDetectionInfo(ungrouped, ii);
                if (ii.length > 1) {
                    groups.add(new GroupData(group, groupShift, associatedInfo, fixShiftsToZero));
                }
                ungrouped = getRemainderInfo(ungrouped, ii);


                correlations = getRemainderMatrix(correlations, ii);
                shifts = getRemainderMatrix(shifts, ii);
                N = correlations.getRowDimension();
            } else {
                break;
            }
        }



        return new ClusterResult(groups);
    }

    private ArrayList<CorrelationComponent> getRemainderInfo(ArrayList<CorrelationComponent> info, int[] ii) {
        Set<Integer> used = new HashSet<>();
        for (int i : ii) {
            used.add(i);
        }
        ArrayList<CorrelationComponent> result = new ArrayList<>();
        for (int j = 0; j < info.size(); ++j) {
            if (!used.contains(j)) {
                result.add(info.get(j));
            }

        }
        return result;
    }

    private ArrayList<CorrelationComponent> getAssociatedDetectionInfo(ArrayList<CorrelationComponent> info, int[] ii) {
        ArrayList<CorrelationComponent> result = new ArrayList<>();
        for (int j : ii) {
            result.add(info.get(j));
        }
        return result;
    }

    private Matrix getRemainderMatrix(Matrix correlations, int[] ii) {
        int N = correlations.getRowDimension();
        Set<Integer> used = new HashSet<>();
        for (int i : ii) {
            used.add(i);
        }
        int[] required = new int[N - ii.length];
        int m = 0;
        for (int j = 0; j < N; ++j) {
            if (!used.contains(j)) {
                required[m++] = j;
            }
        }
        return correlations.getMatrix(required, required);
    }

    private int[] findBestElements(Matrix correlations, int row, double threshold) {
        int N = correlations.getRowDimension();
        ArrayList<Integer> aresult = new ArrayList<>();
        for (int j = 0; j < N; ++j) {
            if (correlations.get(row, j) >= threshold) {
                aresult.add(j);
            }
        }
        int[] result = new int[aresult.size()];
        for (int j = 0; j < aresult.size(); ++j) {
            result[j] = aresult.get(j);
        }
        return result;
    }

    private int getLargestGroup(Matrix correlations, double threshold) {
        int N = correlations.getRowDimension();
        int row = -1;
        int maxNum = 0;
        for (int j = 0; j < N - 1; ++j) {
            int num = 0;
            for (int k = j + 1; k < N; ++k) {
                double v = correlations.get(j, k);
                if (v >= threshold) {
                    ++num;
                }
            }
            if (num > maxNum) {
                maxNum = num;
                row = j;
            }
        }
        return row;
    }
}
