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
package llnl.gnem.dftt.core.util.seriesMathHelpers;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author dodge1
 */
public class DiscontinuityCollection {

    Map<Integer, Discontinuity> items;
    private int numItems;
    private double averageDeviation;
    private double maxDeviation;
    private double averageKurtosis;
    private double maxKurtosis;
    private boolean statsCalculated = false;

    public DiscontinuityCollection() {
        items = new TreeMap<Integer, Discontinuity>();
    }

    public DiscontinuityCollection(Discontinuity item) {
        items = new TreeMap<Integer, Discontinuity>();
        items.put(item.getIndex(), item);
    }

    public void add(Discontinuity item) {
        items.put(item.getIndex(), item);
    }

    public int size() {
        return items.size();
    }

    @Override
    public String toString() {
        if (!statsCalculated) {
            computeStatistics();
        }
        return String.format("%d discontinuities with average deviation of %f", numItems, averageDeviation);
    }

    public void computeStatistics() {
        numItems = items.size();
        averageDeviation = 0;
        maxDeviation = 0;
        averageKurtosis = 0;
        maxKurtosis = 0;
        if (numItems > 0) {
            for (Discontinuity d : items.values()) {
                averageDeviation += d.getRelativeDeviation();
                if (d.getRelativeDeviation() > maxDeviation) {
                    maxDeviation = d.getRelativeDeviation();
                }
                averageKurtosis += d.getKurtosis();
                if (d.getKurtosis() > maxKurtosis) {
                    maxKurtosis = d.getKurtosis();
                }
            }
            averageDeviation /= numItems;
            averageKurtosis /= numItems;
        }
    }

    /**
     * @return the numItems
     */
    public int getNumItems() {
        if (!statsCalculated) {
            computeStatistics();
        }
        return numItems;
    }

    /**
     * @return the maxDeviation
     */
    public double getMaxDeviation() {
        if (!statsCalculated) {
            computeStatistics();
        }
        return maxDeviation;
    }

    /**
     * @return the averageKurtosis
     */
    public double getAverageKurtosis() {
        if (!statsCalculated) {
            computeStatistics();
        }
        return averageKurtosis;
    }

    /**
     * @return the maxKurtosis
     */
    public double getMaxKurtosis() {
        if (!statsCalculated) {
            computeStatistics();
        }
        return maxKurtosis;
    }

    /**
     * @return the averageDeviation
     */
    public double getAverageDeviation() {
        if (!statsCalculated) {
            computeStatistics();
        }
        return averageDeviation;
    }
}
