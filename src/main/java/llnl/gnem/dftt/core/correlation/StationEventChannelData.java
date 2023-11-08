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
package llnl.gnem.dftt.core.correlation;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.dftt.core.waveform.qc.FilteredFeatures;
import llnl.gnem.dftt.core.correlation.util.EventStaInfo;
import llnl.gnem.dftt.core.correlation.util.NominalArrival;
import llnl.gnem.dftt.core.correlation.util.PhaseWindow;
import llnl.gnem.dftt.core.util.BandInfo;
import llnl.gnem.dftt.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class StationEventChannelData implements Serializable, Comparable<StationEventChannelData> {

    private static final long serialVersionUID = 1L;
    private final CssSeismogram seismogram;
    private final SeismogramData data;
    private final FilteredFeatures features;
    private double cumulativeAdjustment;
    private double sigmaInSamples;
    private float[] transform;
    private float maxCC;
    private final BandInfo bandInfo;
    private final PhaseArrivalWindow window;

    public StationEventChannelData(
            SeismogramData data, BandInfo bandInfo, FilteredFeatures features, PhaseArrivalWindow window) {
        this.data = data;
        this.features = features;
        this.bandInfo = bandInfo;
        this.window = window;

        seismogram = data.getSeismogram();
        cumulativeAdjustment = 0;
    }


    public StationEventChannelData(CorrelationComponent comp) {
        EventStaInfo evidWfidDelta = new EventStaInfo(comp.getEvent().getEvid(), comp.getWfid(), comp.getDegDist(), 0.0, 0.0);
        NominalArrival arrival = ((CorrelationTraceData) comp.getCorrelationTraceData()).getNominalPick();
        double nominalWindowLength = 10.0;
        Collection<BandInfo> bands = new ArrayList<>();
        PhaseWindow phaseWindow = new PhaseWindow(-1,arrival.getPhase(), nominalWindowLength, 0,0,0,0,0,bands);
        window = new PhaseArrivalWindow(  phaseWindow , arrival.getTime(), phaseWindow.getNominalWindowLength());


        data = new SeismogramData(comp.getSeismogram(), window, evidWfidDelta);
        features = new FilteredFeatures(comp.getSeismogram(), comp.getSeismogram(), arrival.getTime(),phaseWindow.getNominalWindowLength());
        CssSeismogram tmpSeis = new CssSeismogram(comp.getSeismogram());
        tmpSeis.RemoveMean();
        tmpSeis.triangleTaper(5.0);
        this.seismogram = tmpSeis;
        cumulativeAdjustment = 0;
        bandInfo = null;
    }

    @Override
    public String toString() {
        String sb = String.format("Sta: %s, Chan: %s, Evid: %d, Arrival: %s",
                getSta(), getChan(), getEvid(), data.getWindow().toString());

        return sb;
    }

    /**
     * @return the station code.
     */
    public String getSta() {
        return seismogram.getSta();
    }

    public String getChan() {
        return seismogram.getChan();
    }

    /**
     * @return the evid
     */
    public long getEvid() {
        return data.getEvidWfidDelta().getEvid();
    }

    public String getPhase() {
        return data.getWindow().getPhase();
    }

    /**
     * @return the channels
     */
    public CssSeismogram getSeismogram() {
        return seismogram;
    }

    /**
     * @return the nominalPickTime
     */
    public double getNominalPickTime() {
        return data.getWindow().getTime();
    }

    /**
     * @return the cumulativeAdjustment
     */
    public double getCumulativeAdjustment() {
        return cumulativeAdjustment;
    }

    public double getSampleRate() {
        return seismogram.getSamprate();
    }

    public void addTransform(float[] xx) {
        transform = xx;
    }

    public void addCCMax(float acMax) {
        maxCC = acMax;
    }

    public float getMaxCC() {
        return maxCC;
    }

    public float getMaxCC(CssSeismogram seis) {
        return maxCC;
    }

    public float[] getTransform() {
        return transform;
    }

    public void addShift(double shift) {
        cumulativeAdjustment += shift;
    }

    public void outputResults() {
        double timeAdjustment = cumulativeAdjustment * seismogram.getDelta();
        double sigma = sigmaInSamples * seismogram.getDelta();
        System.out.println(String.format("Sta: %s, Evid: %d, Phase: %s, NominalPickTime: %f, Adjustment: %f, Sigma: %f",
                getSta(), getEvid(), data.getWindow().getPhase(), data.getWindow().getTime(), timeAdjustment, sigma));
    }

    public void interpolateTo(double newRate) {
        seismogram.interpolate(newRate);
    }

    public void addSigma(double sigma) {
        sigmaInSamples = sigma;
    }

    public double getAdjustStd() {
        return sigmaInSamples;
    }

    public double getDelta() {
        return data.getEvidWfidDelta().getDelta();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof StationEventChannelData) {
            StationEventChannelData other = (StationEventChannelData) o;
            if (compareTo(other) == 0) {
                return true;
            }
        }
        return false;
    }

    public NominalArrival getNominalPick()
    {
        return new NominalArrival(data.getWindow().getPhase(), data.getWindow().getTime());
    }

    @Override
    public int hashCode() {
        int hash = Objects.hashCode(getSta(), getChan(), seismogram, cumulativeAdjustment, getDelta(), getEvid(), getNominalPick(), maxCC,
                sigmaInSamples);
        return hash;
    }

    @Override
    public int compareTo(StationEventChannelData o) {
        if (o == null) {
            return 1;
        }
        if (eq(getNominalPickTime(), o.getNominalPickTime()) != 0) {
            return eq(getNominalPickTime(), o.getNominalPickTime());
        }
        if (getSta().compareTo(o.getSta()) != 0) {
            return getSta().compareTo(o.getSta());
        }
        if (getChan().compareTo(o.getChan()) != 0) {
            return getChan().compareTo(o.getChan());
        }
        if (getEvid() != o.getEvid()) {
            return (int)(getEvid() - o.getEvid());
        }
        if (eq(sigmaInSamples, o.sigmaInSamples) != 0) {
            return eq(sigmaInSamples, o.sigmaInSamples);
        }
        if (eq(getDelta(), getDelta()) != 0) {
            return eq(getDelta(), o.getDelta());
        }
        if (eq(getNominalPick(), o.getNominalPick()) != 0) {
            return eq(getNominalPick(), o.getNominalPick());
        }
        if (eq(getCumulativeAdjustment(), o.getCumulativeAdjustment()) != 0) {
            return eq(getCumulativeAdjustment(), o.getCumulativeAdjustment());
        }

        int ac = eq(seismogram, o.seismogram);
        if (ac != 0) {
            return ac;
        }

        return 0;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int eq(Comparable c, Comparable c2) {
        return c.compareTo(c2);
    }
    private static final double EPSILON = 0.00001;

    public static int eq(double d, double d2) {
        if (Math.abs(d - d2) < EPSILON) {
            return 0;
        }
        if (d < d2) {
            return -1;
        }
        return 1;
    }

    public FilteredFeatures getFeatures() {
        return features;
    }

    public EventStaInfo getEventStaInfo() {
        return data.getEvidWfidDelta();
    }

    /**
     * @return the bandInfo
     */
    public BandInfo getBandInfo() {
        return bandInfo;
    }

    /**
     * @return the window
     */
    public PhaseArrivalWindow getWindow() {
        return window;
    }

    public double getLength() {
        return seismogram.getEndtime().getEpochTime() - window.getTime();
    }
}
