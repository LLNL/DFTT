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
package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import llnl.gnem.apps.detection.dataAccess.dataobjects.StationInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.OriginInfo;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ShortDetectionSummary;
import llnl.gnem.core.waveform.BaseTraceData;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataType;
import llnl.gnem.core.waveform.responseProcessing.WaveformDataUnits;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class EventSeismogramData {

    private final Collection<OriginInfo> origins;
    private final StationInfo stationInfo;
    private final BaseTraceData traceData;
    private final Collection<ShortDetectionSummary> detections;
    private final Collection<PhasePick> picks;
    private final Collection<Integer> picksToDelete;

    public EventSeismogramData(Collection<OriginInfo> origins,
            StationInfo stationInfo,
            CssSeismogram seis,
            Collection<ShortDetectionSummary> detections,
            Collection<PhasePick> picks) {
        this.origins = new ArrayList<>(origins);
        this.stationInfo = stationInfo;
        this.traceData = new BaseTraceData(seis, WaveformDataType.unknown, WaveformDataUnits.unknown);
        this.detections = new ArrayList<>(detections);
        this.picks = new HashSet<>(picks);
        picksToDelete = new ArrayList<>();
    }

    public Collection<Integer> getPicksToDelete() {
        return new ArrayList<>(picksToDelete);
    }

    public StationInfo getStationInfo() {
        return stationInfo;
    }

    public BaseTraceData getTraceData() {
        return traceData;
    }

    public Collection<ShortDetectionSummary> getDetections() {
        return new ArrayList<>(detections);
    }

    public Collection<PhasePick> getPicks() {
        return new ArrayList<>(picks);
    }

    public Collection<OriginInfo> getOrigins() {
        return origins;
    }

    public void addPick(PhasePick dpp) {
        picks.add(dpp);
    }

    void removePick(PhasePick pick) {
        picks.remove(pick);
    }

    void markForDeletion(int pickid) {
        picksToDelete.add(pickid);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.origins);
        hash = 97 * hash + Objects.hashCode(this.stationInfo);
        hash = 97 * hash + Objects.hashCode(this.traceData);
        hash = 97 * hash + Objects.hashCode(this.detections);
        hash = 97 * hash + Objects.hashCode(this.picks);
        hash = 97 * hash + Objects.hashCode(this.picksToDelete);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventSeismogramData other = (EventSeismogramData) obj;
        if (!Objects.equals(this.origins, other.origins)) {
            return false;
        }
        if (!Objects.equals(this.stationInfo, other.stationInfo)) {
            return false;
        }
        if (!Objects.equals(this.traceData, other.traceData)) {
            return false;
        }
        if (!Objects.equals(this.detections, other.detections)) {
            return false;
        }
        if (!Objects.equals(this.picks, other.picks)) {
            return false;
        }
        if (!Objects.equals(this.picksToDelete, other.picksToDelete)) {
            return false;
        }
        return true;
    }

}
