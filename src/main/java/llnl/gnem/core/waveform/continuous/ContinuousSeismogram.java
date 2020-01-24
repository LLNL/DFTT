/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.continuous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.seriesMathHelpers.MinMax;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.filter.StoredFilter;
import llnl.gnem.core.waveform.merge.MergeException;

/**
 *
 * @author dodge1
 */
public class ContinuousSeismogram {

    private final Map<Double, CssSeismogram> timeSegmentMap;
    private final StreamKey identifier;

    public ContinuousSeismogram(CssSeismogram segment) {
        timeSegmentMap = new TreeMap<>();
        timeSegmentMap.put(segment.getTimeAsDouble(), segment);
        identifier = segment.getStreamKey();
    }

    public ContinuousSeismogram(ContinuousSeismogram other) {
        timeSegmentMap = new TreeMap<>();
        StreamKey tmp = null;
        for (Double time : other.timeSegmentMap.keySet()) {
            CssSeismogram seis = other.timeSegmentMap.get(time);
            timeSegmentMap.put(time, new CssSeismogram(seis));
            tmp = seis.getStreamKey();
        }
        identifier = tmp;
    }

    public ContinuousSeismogram(Collection<CssSeismogram> segments) {
        if (segments.isEmpty()) {
            throw new IllegalStateException("Attempt to initialize ContinuousSeismogram with empty collection!");
        }
        timeSegmentMap = new TreeMap<>();
        identifier = segments.iterator().next().getStreamKey();
        for (CssSeismogram seis : segments) {
            timeSegmentMap.put(seis.getTimeAsDouble(), seis);
            if (!seis.getStreamKey().equals(identifier)) {
                throw new IllegalStateException("Not all segments are for same channel!");
            }
        }
    }

    public CssSeismogram getSegment(Epoch epoch) throws MergeException {
        ArrayList<CssSeismogram> segments = new ArrayList<>();
        for (CssSeismogram seis : timeSegmentMap.values()) {
            if (seis.getEpoch().isSuperset(epoch)) {
                CssSeismogram result = new CssSeismogram(seis);
                result.trimTo(epoch);
                return result;
            }
        }
        timeSegmentMap.values().forEach((seis) -> {
            Epoch thisEpoch = seis.getEpoch();
            if (thisEpoch.intersects(epoch)) {
                segments.add(seis);
            }
        });
        if (segments.isEmpty()) {
            return null;
        } else {
            CssSeismogram first = segments.get(0);
            for (int j = 1; j < segments.size(); ++j) {
                CssSeismogram seis = segments.get(j);
                first = CssSeismogram.unionOf(first, seis);
            }
            first.trimTo(epoch);
            return first;
        }
    }

    public Collection<CssSeismogram> getSegments() {
        return new ArrayList<>(timeSegmentMap.values());
    }

    public StreamKey getIdentifier() {
        return identifier;
    }

    public double getMean() {
        double result = 0;
        int count = 0;
        for (CssSeismogram seis : timeSegmentMap.values()) {
            result += seis.getMean();
            ++count;
        }
        return count > 0 ? result / count : 0;
    }

    public MinMax getMinMax() {
        double min = Double.MAX_VALUE;
        double max = -min;
        for (CssSeismogram seis : timeSegmentMap.values()) {
            MinMax mm = seis.getMinMax();
            min = mm.getMin() < min ? mm.getMin() : min;
            max = mm.getMax() > max ? mm.getMax() : max;
        }
        return new MinMax(min, max);
    }

    public double getTime() {
        double result = Double.MAX_VALUE;
        for (CssSeismogram seis : timeSegmentMap.values()) {
            double time = seis.getTimeAsDouble();
            if (time < result) {
                result = time;
            }
        }
        return result;
    }

    public double getEnd() {
        double result = -Double.MAX_VALUE;
        for (CssSeismogram seis : timeSegmentMap.values()) {
            double value = seis.getEndtime().getEpochTime();
            if (value > result) {
                result = value;
            }
        }
        return result;
    }

    void replaceContents(ContinuousSeismogram second) {
        if (!this.identifier.equals(second.identifier)) {
            throw new IllegalArgumentException("Attempt to replace contents using data from different channel!");
        }
        timeSegmentMap.clear();
        for (CssSeismogram seis : second.timeSegmentMap.values()) {
            double value = seis.getTimeAsDouble();
            timeSegmentMap.put(value, new CssSeismogram(seis));
        }
    }

    void applyFilter(StoredFilter filter) {
        for (CssSeismogram seis : timeSegmentMap.values()) {
            seis.applyFilter(filter);
        }
    }

}
