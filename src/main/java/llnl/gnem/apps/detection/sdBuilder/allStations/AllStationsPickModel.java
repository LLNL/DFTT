/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.core.waveform.BaseTraceData;

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
            PhasePick dpp = new PhasePick(-1, configid, null, currentPhase, pickEpochTime, pickStd);
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
                    esd.addPick(new PhasePick(dpp.getPickid(), dpp.getConfigid(), dpp.getDetectionid(),
                            dpp.getPhase(), dpp.getTime() + deltaT, dpp.getStd()));
                    esd.removePick(pick);
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
