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
package llnl.gnem.apps.detection.statistics;

import llnl.gnem.dftt.core.optimization.NelderMead;
import llnl.gnem.dftt.core.signalprocessing.statistics.EMGDistribution;
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
