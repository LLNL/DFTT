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
