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
import java.util.Iterator;
import java.util.Map;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.DetectionWaveforms;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;

/**
 *
 * @author dodge1
 */
public class DetectionPhasePickModel {

    private String currentPhase = "NONE";
    private final Map<String, Double> phaseTimeMap;
    private final Map<CorrelationComponent, Collection<PhasePick>> corrCompPhasePickMap;
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

    public void createPickForCurrentPhase(double pickTime, Double pickStd) {
        phaseTimeMap.put(currentPhase, pickTime);
        Collection<CorrelationComponent> data = CorrelatedTracesModel.getInstance().getMatchingTraces();
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getTraceData();

            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double detectionTime = nominalPickTime - ccShift + pickTime;
            addSinglePick(cc, detectionTime, pickStd);
        }

        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void adjustAllPickTimesForPhase(String phase, double deltaT) {

        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            Collection<PhasePick> detPicks = corrCompPhasePickMap.get(cc);
            Collection<PhasePick> replacement = new ArrayList<>();
            Iterator<PhasePick> it = detPicks.iterator();
            while (it.hasNext()) {
                PhasePick dpp = it.next();
                if (dpp.getPhase().equals(phase)) {
                    replacement.add(new PhasePick(dpp.getPickid(), dpp.getConfigid(), dpp.getDetectionid(), phase, dpp.getTime() + deltaT, dpp.getStd()));
                    it.remove();
                }
            }
            detPicks.addAll(replacement);

        }
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void clear() {
        phaseTimeMap.clear();
        corrCompPhasePickMap.clear();
        deletedPicks.clear();
        CorrelatedTracesModel.getInstance().notifyViewsPicksRemoved();
    }
    
    public PhasePick getPickForComponentAndPhase(CorrelationComponent cc, String phase){
        Collection<PhasePick> picks = corrCompPhasePickMap.get(cc);
        if( picks == null){
            return null;
        }
        for( PhasePick dpp : picks){
            if(dpp.getPhase().equals(phase)){
                return dpp;
            }
        }
        return null;
    }

    public PhasePick addSinglePick(CorrelationComponent cc, double detectionTime, Double pickStd) {
        int detectionid = (int) cc.getEvent().getEvid();
        int configid = CorrelatedTracesModel.getInstance().getConfigid();
        Collection<PhasePick> picks = corrCompPhasePickMap.get(cc);

        if (picks == null) {
            picks = new ArrayList<>();
            corrCompPhasePickMap.put(cc, picks);
        }

        // If already a pick for this detectionid and phase, then replace the pick.
        Iterator<PhasePick> it = picks.iterator();
        while (it.hasNext()) {
            PhasePick dpp = it.next();
            if (dpp.getDetectionid() != null && dpp.getDetectionid() == detectionid && dpp.getPhase().equals(currentPhase)) {
                it.remove();
            }
        }
        PhasePick dpp = new PhasePick(-1,configid, detectionid, currentPhase, detectionTime, pickStd);
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
            Collection<PhasePick> nonDetectionPicks = dw.getAssociatedPicks();
            for (CorrelationComponent cc : dw.getSegments()) {
                int detectionid = (int) cc.getEvent().getEvid();
                Collection<PhasePick> picks = detectionPickMap.get(detectionid);
                if (picks != null) {
                    picks.addAll(nonDetectionPicks);
                    corrCompPhasePickMap.put(cc, picks);
                }
                else if(!nonDetectionPicks.isEmpty()){
                    corrCompPhasePickMap.put(cc, nonDetectionPicks);
                }
            }
        }
    }

    public void adjustAllPickStdValuesForPhase(String phase, double deltaT) {
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            Collection<PhasePick> detPicks = corrCompPhasePickMap.get(cc);
            Collection<PhasePick> replacement = new ArrayList<>();
            Iterator<PhasePick> it = detPicks.iterator();
            while (it.hasNext()) {
                PhasePick dpp = it.next();
                if (dpp.getPhase().equals(phase)) {
                    replacement.add(new PhasePick(dpp.getPickid(), dpp.getConfigid(), 
                            dpp.getDetectionid(), phase, dpp.getTime(), dpp.getStd() + deltaT));
                    it.remove();
                }
            }
            detPicks.addAll(replacement);

        }
        CorrelatedTracesModel.getInstance().notifyViewsPicksChanged();
    }

    public void moveSinglePick(PhasePick dpp, double deltaT) {
        for (CorrelationComponent cc : corrCompPhasePickMap.keySet()) {
            Collection<PhasePick> detpicks = corrCompPhasePickMap.get(cc);
            for (PhasePick detPick : detpicks) {
                if (detPick.equals(dpp)) {
                    detpicks.add(new PhasePick(dpp.getPickid(), dpp.getConfigid(), dpp.getDetectionid(),
                            dpp.getPhase(), dpp.getTime() + deltaT, dpp.getStd()));
                    detpicks.remove(detPick);
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
                    detpicks.add(new PhasePick(dpp.getPickid(), dpp.getConfigid(),dpp.getDetectionid(),
                            dpp.getPhase(), dpp.getTime(), dpp.getStd() + deltaT));
                    detpicks.remove(detPick);
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

    private static class DetectionPhasePickModelHolder {

        private static final DetectionPhasePickModel INSTANCE = new DetectionPhasePickModel();
    }
}
