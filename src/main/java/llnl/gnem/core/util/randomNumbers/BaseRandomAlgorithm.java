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
package llnl.gnem.core.util.randomNumbers;

/**
 *
 * @author dodge1
 */
public abstract class BaseRandomAlgorithm implements RandomAlgorithm{

    @Override
    public abstract double nextDouble();

    @Override
    public abstract int nextInt();

    @Override
    public abstract int nextInt(int n);

    @Override
    public abstract long nextLong();

    @Override
    public int getBoundedInt(int lower, int upper) {
        int outRange = upper - lower;
        long min = Integer.MIN_VALUE;
        long max = Integer.MAX_VALUE;
        long inRange = max - min;
        long value = this.nextInt();
        double numerator = (value - min) * outRange;
        long result = lower + Math.round(numerator / inRange);
        return (int) result;
    }

    @Override
    public double getBoundedDouble(double lower, double upper) {
        double outRange = upper - lower;
        long min = Integer.MIN_VALUE;
        long max = Integer.MAX_VALUE;
        long inRange = max - min;
        long value = this.nextInt();
        double numerator = (value - min) * outRange;
        return lower + numerator / inRange;
    }
    
        /**
     * Produce a normally-distributed deviate using Box-Muller transformation.
     * Adapted from Numerical Recipes P. 203
     *
     * @param mean
     * @param std
     * @return normal deviate
     */
    @Override
    public double nextGaussian(double mean, double std) {
        double r = Double.MAX_VALUE;
        double v1 = 0;
        while (r >= 1) {
            v1 = 2 * nextDouble() - 1;
            double v2 = 2 * nextDouble() - 1;
            r = v1 * v1 + v2 * v2;
        }
        double value = v1 * Math.sqrt(-2 * Math.log(r) / r);
        return value * std + mean;
    }

    
}
