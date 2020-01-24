/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.statistics;

import llnl.gnem.core.optimization.ObjectiveFunction;
import llnl.gnem.core.signalprocessing.statistics.EMGDistribution;

/**
 *
 * @author dodge1
 */
public class EMGObjectiveFunction implements ObjectiveFunction {

    private final float[] bins;
    private final float[] values;

    public EMGObjectiveFunction(float[] bins, float[] values) {
        this.bins = bins.clone();
        this.values = values.clone();
    }

    @Override
    public int dimension() {
        return 3;
    }

    @Override
    public double evaluate(float[] p) {
        EMGDistribution dist = new EMGDistribution(p[0], p[1], p[2]);
        if (p[0] <= 0 || p[1] <= 0 || p[2] <= 0) {
            return 9999.9;
        }
        float[] est = dist.getDensity(bins);
        float sum = 0;
        for (int j = 0; j < est.length; ++j) {
            float res = est[j] - values[j];
            sum += res * res;
        }
        if (Float.isNaN(sum)) {
            return 9999.9;
        } else {
            return Math.sqrt(sum) / est.length;
        }
    }

    @Override
    public float[] gradient(float[] parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
