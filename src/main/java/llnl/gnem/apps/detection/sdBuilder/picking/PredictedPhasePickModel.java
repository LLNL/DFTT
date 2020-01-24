/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.picking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.sdBuilder.DetectionWaveforms;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;

/**
 *
 * @author dodge1
 */
public class PredictedPhasePickModel {

    Map<CorrelationComponent, Collection<PredictedPhasePick>> corrCompPhasePickMap;

    private static class PredictedPhasePickModelHolder {

        private static final PredictedPhasePickModel INSTANCE = new PredictedPhasePickModel();
    }

    private PredictedPhasePickModel() {
        corrCompPhasePickMap = new HashMap<>();
    }

    public static PredictedPhasePickModel getInstance() {
        return PredictedPhasePickModelHolder.INSTANCE;
    }

    public void clear() {
        corrCompPhasePickMap.clear();
    //    CorrelatedTracesModel.getInstance().notifyViewsPicksRemoved();
    }

    public Map<CorrelationComponent, Collection<PredictedPhasePick>> getAllPicks() {
        return new HashMap<>(corrCompPhasePickMap);
    }

    public void addExistingPicks(Collection<DetectionWaveforms> allWaveforms, Collection<PredictedPhasePick> detectionPicks) {
        Map<Integer, Collection<PredictedPhasePick>> detectionPickMap = new HashMap<>();
        for (PredictedPhasePick dpp : detectionPicks) {
            Collection<PredictedPhasePick> picks = detectionPickMap.get(dpp.getAssociatedDetectionid());
            if (picks == null) {
                picks = new ArrayList<>();
                detectionPickMap.put(dpp.getAssociatedDetectionid(), picks);
            }
            picks.add(dpp);
        }

        for (DetectionWaveforms dw : allWaveforms) {
            for (CorrelationComponent cc : dw.getSegments()) {
                int detectionid = (int) cc.getEvent().getEvid();
                Collection<PredictedPhasePick> picks = detectionPickMap.get(detectionid);
                if (picks != null) {
                    corrCompPhasePickMap.put(cc, picks);
                }
            }
        }
    }
}
