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
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.dftt.core.waveform.BaseTraceData;

/**
 *
 * @author dodge1
 */
public class AllStationsPickModel {

    private String currentPhase = "NONE";
    private final Collection<EventSeismogramData> seismograms;
    private MultiSeismogramPlot seismogramPlot;

    private AllStationsPickModel() {
        seismograms = new ArrayList<>();
    }

    public static AllStationsPickModel getInstance() {
        return AllStationsPickModelHolder.INSTANCE;
    }

    public PhasePick addSinglePick(BaseTraceData btd, double pickEpochTime, double pickStd) {

        EventSeismogramData esd = getDataFor(btd);
        if (esd != null) {
            int configid = esd.getStationInfo().getConfigid();
            PhasePick dpp = new PhasePick(-1, configid, null,btd.getStreamKey(), currentPhase, pickEpochTime, pickStd);
            esd.addPick(dpp);
            return dpp;
        }
        return null;
    }

    private EventSeismogramData getDataFor(BaseTraceData btd) {
        for (EventSeismogramData esd : seismograms) {
            if (esd.getTraceData().equals(btd)) {
                return esd;
            }
        }
        return null;
    }

    void moveSinglePick(PhasePick dpp, double deltaT) {
        for (EventSeismogramData esd : seismograms) {
            for (PhasePick pick : esd.getPicks()) {
                if (pick.equals(dpp)) {
                    pick.adjustPickTime(deltaT);
                    return;
                }
            }
        }
    }

    void setEventSeismogramData(Collection<EventSeismogramData> results) {
        seismograms.clear();
        seismograms.addAll(results);
    }

    public void deleteSinglePick(PhasePick dpp) {
        for (EventSeismogramData esd : seismograms) {
            for (PhasePick pick : esd.getPicks()) {
                if (pick.equals(dpp)) {
                    esd.removePick(pick);
                    if (dpp.getPickid() > 0) {
                        esd.markForDeletion(dpp.getPickid());
                    }
                    seismogramPlot.removeDeletedPick(dpp);
                    return;
                }
            }
        }
    }

    void setSeismogramPlot(MultiSeismogramPlot seismogramPlot) {
        this.seismogramPlot = seismogramPlot;
    }

    public Collection<PhasePick> getAllPicks() {
        Collection<PhasePick> result = new ArrayList<>();
        for (EventSeismogramData esd : seismograms) {
            for (PhasePick pick : esd.getPicks()) {
                result.add(pick);
            }
        }
        return result;
    }

    public Collection<Integer> getPicksToRemove() {
        Collection<Integer> result = new ArrayList<>();
        for (EventSeismogramData esd : seismograms) {
            result.addAll(esd.getPicksToDelete());
        }
        
        return result;
    }

    private static class AllStationsPickModelHolder {

        private static final AllStationsPickModel INSTANCE = new AllStationsPickModel();
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String phase) {
        currentPhase = phase;
    }

}
