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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.DetectionWaveforms;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class DetectionPhasePickModel {

    private String currentPhase = "NONE";
    private final Map<String, Double> phaseTimeMap;
    private final Map<CorrelationComponent, Set<PhasePick>> corrCompPhasePickMap;
    private final Collection<Integer> deletedPicks;

    private DetectionPhasePickModel() {
        phaseTimeMap = new HashMap<>();
        corrCompPhasePickMap = new HashMap<>();
        deletedPicks = new ArrayList<>();
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
//        Collection<CorrelationComponent> data = CorrelatedTracesModel.getInstance().getMatchingTraces(sta, chan);
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

        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            if (cc.getStreamKey().equals(key)) {
                // get all picks for this station-chan...
                Set<PhasePick> detPicks = corrCompPhasePickMap.get(cc);
                for (PhasePick dpp : detPicks) {
                    if (dpp.getPhase().equals(phase)) {
                        dpp.adjustPickTime(deltaT);
                    }
                }
            }

        }
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void clear() {
        deletedPicks.clear();
        phaseTimeMap.clear();
        corrCompPhasePickMap.clear();
        CorrelatedTracesModel.getInstance().notifyViewsPicksRemoved();
    }

    public void deleteAllPicks() {
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            Collection<PhasePick> picks = corrCompPhasePickMap.get(cc);
            for (PhasePick pp : picks) {
                int pickid = pp.getPickid();
                if (pickid > 0) {
                    deletedPicks.add(pickid);
                }
            }
        }
        phaseTimeMap.clear();
        corrCompPhasePickMap.clear();
        CorrelatedTracesModel.getInstance().notifyViewsPicksRemoved();
    }

    public PhasePick getPickForComponentAndPhase(CorrelationComponent cc, String phase) {
        Collection<PhasePick> picks = corrCompPhasePickMap.get(cc);
        if (picks == null) {
            return null;
        }
        for (PhasePick dpp : picks) {
            if (dpp.getPhase().equals(phase)) {
                return dpp;
            }
        }
        return null;
    }

    public PhasePick addSinglePick(CorrelationComponent cc, double detectionTime, Double pickStd, StreamKey key) {
        int detectionid = (int) cc.getEvent().getEvid();
        int configid = CorrelatedTracesModel.getInstance().getConfigid();

        //Get or create the collection of picks for this component...
        Set<PhasePick> picks = corrCompPhasePickMap.get(cc);

        if (picks == null) {
            picks = new HashSet<>();
            corrCompPhasePickMap.put(cc, picks);
        }

        // If already a pick for this detectionid and phase, then replace the pick.
        Iterator<PhasePick> it = picks.iterator();
        while (it.hasNext()) {
            PhasePick dpp = it.next();
            if (dpp.getDetectionid() != null && dpp.getDetectionid() == detectionid && dpp.getPhase().equals(currentPhase) && dpp.getKey().equals(key) ) {
                it.remove();
                if (dpp.getPickid() > 0) {
                    deletedPicks.add(dpp.getPickid());
                }
            }
        }
        PhasePick dpp = new PhasePick(-1, configid, detectionid, key, currentPhase, detectionTime, pickStd);
        picks.add(dpp);
        return dpp;
    }

    public Map<CorrelationComponent, Collection<PhasePick>> getAllPicks() {
        return new HashMap<>(corrCompPhasePickMap);
    }

    public void addExistingPicks(Collection<DetectionWaveforms> allWaveforms, Collection<PhasePick> detectionPicks) {
        Map<Integer, Collection<PhasePick>> detectionPickMap = new HashMap<>();
        for (PhasePick dpp : detectionPicks) {
            Collection<PhasePick> picks = detectionPickMap.get(dpp.getDetectionid());
            if (picks == null) {
                picks = new ArrayList<>();
                detectionPickMap.put(dpp.getDetectionid(), picks);
            }
            picks.add(dpp);
        }

        for (DetectionWaveforms dw : allWaveforms) {
            for (CorrelationComponent cc : dw.getSegments()) {
                int detectionid = (int) cc.getEvent().getEvid();
                Collection<PhasePick> tmp = detectionPickMap.get(detectionid);
                if (tmp != null && !tmp.isEmpty()) {
                    Set<PhasePick> picks = new HashSet<>();
                    for (PhasePick pp : tmp) {
                        if (pp.getKey().equals(cc.getStreamKey())) {
                            picks.add(pp);
                        }
                    }
                    if (!picks.isEmpty()) {
                        corrCompPhasePickMap.put(cc, picks);
                    }
                }
            }
        }
    }

    public void adjustAllPickStdValuesForPhase(String phase, double deltaT, StreamKey key) {
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            if (cc.getStreamKey().equals(key)) {
                Set<PhasePick> detPicks = corrCompPhasePickMap.get(cc);

                for (PhasePick dpp : detPicks) {
                    if (dpp.getPhase().equals(phase) && dpp.getKey().equals(key) ) {
                        dpp.adjustStd(deltaT);
                    }
                }
            }
        }
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void moveSinglePick(PhasePick dpp, double deltaT) {
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            Collection<PhasePick> detpicks = corrCompPhasePickMap.get(cc);
            for (PhasePick detPick : detpicks) {
                if (detPick.equals(dpp)) {
                    detPick.adjustPickTime(deltaT);
                    CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
                    return;
                }
            }
        }

    }

    public void adjustSinglePickStd(PhasePick dpp, double deltaT) {
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            Collection<PhasePick> detpicks = corrCompPhasePickMap.get(cc);
            for (PhasePick detPick : detpicks) {
                if (detPick.equals(dpp)) {
                    detPick.adjustStd(deltaT);
                    CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
                    return;
                }
            }
        }

    }

    public void deleteSinglePick(PhasePick selectedPick) {
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            Collection<PhasePick> detpicks = corrCompPhasePickMap.get(cc);
            for (PhasePick detPick : detpicks) {
                if (detPick.equals(selectedPick)) {
                    detpicks.remove(detPick);
                    deletedPicks.add(detPick.getPickid());
                    CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
                    return;

                }
            }
        }
    }

    public String getBestChanForPicks() {
        Map<String, Integer> chanCountMap = new HashMap<>();
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            String chan = cc.getStreamKey().getChan();
            Integer acount = chanCountMap.get(chan);
            if (acount == null) {
                acount = 0;
            }
            Collection<PhasePick> detpicks = corrCompPhasePickMap.get(cc);
            if (detpicks != null) {
                acount += detpicks.size();
            }
            chanCountMap.put(chan, acount);
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

    private static class DetectionPhasePickModelHolder {

        private static final DetectionPhasePickModel INSTANCE = new DetectionPhasePickModel();
    }
}
