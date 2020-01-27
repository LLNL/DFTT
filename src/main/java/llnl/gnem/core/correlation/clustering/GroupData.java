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
import llnl.gnem.core.correlation.RealSequenceCorrelator;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.util.ShiftType;

/**
 * Created by dodge1 Date: Mar 23, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GroupData {

    private final Matrix correlations;
    private final Matrix shifts;
    private final ArrayList<CorrelationComponent> components;
    private final Matrix lsShifts;
    private final Matrix stdMatrix;

    GroupData(Matrix correlations, Matrix shifts, ArrayList<CorrelationComponent> components, boolean fixToZeroShifts) {
        this.correlations = correlations;
        this.shifts = shifts;
        this.components = new ArrayList<>(components);
        sortData();
        convertShiftsToSeconds();
        RealSequenceCorrelator correlator = new RealSequenceCorrelator();
        lsShifts = correlator.getAdjustmentVector(this.shifts.getColumnDimension(), 
                this.shifts, this.correlations, ShiftType.LEAST_SQUARES, fixToZeroShifts, null);

        stdMatrix = correlator.getSigmaMatrix(shifts, lsShifts);






        for (int j = 0; j < this.components.size(); ++j) {
            CorrelationComponent cc = this.components.get(j);
            double correlation = this.correlations.get(0, j);

            double lsShift = lsShifts.get(j, 0);
            cc.setCorrelation(correlation);
            cc.setShift(-lsShift);
            cc.setStd(stdMatrix.get(j,0));
        }
    }

    private void sortData() {
        int numCols = correlations.getColumnDimension();
        for (int j = 0; j < numCols - 1; ++j) {
            int next = j + 1;
            int best = getNextHighestValue(j);
            if (best > next) {
                switchColsAndRows(correlations, next, best);
                switchColsAndRows(shifts, next, best);
                CorrelationComponent nextComp = components.get(next);
                CorrelationComponent bestComp = components.get(best);
                components.set(next, bestComp);
                components.set(best, nextComp);
            }
        }
    }

    public Matrix getCorrelations() {
        return correlations;
    }

    public Matrix getShifts() {
        return shifts;
    }

    public ArrayList<CorrelationComponent> getAssociatedInfo() {
        return new ArrayList<>(components);
    }

    private int getNextHighestValue(int j) {
        int result = -1;
        double maxValue = -Double.MAX_VALUE;
        for (int m = j + 1; m < correlations.getColumnDimension(); ++m) {
            double value = correlations.get(0, m);
            if (value > maxValue) {
                maxValue = value;
                result = m;
            }
        }
        return result;
    }

    private void switchColsAndRows(Matrix P, int next, int best) {
        int n = P.getColumnDimension() - 1;
        Matrix nextCol = P.getMatrix(0, n, next, next);
        Matrix bestCol = P.getMatrix(0, n, best, best);
        P.setMatrix(0, n, next, next, bestCol);
        P.setMatrix(0, n, best, best, nextCol);
        Matrix nextRow = P.getMatrix(next, next, 0, n);
        Matrix bestRow = P.getMatrix(best, best, 0, n);
        P.setMatrix(next, next, 0, n, bestRow);
        P.setMatrix(best, best, 0, n, nextRow);
    }

    int size() {
        return components.size();
    }

    private void convertShiftsToSeconds() {
        for( int j = 0; j < shifts.getRowDimension(); ++j){
            double sampleRate = components.get(j).getSeismogram().getSamprate();
            for( int k = 0; k < shifts.getColumnDimension(); ++k ){
                double v = shifts.get(j,k);
                shifts.set(j,k,v / sampleRate);
            }
        }
    }
}
