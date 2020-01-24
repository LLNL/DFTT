/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.statistics;

import llnl.gnem.core.optimization.ObjectiveFunction;
import org.apache.commons.math3.distribution.BetaDistribution;

/**
 *
 * @author dodge1
 */
public class BetaObjectiveFunction implements ObjectiveFunction {

    private final float[] bins;
    private final float[] values;

    public BetaObjectiveFunction(float[] bins, float[] values) {
        this.bins = bins.clone();
        this.values = values.clone();
    }

    @Override
    public int dimension() {
        return 2;
    }

    @Override
    public double evaluate(float[] p) {
        BetaDistribution dist = new BetaDistribution(p[0], p[1] );
        if (p[0] <= 0 || p[1] <= 0) {
            return 9999.9;
        }
        float[] est = getDensity(dist, bins);
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

    private float[] getDensity(BetaDistribution dist, float[] bins) {
        float[] result = new float[bins.length];
        for(int j = 0; j < bins.length; ++j){
            double v = dist.density(bins[j]);
            result[j] = (float)v;
        }
        return result;
    }

}
