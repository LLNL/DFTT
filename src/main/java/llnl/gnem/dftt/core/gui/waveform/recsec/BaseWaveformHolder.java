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



import java.io.IOException;
import java.util.logging.Level;
import llnl.gnem.dftt.core.gui.map.origins.OriginInfo;
import llnl.gnem.dftt.core.waveform.components.BaseSingleComponent;
import llnl.gnem.dftt.core.traveltime.Ak135.TraveltimeCalculatorProducer;
import llnl.gnem.dftt.core.traveltime.Point3D;
import llnl.gnem.dftt.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.SeriesMath;

/**
 * User: Doug
 * Date: Dec 13, 2009
 * Time: 1:40:44 PM
 * COPYRIGHT NOTICE
 * Copyright (C) 2008 Doug Dodge.
 */
public abstract class BaseWaveformHolder implements WaveformHolder {
    protected final BaseSingleComponent component;
    protected final double meanValue;
    private double thisDataRange;
    private double maxDataRange;
    protected ScalingType scalingType;
    protected double magnification = 1;
    private final double referenceTime;

    public BaseWaveformHolder(double thisRange,
            BaseSingleComponent channelData,
            ScalingType scalingType,
            double meanValue,
            double maxRange,
            double referenceTime) {
        thisDataRange = thisRange;
        this.component = channelData;
        this.scalingType = scalingType;
        this.meanValue = meanValue;
        maxDataRange = maxRange;
        this.referenceTime = referenceTime;
    }

    @Override
    public void updateAmplitudeInformation(double maxPeakToPeak, double range) {
        maxDataRange = maxPeakToPeak;
        thisDataRange = range;
    }

    @Override
    public double getHeight() {
        return 20;     // height in millimeters.
    }

    @Override
    public double getCenter() {
        return meanValue;
    }

    @Override
    public void magnify() {
        magnification *= 2;
    }

    @Override
    public void reduce() {
        magnification /= 2;
    }

    @Override
    public abstract float[] getPlotArray();

    @Override
    public double getTime() {
        return component.getTraceData().getTime().getEpochTime() - referenceTime;
    }

    @Override
    public double getSamprate() {
        return component.getTraceData().getSampleRate();
    }

    @Override
    public BaseSingleComponent getChannelData() {
        return component;
    }

    @Override
    public void setScalingType(ScalingType scalingType) {
        this.scalingType = scalingType;
    }


    protected float[] getPlotArray(double scaleFactor2) {
        float[] data = component.getTraceData().getPlotData();
        SeriesMath.removeMean(data);
        if (thisDataRange > 0) {
            double scale = 1 / maxDataRange; // backup in case this trace is flatline.
            switch (scalingType) {
                case Fixed:
                    scale = 1 / thisDataRange * scaleFactor2;
                    break;
                case Relative:
                    scale = 1 / maxDataRange * scaleFactor2;
                    break;
            }
            SeriesMath.MultiplyScalar(data, scale * magnification);
        }
        SeriesMath.AddScalar(data, meanValue);

        return data;
    }

    @Override
    public double getDataRange() {
        return thisDataRange;
    }

    @Override
    public double getMaxDataRange() {
        return maxDataRange;
    }

    @Override
    public ScalingType getScalingType() {
        return scalingType;
    }


    @Override
    public double getTimeReduction(TimeReductionType timeReduction, OriginInfo origin) {
        if (origin == null) {
            return 0;
        } else {
            switch (timeReduction) {
                case None:
                    return 0;
                case Ptime: {
                try {
                    SinglePhaseTraveltimeCalculator calculator = TraveltimeCalculatorProducer.getInstance().getSinglePhaseTraveltimeCalculator("P");

                    Point3D pos = component.getPoint3D();
                    return calculator.getTT(origin.getPoint3D(), pos);// + origin.getTime();
                } catch (IOException ex) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
                }
                }
                default:
                    throw new IllegalArgumentException("Unsupported Reduction Type: " + timeReduction);
            }
        }
    }

}
