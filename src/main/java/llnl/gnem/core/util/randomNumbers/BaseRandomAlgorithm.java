/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
