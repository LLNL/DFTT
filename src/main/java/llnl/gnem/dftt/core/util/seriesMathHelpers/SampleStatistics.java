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

import java.io.Serializable;
import java.util.Collection;
import llnl.gnem.dftt.core.util.NumericalList;
import llnl.gnem.dftt.core.util.NumericalList.DoubleList;
import llnl.gnem.dftt.core.util.NumericalList.FloatList;
import llnl.gnem.dftt.core.util.NumericalList.NumberList;
import llnl.gnem.dftt.core.util.SeriesMath;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author dodge1
 */
public class SampleStatistics implements Serializable {

    private static final long serialVersionUID = -441897415364308306L;

    private final double min;
    private final double max;
    private final double rms;
    private final double mean;
    private final double variance;
    private final double skewness;
    private final double kurtosis;
    private final double standardDeviation;
    private final double median;
    private final Order order;

    public static double computeMean(NumericalList data) {
        int npts = data.size();
        if (npts < 1) {
            throw new IllegalStateException("Collection is empty!");
        }
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < data.size(); i++) {
            stats.addValue(data.get(i));
        }
        return stats.getMean();
    }

    @Override
    public String toString() {
        switch (order) {
            case FIRST:
                return String.format("Mean = %f", mean);
            case SECOND:
                return String.format("Mean = %f, variance = %f", mean, variance);
            case THIRD:
                return String.format("Mean = %f, variance = %f, Skewness = %f", mean, variance, skewness);
            case FOURTH:
                return String.format("Mean = %f, variance = %f, Skewness = %f, Kurtosis = %f", mean, variance, skewness, kurtosis);
            default:
                return "";
        }
    }

    public String toCSV() {
        switch (order) {
            case FIRST:
                return String.format("%f", mean);
            case SECOND:
                return String.format("%f,%f", mean, variance);
            case THIRD:
                return String.format("%f,%f,%f", mean, variance, skewness);
            case FOURTH:
                return String.format("%f,%f,%f,%f", mean, variance, skewness, kurtosis);
            default:
                return "";
        }

    }

    public SampleStatistics(double[] data, Order order) {
        this(new DoubleList(data), order);
    }

    public SampleStatistics(double[] data) {
        this(new DoubleList(data));
    }

    public SampleStatistics(float[] data, Order order) {
        this(new FloatList(data), order);
    }

    public SampleStatistics(float[] data) {
        this(new FloatList(data));
    }

    public SampleStatistics(Double[] data, Order order) {
        this(new NumberList(data), order);
    }

    public SampleStatistics(Double[] data) {
        this(new NumberList(data));
    }

    public SampleStatistics(Float[] data, Order order) {
        this(new NumberList(data), order);
    }

    public SampleStatistics(Float[] data) {
        this(new NumberList(data));
    }

    public SampleStatistics(Collection<? extends Number> data) {
        this(data, Order.FOURTH);
    }

    public SampleStatistics(Collection<? extends Number> data, Order order) {
        this(new NumberList((Number[]) data.toArray(new Number[1])), order);
    }

    /**
     * @return the median
     */
    public double getMedian() {
        return median;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getRMS() {
        return rms;
    }

    public double getRange() {
        return max - min;
    }

    public MinMax getMinMax() {
        return new MinMax(min, max);
    }

    public static enum Order {

        FIRST, SECOND, THIRD, FOURTH
    }

    public SampleStatistics(NumericalList data) {
        this(data, Order.FOURTH);
    }

    public SampleStatistics(NumericalList data, Order order) {
        this.order = order;
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < data.size(); i++) {
            stats.addValue(data.get(i));
        }
        mean = stats.getMean();
        median = quickMedian(data);
        max = stats.getMax();
        min = stats.getMin();
        rms = stats.getStandardDeviation();
        if (order.ordinal() > Order.FIRST.ordinal()) {
            variance = stats.getVariance();
            standardDeviation = rms;
            if (order.ordinal() > Order.SECOND.ordinal()) {
                double tmp  = stats.getSkewness();
                skewness = Double.isNaN(tmp) ? 0 : tmp;
                if (order.ordinal() > Order.THIRD.ordinal()) {
                    tmp = stats.getKurtosis();
                    kurtosis = Double.isNaN(tmp) ? 0 : tmp;
                } else {
                    kurtosis = -Double.MAX_VALUE;
                }
            } else {
                skewness = -Double.MAX_VALUE;
                kurtosis = skewness;
            }
        } else {
            variance = -1;
            standardDeviation = -1;
            skewness = -Double.MAX_VALUE;
            kurtosis = skewness;
        }
    }

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return the variance
     */
    public double getVariance() {
        if (order.ordinal() > Order.FIRST.ordinal()) {
            return variance;
        } else {
            throw new IllegalStateException("Only first-order statistics available!");
        }
    }

    /**
     * @return the skewness
     */
    public double getSkewness() {
        if (order.ordinal() > Order.SECOND.ordinal()) {
            return skewness;
        } else {
            throw new IllegalStateException("No 3nd-order statistics available!");
        }
    }

    /**
     * @return the kurtosis
     */
    public double getKurtosis() {
        if (order.ordinal() > Order.THIRD.ordinal()) {
            return kurtosis;
        } else {
            throw new IllegalStateException("No 4th-order statistics available!");
        }
    }

    /**
     * @return the standardDeviation
     */
    public double getStandardDeviation() {
        if (order.ordinal() > Order.FIRST.ordinal()) {
            return standardDeviation;
        } else {
            throw new IllegalStateException("Only first-order statistics available!");
        }
    }

    public static double quickMedian(NumericalList values) {
        return SeriesMath.quickMedian(values.clone());
    }
}
