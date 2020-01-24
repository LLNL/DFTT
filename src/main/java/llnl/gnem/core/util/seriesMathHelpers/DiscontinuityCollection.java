/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util.seriesMathHelpers;

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
