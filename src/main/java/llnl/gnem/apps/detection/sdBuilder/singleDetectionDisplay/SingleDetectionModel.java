/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
