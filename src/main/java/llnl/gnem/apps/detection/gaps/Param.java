/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.gaps;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class Param {

    @Override
    public String toString() {
        return "Param{" + "mean=" + mean + ", std=" + std + '}';
    }

    private final double mean;
    private final double std;

    public Param(double mean, double std) {
        this.mean = mean;
        this.std = std;
    }

    public double getMean() {
        return mean;
    }

    public double getStd() {
        return std;
    }
}
