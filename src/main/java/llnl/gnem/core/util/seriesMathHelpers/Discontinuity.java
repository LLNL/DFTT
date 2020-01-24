/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util.seriesMathHelpers;

/**
 *
 * @author dodge1
 */
public class Discontinuity {

    private final int index;
    private final double time;
    private final double relativeDeviation;
    private final SampleStatistics statistics;

    public Discontinuity(int index, double time, double relativeDeviation, SampleStatistics statistics) {
        this.index = index;
        this.time = time;
        this.relativeDeviation = relativeDeviation;
        this.statistics = statistics;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @return the relativeDeviation
     */
    public double getRelativeDeviation() {
        return relativeDeviation;
    }
    
    public double getKurtosis()
    {
        return statistics.getKurtosis();
    }

    @Override
    public String toString() {
        return String.format("discontinuity at %d (%f) with relative deviation = %f, Kurtosis = %f",
                index, time, relativeDeviation, statistics.getKurtosis());
    }
}