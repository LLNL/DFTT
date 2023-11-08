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
package llnl.gnem.apps.detection.sdBuilder.picking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.DetectionWaveforms;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.correlation.CorrelationTraceData;
import llnl.gnem.dftt.core.util.StreamKey;

public class DetectionPhasePickModel {

    private String currentPhase = "NONE";
    private final Map<String, Double> phaseTimeMap;
    private final Collection<Integer> deletedPicks;
    private final Map<DetectionStation, PickSet> detectionPickMap;

    private DetectionPhasePickModel() {
        phaseTimeMap = new HashMap<>();
        deletedPicks = new ArrayList<>();
        detectionPickMap = new HashMap<>();
    }

    public static DetectionPhasePickModel getInstance() {
        return DetectionPhasePickModelHolder.INSTANCE;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String phase) {
        currentPhase = phase;
    }

    public Collection<Integer> getDeletedPicks() {
        return new ArrayList<>(deletedPicks);
    }

    public void createMultiplePicksForCurrentPhase(double relativePickTime, Double pickStd, StreamKey key, Collection<CorrelationComponent> channels) {
        phaseTimeMap.put(currentPhase, relativePickTime);
        for (CorrelationComponent cc : channels) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
            if (td.getStreamKey().equals(key)) {
                double detectionTime = td.getNominalPick().getTime();
                double ccShift = cc.getShift();
                double pickEpochTime = detectionTime - ccShift + relativePickTime;
                addSinglePick(cc, pickEpochTime, pickStd, key);
            }
        }

        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void adjustAllPickTimesForPhase(String phase, double deltaT, StreamKey key) {

        Collection<PickSet> picks = detectionPickMap.values();
        for (PickSet ps : picks) {
            for (PhasePick dpp : ps.getPicks()) {
                if (dpp.getKey().equals(key) && dpp.getPhase().equals(phase)) {
                    dpp.adjustPickTime(deltaT);
                }
            }
        }
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void clear() {
        deletedPicks.clear();
        phaseTimeMap.clear();
        detectionPickMap.clear();
        CorrelatedTracesModel.getInstance().notifyViewsPicksRemoved();
    }

    public void deleteAllPicks() {
        Collection<PickSet> picks = detectionPickMap.values();
        for (PickSet ps : picks) {
            for (PhasePick pp : ps.getPicks()) {
                int pickid = pp.getPickid();
                if (pickid > 0) {
                    deletedPicks.add(pickid);
                }
            }
        }
        phaseTimeMap.clear();
        detectionPickMap.clear();
        CorrelatedTracesModel.getInstance().notifyViewsPicksRemoved();
    }

    public PhasePick getPickForComponentAndPhase(CorrelationComponent cc, String phase) {

        Collection<PickSet> picks = detectionPickMap.values();
        for (PickSet ps : picks) {
            for (PhasePick dpp : ps.getPicks()) {
                CorrelationComponent acc = ps.getComponentForPick(dpp);
                if (acc != null && acc.equals(cc) && dpp.getPhase().equals(phase)) {
                    return dpp;
                }
            }
        }
        return null;
    }

    public PhasePick addSinglePick(CorrelationComponent cc, double detectionTime, Double pickStd, StreamKey key) {
        int detectionid = (int) cc.getEvent().getEvid();
        int configid = CorrelatedTracesModel.getInstance().getConfigid();
        PhasePick dpp = new PhasePick(-1, configid, detectionid, key, currentPhase, detectionTime, pickStd);
        DetectionStation ds = new DetectionStation(detectionid,key.getSta());
        PickSet ps = detectionPickMap.get(ds);
        if (ps == null) { // This is first pick for this detectionid.
            ps = new PickSet(dpp, cc);
            detectionPickMap.put(ds, ps);
        } else { // One or more picks already exist for this detectionid.
            Collection<PhasePick> existingPicks = ps.getPicks();
            for (PhasePick pp : existingPicks) {
                if (pp.getPhase().equals(currentPhase) && pp.getKey().getSta().equals(key.getSta())) {//We already have this phase pick for this detection and station
                    if (pp.getPickid() > 0) {
                        deletedPicks.add(pp.getPickid());
                    }
                    ps.removePick(pp);
                }
            }
            ps.addPick(dpp, cc);
        }

        return dpp;
    }

    public Map<CorrelationComponent, Collection<PhasePick>> getAllPicks() {
        Map<CorrelationComponent, Collection<PhasePick>> result = new HashMap<>();
        for (PickSet ps : detectionPickMap.values()) {
            for (PhasePick pp : ps.getPicks()) {
                CorrelationComponent cc = ps.getComponentForPick(pp);
                Collection<PhasePick> picks = result.get(cc);
                if (picks == null) {
                    picks = new ArrayList<>();
                    result.put(cc, picks);
                }
                picks.add(pp);
            }
        }
        return result;
    }

    /*
    This method is called by waveform retrieval code. It gets all picks for detector. In the case of
    a network detector, the picks can be on multiple stations.
    */
    public void addExistingPicks(Collection<DetectionWaveforms> allWaveforms, Collection<PhasePick> detectionPicks) {
        Map<DetectionStation, Collection<PhasePick>> detPickMap = new HashMap<>(); // Use this map to lookup all picks for a detection
        for (PhasePick dpp : detectionPicks) {
            DetectionStation ds = new DetectionStation(dpp.getDetectionid(),dpp.getKey().getSta());
            Collection<PhasePick> tmp = detPickMap.get(ds);
            if (tmp == null) {
                tmp = new ArrayList<>();
                detPickMap.put(ds, tmp);
            }
            tmp.add(dpp);
        }

        for (DetectionWaveforms dw : allWaveforms) {
            int detectionid = dw.getDetectionid();
            for(CorrelationComponent cc : dw.getSegments()){
                StreamKey key = cc.getStreamKey();
                DetectionStation ds = new DetectionStation(detectionid,key.getSta());
                PickSet ps = detectionPickMap.get(ds);
                Collection<PhasePick> picks = detPickMap.get(ds);
                if (picks != null && !picks.isEmpty()) {
                    for (PhasePick pp : picks) {
                        if (pp.getKey().getSta().equals(key.getSta()) && pp.getKey().getChan().equals(key.getChan())) {
                            if (ps == null) {
                                ps = new PickSet(pp, cc);
                                detectionPickMap.put(ds, ps);
                            }
                            else {
                                ps.addPick(pp, cc);
                            }
                        }
                    }
                }
            }
         }
        System.out.println("");
    }

    public void adjustAllPickStdValuesForPhase(String phase, double deltaT, StreamKey key) {
        Collection<PickSet> cps = detectionPickMap.values();
        for (PickSet ps : cps) {
            for (PhasePick dpp : ps.getPicks()) {
                if (dpp.getPhase().equals(phase) && dpp.getKey().equals(key)) {
                    dpp.adjustStd(deltaT);
                }
            }
        }
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void moveSinglePick(PhasePick dpp, double deltaT) {
        dpp.adjustPickTime(deltaT);
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void adjustSinglePickStd(PhasePick dpp, double deltaT) {
        dpp.adjustStd(deltaT);
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void deleteSinglePick(PhasePick selectedPick) {
        int detectionid = selectedPick.getDetectionid();
        StreamKey key = selectedPick.getKey();
        DetectionStation ds = new DetectionStation(detectionid,key.getSta());
        PickSet existing = detectionPickMap.get(ds);
        if (existing != null) {
            existing.removePick(selectedPick);
            int pickId = selectedPick.getPickid();
            if (pickId > 0) {
                deletedPicks.add(selectedPick.getPickid());
            }
            CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();

        }
    }

    public String getBestChanForPicks() {
        Map<String, Integer> chanCountMap = new HashMap<>();
        Map<CorrelationComponent, Collection<PhasePick>> tmp = getAllPicks();
        for (CorrelationComponent cc : tmp.keySet()) {
            if (cc != null) {
                String chan = cc.getStreamKey().getChan();
                Integer acount = chanCountMap.get(chan);
                if (acount == null) {
                    acount = 0;
                }
                Collection<PhasePick> detpicks = tmp.get(cc);
                if (detpicks != null) {
                    acount += detpicks.size();
                }
                chanCountMap.put(chan, acount);
            }
        }
        String best = null;
        int bestCount = 0;
        for (String chan : chanCountMap.keySet()) {
            Integer acount = chanCountMap.get(chan);
            if (acount > bestCount) {
                bestCount = acount;
                best = chan;
            }
        }
        return best;
    }

    
    private static class DetectionStation{
        private final int detectionid;
        private final String stationCode;

        public DetectionStation(int detectionid, String stationCode) {
            this.detectionid = detectionid;
            this.stationCode = stationCode;
        }

        public int getDetectionid() {
            return detectionid;
        }

        public String getStationCode() {
            return stationCode;
        }

        @Override
        public String toString() {
            return "DetectionStation{" + "detectionid=" + detectionid + ", stationCode=" + stationCode + '}';
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 59 * hash + this.detectionid;
            hash = 59 * hash + Objects.hashCode(this.stationCode);
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
            final DetectionStation other = (DetectionStation) obj;
            if (this.detectionid != other.detectionid) {
                return false;
            }
            return Objects.equals(this.stationCode, other.stationCode);
        }
        
    }
    private static class DetectionPhasePickModelHolder {

        private static final DetectionPhasePickModel INSTANCE = new DetectionPhasePickModel();
    }

    private static class PickSet {

        private final Map<String, PhasePick> phasePickMap;
        private final Map<String, CorrelationComponent> pickComponentMap;

        public PickSet(PhasePick pick, CorrelationComponent cc) {
            phasePickMap = new HashMap<>();
            pickComponentMap = new HashMap<>();
            phasePickMap.put(pick.getPhase(), pick);
            pickComponentMap.put(pick.getPhase(), cc);
        }

        public void addPick(PhasePick pick, CorrelationComponent cc) {
            phasePickMap.put(pick.getPhase(), pick);
            pickComponentMap.put(pick.getPhase(), cc);
        }

        public void removePick(PhasePick pick) {
            phasePickMap.remove(pick.getPhase());
            pickComponentMap.remove(pick.getPhase());
        }

        public Collection<PhasePick> getPicks() {
            return new ArrayList<>(phasePickMap.values());
        }

        public CorrelationComponent getComponentForPick(PhasePick pp) {
            return pickComponentMap.get(pp.getPhase());
        }

        @Override
        public String toString() {
            PhasePick pp = phasePickMap.values().iterator().next();
            int detectionid = pp.getDetectionid();
            int count = phasePickMap.size();
            return String.format("%d Picks for detectionid: %d", count, detectionid);
        }
    }
}
