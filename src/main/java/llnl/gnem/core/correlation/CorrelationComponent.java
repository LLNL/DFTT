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
package llnl.gnem.core.correlation;

import llnl.gnem.core.seismicData.EventInfo;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.filter.StoredFilter;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class CorrelationComponent implements Comparable { //extends BaseSingleComponent

    private final CorrelationEventInfo event;
    private double shift;
    private double correlation;
    private double std;
    private final String refstaName;
    private final Double dnorth;
    private final Double deast;
    private final boolean refsta;
    private final boolean element;
    private CorrelationTraceData correlationTraceData;

    public CorrelationComponent(CorrelationComponent other) {
        event = new CorrelationEventInfo(other.event);
        shift = other.shift;
        correlation = other.correlation;
        std = other.std;
        refstaName = other.refstaName;
        dnorth = other.dnorth;
        deast = other.deast;
        refsta = other.refsta;
        element = other.element;
    }

    public CssSeismogram getSeismogram() {
        return correlationTraceData.getSeismogram();
    }

    /**
     * @return the refsta
     */
    public String getRefstaName() {
        return refstaName;
    }

    /**
     * @return the dnorth
     */
    public Double getDnorth() {
        return dnorth;
    }

    /**
     * @return the deast
     */
    public Double getDeast() {
        return deast;
    }

    /**
     * @return the isRefsta
     */
    public boolean isRefsta() {
        return refsta;
    }

    /**
     * @return the isElement
     */
    public boolean isElement() {
        return element;
    }

    public CorrelationTraceData getCorrelationTraceData() {
        return correlationTraceData;
    }

    public double estimatePickStdErr(double time) {
        return correlationTraceData.estimatePickStdErr(time);
    }

    public void applyFilter(StoredFilter filter) {
        correlationTraceData.applyFilter(filter);
    }

    public void unApplyFilter() {
        correlationTraceData.unApplyFilter();
    }

    public void removeTrend() {
        correlationTraceData.removeTrend();
    }

    public void removeMean() {
        correlationTraceData.removeMean();
    }

    public void applyTaper(double taperPercent) {
        correlationTraceData.taper(taperPercent);
    }

    public void resample(double newRate) {
        correlationTraceData.resample(newRate);
    }

    public Integer getFilterid() {
        StoredFilter filter = correlationTraceData.getCurrentFilter();
        return filter != null ? filter.getFilterid() : null;
    }

    public float[] getSegment(double start, double duration) {
        return correlationTraceData.getSegment(start, duration);
    }

    public CorrelationComponent(
            CorrelationTraceData ctd, EventInfo event) {
        this.event = new CorrelationEventInfo(event);
        this.correlationTraceData = ctd;
        shift = 0;
        correlation = 0;

        refstaName = null;
        dnorth = null;
        deast = null;
        refsta = false;
        element = false;

    }

    public CorrelationComponent( CorrelationTraceData ctd, EventInfo event, String refstaName, Double dnorth, Double deast) {
        this.event = new CorrelationEventInfo(event);
        this.correlationTraceData = ctd;
        shift = 0;
        correlation = 0;
        this.refstaName = refstaName;
        this.dnorth = dnorth;
        this.deast = deast;
        refsta = true;
        element = true;
    }

    public boolean isArrayComponent() {
        return refsta || element;
    }

    public EventInfo getEvent() {
        return event;
    }

    public StreamKey getStreamKey() {
        return correlationTraceData.getStreamKey();
    }

    public double getDegDist() {
        return -999.0;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public void setShift(double shift) {
        this.shift = shift;
    }

    public double getShift() {
        return shift;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public double getStd() {
        return std;
    }

    public NominalArrival getNominalPick() {
        return this.correlationTraceData.getNominalPick();
    }

    public boolean containsWindow() {

        double prePhaseOffset = 5; // 
        double postPhaseOffset = 10;

        NominalArrival arrival = correlationTraceData.getNominalPick();
        double start = arrival.getTime() - prePhaseOffset;
        double end = arrival.getTime() + postPhaseOffset;
        Epoch epoch = new Epoch(start, end);
        return correlationTraceData.getSeismogram().contains(epoch, true);
    }

    @Override
    public int compareTo(Object t) {
        CorrelationComponent other = (CorrelationComponent) t;
        if (this.event.getEvid() > other.event.getEvid()) {
            return 1;
        } else if (this.event.getEvid() < other.event.getEvid()) {
            return -1;
        } else {
            return 0;
        }
    }

    long getWfid() {
        return correlationTraceData.getIdentifier();
    }
}
