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
package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.core.correlation.CorrelationComponent;

/**
 *
 * @author dodge1
 */
public class SingleDetectionModel {

 
    private Detection detection;
    private CorrelationComponent cc;
    private final Collection<SingleDetectionView> views;
    private int runid;
    private TriggerDataFeatures triggerDataFeatures;

    private SingleDetectionModel() {
        views = new ArrayList<>();
    }

    public static SingleDetectionModel getInstance() {
        return SingleDetectionModelHolder.INSTANCE;
    }

    public void setData(CorrelationComponent cc, int runid) {
        this.cc = new CorrelationComponent(cc);
        this.runid = runid;
        for (SingleDetectionView view : views) {
            view.traceWasAdded();
        }
        new TriggerStatisticsRetrievalWorker((int) cc.getEvent().getEvid()).execute();
        new FeatureValuesRetrievalWorker(runid, "AMPLITUDE").execute();
        new SingleDetectionRetrievalWorker( (int)cc.getEvent().getEvid()).execute();
    }

    public void clear() {
        cc = null;
        for (SingleDetectionView view : views) {
            view.clear();
        }
    }

    public CorrelationComponent getCorrelationComponent() {
        return cc;
    }

    void addView(SingleDetectionView view) {
        views.remove(view);
        views.add(view);
    }

    void setTriggerStatistics(TriggerDataFeatures result) {
        this.triggerDataFeatures = result;
        views.forEach(view -> {
            view.setTriggerStatistics(result);
        });
    }

    public void featureSelected(String columnName) {
        new FeatureValuesRetrievalWorker(runid, columnName).execute();
    }

    void setFeatureValues(String featureName, Collection<Double> result) {
        views.forEach(view -> {
            view.setFeatureValues(featureName, result);
        });
    }

    void setDetection(Detection detection) {
        this.detection = detection;
        views.forEach(view -> {
            view.detectionRetrieved();
        });
    }

    public int getDetectorid()
    {
        return detection != null ? detection.getDetectionid() : -1;
    }
       /**
     * @return the detection
     */
    public Detection getDetection() {
        return detection;
    }


    /**
     * @return the triggerDataFeatures
     */
    public TriggerDataFeatures getTriggerDataFeatures() {
        return triggerDataFeatures;
    }

    private static class SingleDetectionModelHolder {

        private static final SingleDetectionModel INSTANCE = new SingleDetectionModel();
    }
}
