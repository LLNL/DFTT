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
package llnl.gnem.dftt.core.waveform.classification;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Based on Matlab code by R. Moddemeijer Copyright (c) by R. Moddemeijer %
 * $Revision: 1.2 $ $Date: 2001/02/05 09:54:29 $
 */
public class Histogram {

    private final Descriptor descriptor;
    private final double lowerBound;
    private final double upperBound;
    private final int ncells;
    private final int[] cellCounts;
    private final int cellSum;

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public static class Descriptor {

        private final double lowerBound;
        private final double upperBound;
        private final int ncells;

        public Descriptor(double lowerBound, double upperBound, int ncells) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.ncells = ncells;
        }

        @Override
        public String toString() {
            return "Descriptor{" + "lowerBound=" + lowerBound + ", upperBound=" + upperBound + ", ncells=" + ncells + '}';
        }

        public double getLowerBound() {
            return lowerBound;
        }

        public double getUpperBound() {
            return upperBound;
        }

        public int getNcells() {
            return ncells;
        }

    }

    public static class Cell {

        private final double lowerBound;
        private final double upperBound;
        private final double center;

        @Override
        public String toString() {
            return "Cell{" + "lowerBound=" + lowerBound + ", upperBound=" + upperBound + ", center=" + center + '}';
        }

        public Cell(double lowBound, double center, double highBound) {
            lowerBound = lowBound;
            this.center = center;
            upperBound = highBound;
        }

        public double getLowerBound() {
            return lowerBound;
        }

        public double getUpperBound() {
            return upperBound;
        }

        public double getCenter() {
            return center;
        }
    }

    public static class CellValue {

        private final Cell cell;
        private final double value;

        public CellValue(Cell cell, double value) {
            this.cell = cell;
            this.value = value;
        }

        @Override
        public String toString() {
            return "CellValue{" + "cell=" + cell + ", value=" + value + '}';
        }

        public Cell getCell() {
            return cell;
        }

        public double getValue() {
            return value;
        }
    }

    public Histogram(Collection<Double> values, Descriptor desc) {
        this(values, desc.lowerBound, desc.upperBound, desc.ncells);
    }

    public Histogram(Collection<Double> values, double lower, double upper, int ncells) {
        lowerBound = lower;
        upperBound = upper;
        this.ncells = ncells;
        double dmin = lowerBound;
        double dmax = upperBound;
        double delta = (dmax - dmin) / (values.size() - 1);
        cellCounts = new int[ncells];
        dmin -= (delta / 2);
        dmax += (delta / 2);
        int count = 0;
        for (Double v : values) {
            long index = Math.round((v - dmin) / (dmax - dmin) * ncells - 0.5);
            if (index >= 0 && index < ncells) {
                cellCounts[(int) index] += 1;
                ++count;
            }
        }
        cellSum = count;
        descriptor = new Descriptor(lowerBound, upperBound, ncells);
    }

    public Histogram(Collection<Double> values) {
        double dmin = Double.MAX_VALUE;
        double dmax = -dmin;
        for (double v : values) {
            if (v < dmin) {
                dmin = v;
            }
            if (v > dmax) {
                dmax = v;
            }
        }
        double delta = (dmax - dmin) / (values.size() - 1);
        ncells = (int) Math.ceil(Math.sqrt(values.size()));
        cellCounts = new int[ncells];
        dmin -= (delta / 2);
        dmax += (delta / 2);
        lowerBound = dmin;
        upperBound = dmax;
        int count = 0;
        for (Double v : values) {
            long index = Math.round((v - dmin) / (dmax - dmin) * ncells - 0.5);
            if (index >= 0 && index < ncells) {
                cellCounts[(int) index] += 1;
                ++count;
            }
        }
        cellSum = count;
        descriptor = new Descriptor(lowerBound, upperBound, ncells);
    }

    public Collection<Integer> getCellCounts() {
        Collection<Integer> result = new ArrayList<>();
        for (int v : cellCounts) {
            result.add(v);
        }
        return result;
    }

    public Collection<CellValue> getValues(boolean asPdf) {
        double cellWidth = (upperBound - lowerBound) / (cellCounts.length - 1);
        Collection<CellValue> result = new ArrayList<>();
        for (int j = 0; j < cellCounts.length; ++j) {
            double min = lowerBound + cellWidth * j - cellWidth / 2;
            double max = min + cellWidth;
            double center = (max + min) / 2;
            Cell cell = new Cell(min, center, max);
            double v = cellCounts[j];
            if (asPdf) {
                v /= cellSum;
            }
            result.add(new CellValue(cell, v));
        }

        return result;
    }

    public Collection<CellValue> getValues() {
        return getValues(true);
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public int getNcells() {
        return ncells;
    }

    public int getCellSum() {
        return cellSum;
    }
}
