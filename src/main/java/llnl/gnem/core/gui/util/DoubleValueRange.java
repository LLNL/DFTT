/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.util;

import java.io.Serializable;

/**
 *
 * @author dodge1
 */
public class DoubleValueRange implements Serializable {

    private final double min;
    private final double max;
    private double value;
    static final long serialVersionUID = -3688999827184039402L;

    public DoubleValueRange(double min, double value, double max) {
        this.min = min;
        this.value = value;
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

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     */
    public void setValue(double newValue) {
        if (newValue < min) {
            value = min;
        } else if (value > max) {
            value = max;

        } else {
            value = newValue;
        }
    }
}
