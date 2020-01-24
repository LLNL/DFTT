/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.statistics;

import llnl.gnem.core.optimization.NelderMead;
import llnl.gnem.core.signalprocessing.statistics.EMGDistribution;
import org.apache.commons.math3.distribution.BetaDistribution;

/**
 *
 * @author dodge1
 */
public class DistributionFitter {

    private final static double FALSE_ALARM_RATE = 0.00000001;

    public static EMGDistribution getEMGDistribution(HistogramData histogram) {
        float[] bins = histogram.getBins();
        float[] values = histogram.getValues();

        float[] pre = {.05f, .02f, 33f};
        NelderMead nm = new NelderMead(new EMGObjectiveFunction(bins, values),
                0.0000001,
                2000);
        nm.initialize(pre, .01f);
        nm.run();
        float[] post = nm.getResult().getParameters();
        return new EMGDistribution(post[0], post[1], post[2]);

    }

    public static BetaDistribution getBetaDistribution(HistogramData histogram) {
        float[] bins = histogram.getBins();
        float[] values = histogram.getValues();

        float[] pre = {(float)4.0, (float)50.0};
        NelderMead nm = new NelderMead(new BetaObjectiveFunction(bins, values),
                0.00000001,
                2000);
        nm.initialize(pre, .01f);
        nm.run();
        float[] post = nm.getResult().getParameters();
        return new BetaDistribution(post[0], post[1]);
    }

    public static double getFixedFalseAlarmRate() {
        return FALSE_ALARM_RATE;
    }

}
