/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.util.seriesMathHelpers;

/**
 *
 * @author dodge1
 */
public class MinMax {
    private final double min;
    private final double max;

    public MinMax(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @return the max
     */
    public double getMax() {
        return max;
    }

    public double getRange() {
        return max-min;
    }
}
