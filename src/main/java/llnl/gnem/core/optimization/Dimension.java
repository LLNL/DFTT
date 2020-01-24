/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.optimization;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class Dimension {

    private final double minimum;
    private final double maximum;

    public Dimension(double minimum,
            double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public double getLengthScale() {
        return getRange() / 2;
    }

    private double getRange() {
        return maximum - minimum;
    }

    public boolean contains(double value) {
        return value >= minimum && value <= maximum;
    }

    public double nearestBoundary(double value) {
        if (value > maximum) {
            return maximum;
        } else {
            return minimum;
        }
    }
}
