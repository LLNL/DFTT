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
package llnl.gnem.dftt.core.gui.waveform.recsec;

import java.util.Collection;
import java.util.ArrayList;
import llnl.gnem.dftt.core.gui.map.origins.OriginInfo;
import llnl.gnem.dftt.core.waveform.components.BaseSingleComponent;
import llnl.gnem.dftt.core.waveform.DistanceType;
import llnl.gnem.dftt.core.util.SeriesMath;

/**
 * User: Doug Date: Sep 26, 2009 Time: 3:14:36 PM
 * COPYRIGHT NOTICE Copyright (C) 2008 Doug Dodge.
 */
public class WaveformHolderManager {
    private static DistanceType distanceType = DistanceType.km;

    public static Collection<WaveformHolder> getWaveformHolders(OriginInfo origin,
            Collection<BaseSingleComponent> channels,
            ScalingType scalingType) {
        Collection<WaveformHolder> result = new ArrayList<WaveformHolder>();
        double maxPeakToPeak = getMaxPeakToPeak(channels);

        double referenceTime = origin.getTime();
        if (DistanceRenderPolicyPrefs.getInstance().getPolicy() == DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE) {
            double traceAmpToDistFactor = getTraceAmpToDistFactor(origin, channels);

            for (BaseSingleComponent bsc : channels) {
                float[] data = bsc.getTraceData().getPlotData();
                double val = SeriesMath.getWindowPeakToPeak(data, 0, data.length);
                double delta = origin.getPoint3D().dist_geog(bsc.getPoint3D());
                HasOriginWaveformHolder holder = new HasOriginWaveformHolder(bsc,
                        delta,
                        val,
                        maxPeakToPeak,
                        scalingType,
                        traceAmpToDistFactor / distanceType.getScaleFactor(),
                        referenceTime);
                result.add(holder);
            }
        } else {

            int j = 0;
            for (BaseSingleComponent bsc : channels) {
                float[] data = bsc.getTraceData().getPlotData();
                double val = SeriesMath.getWindowPeakToPeak(data, 0, data.length);
                NoOriginWaveformHolder holder = new NoOriginWaveformHolder(bsc, j + .5, val, maxPeakToPeak, scalingType, referenceTime);
                result.add(holder);
                ++j;

            }
        }

        return result;
    }

    public static double getTraceAmpToDistFactor(OriginInfo origin, Collection<BaseSingleComponent> channels) {
        double maxDist = 1;
        for (BaseSingleComponent bsc : channels) {
            double dist = origin.getPoint3D().dist_geog(bsc.getPoint3D());
            if (dist > maxDist) {
                maxDist = dist;
            }
        }
        return maxDist / Math.max(channels.size(), 1);
    }

    public static void renormalizeHolders(Collection<WaveformHolder> holders) {
        double maxPeakToPeak = getUpdatedMaxPeakToPeak(holders);
        for (WaveformHolder holder : holders) {
            BaseSingleComponent bsc = holder.getChannelData();
            float[] data = bsc.getTraceData().getPlotData();
            double val = SeriesMath.getWindowPeakToPeak(data, 0, data.length);
            holder.updateAmplitudeInformation(maxPeakToPeak, val);
        }
    }

    private static double getUpdatedMaxPeakToPeak(Collection<WaveformHolder> holders) {
        double result = -Double.MAX_VALUE;
        for (WaveformHolder holder : holders) {
            BaseSingleComponent bsc = holder.getChannelData();
            float[] data = bsc.getTraceData().getPlotData();
            double val = SeriesMath.getWindowPeakToPeak(data, 0, data.length);
            if (val > result) {
                result = val;
            }
        }
        return result;
    }

    private static double getMaxPeakToPeak(Collection<BaseSingleComponent> channels) {

        double result = -Double.MAX_VALUE;
        for (BaseSingleComponent bsc : channels) {
            float[] data = bsc.getTraceData().getPlotData();
            double val = SeriesMath.getWindowPeakToPeak(data, 0, data.length);
            if (val > result) {
                result = val;
            }
        }
        return result;
    }

    static void setDistanceType(DistanceType type) {
        distanceType = type;
    }
}
