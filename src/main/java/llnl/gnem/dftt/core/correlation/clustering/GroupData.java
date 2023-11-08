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
import java.util.List;

import org.ojalgo.matrix.Primitive32Matrix;
import org.ojalgo.matrix.Primitive32Matrix.DenseReceiver;

import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.correlation.RealSequenceCorrelator;
import llnl.gnem.dftt.core.correlation.util.ShiftType;

/**
 * Created by dodge1 Date: Mar 23, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GroupData {

    private Primitive32Matrix correlations;
    private Primitive32Matrix shifts;
    private final ArrayList<CorrelationComponent> components;
    private final Primitive32Matrix lsShifts;
    private final Primitive32Matrix stdMatrix;
    private final boolean residualGroup;

    GroupData(Primitive32Matrix correlations, Primitive32Matrix shifts, List<CorrelationComponent> associatedInfo, boolean fixToZeroShifts, boolean residualGroup) {
        this.residualGroup = residualGroup;
        if (residualGroup) {
            fixToZeroShifts = true;
        }
        this.correlations = correlations;
        this.shifts = shifts;
        this.components = new ArrayList<>(associatedInfo);
        sortData();
        convertShiftsToSeconds();
        RealSequenceCorrelator correlator = new RealSequenceCorrelator();
        lsShifts = correlator.getAdjustmentVector(this.shifts.getColDim(), this.shifts, this.correlations, ShiftType.LEAST_SQUARES, fixToZeroShifts, null);

        stdMatrix = correlator.getSigmaMatrix(shifts, lsShifts);

        for (int j = 0; j < this.components.size(); ++j) {
            CorrelationComponent cc = this.components.get(j);
            double correlation = this.correlations.get(0, j);

            double lsShift = lsShifts.get(j, 0);
            cc.setCorrelation(correlation);
            cc.setShift(-lsShift);
            cc.setStd(stdMatrix.get(j, 0));
        }
    }

    GroupData(Primitive32Matrix correlations, Primitive32Matrix shifts, List<CorrelationComponent> components, boolean fixToZeroShifts) {
        this.residualGroup = false;
        this.correlations = correlations;
        this.shifts = shifts;
        this.components = new ArrayList<>(components);
        sortData();
        convertShiftsToSeconds();
        RealSequenceCorrelator correlator = new RealSequenceCorrelator();
        lsShifts = correlator.getAdjustmentVector(this.shifts.getColDim(), this.shifts, this.correlations, ShiftType.LEAST_SQUARES, fixToZeroShifts, null);

        stdMatrix = correlator.getSigmaMatrix(shifts, lsShifts);

        for (int j = 0; j < this.components.size(); ++j) {
            CorrelationComponent cc = this.components.get(j);
            double correlation = this.correlations.get(0, j);

            double lsShift = lsShifts.get(j, 0);
            cc.setCorrelation(correlation);
            cc.setShift(-lsShift);
            cc.setStd(stdMatrix.get(j, 0));
        }
    }

    public double getAverageCorrelation() {
        double sum = 0;
        for (int j = 1; j < components.size(); ++j) {
            double correlation = correlations.get(0, j);
            sum += correlation;
        }
        return sum / (components.size() - 1);
    }

    private void sortData() {
        int numCols = correlations.getColDim();
        for (int j = 0; j < numCols - 1; ++j) {
            int next = j + 1;
            int best = getNextHighestValue(j);
            if (best > next) {
                correlations = switchColsAndRows(correlations, next, best);
                shifts = switchColsAndRows(shifts, next, best);
                CorrelationComponent nextComp = components.get(next);
                CorrelationComponent bestComp = components.get(best);
                components.set(next, bestComp);
                components.set(best, nextComp);
            }
        }
    }

    public boolean isResidualGroup() {
        return residualGroup;
    }

    public Primitive32Matrix getCorrelations() {
        return correlations;
    }

    public Primitive32Matrix getShifts() {
        return shifts;
    }

    public ArrayList<CorrelationComponent> getAssociatedInfo() {
        return new ArrayList<>(components);
    }

    private int getNextHighestValue(int j) {
        int result = -1;
        double maxValue = -Double.MAX_VALUE;
        for (int m = j + 1; m < correlations.getColDim(); ++m) {
            double value = correlations.get(0, m);
            if (value > maxValue) {
                maxValue = value;
                result = m;
            }
        }
        return result;
    }

    private Primitive32Matrix switchColsAndRows(Primitive32Matrix P, int next, int best) {
        DenseReceiver Pa = P.copy();
        Pa.exchangeColumns(best, next);
        Pa.exchangeRows(best, next);
        return Pa.get();
    }

    int size() {
        return components.size();
    }

    private void convertShiftsToSeconds() {
        DenseReceiver S = shifts.copy();
        for (int j = 0; j < shifts.getRowDim(); ++j) {
            double sampleRate = components.get(j).getSeismogram().getSamprate();
            for (int k = 0; k < shifts.getColDim(); ++k) {
                double v = shifts.get(j, k);
                S.set(j, k, v / sampleRate);
            }
        }
        shifts = S.get();
    }

    public void maybeRemoveCorrelation(CorrelationComponent cc) {
        components.remove(cc);
    }
}
