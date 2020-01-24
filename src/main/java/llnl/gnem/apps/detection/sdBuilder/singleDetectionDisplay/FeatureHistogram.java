/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.singleDetectionDisplay;

import java.awt.Color;
import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.core.gui.plotting.histogram.Histogram;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;

/**
 *
 * @author dodge1
 */
public class FeatureHistogram extends Histogram implements SingleDetectionView{

    private static final long serialVersionUID = 2789884117682484530L;

    @Override
    public void traceWasAdded() {
        // Do nothing
    }

    @Override
    public void setTriggerStatistics(TriggerDataFeatures result) {
        // Do nothing...
    }

    @Override
    public void setFeatureValues(String featureName, Collection<Double> result) {
        getTitle().setText(String.format("%s distribution for current runid",featureName));
        float[] values = new float[result.size()];
        int j = 0;
        for(double v : result){
            values[j++] = (float) v;
        }
        setData( values, 100 );
        TriggerDataFeatures features = SingleDetectionModel.getInstance().getTriggerDataFeatures();
        double value = features.getFeatureValue(featureName);
        VPickLine vpl = new VPickLine(value,1.0, "");
        vpl.setAllDraggable(false);
        vpl.setColor(Color.red);
        this.addPlotObject(vpl);
    }

    @Override
    public void detectionRetrieved() {
        // Not needed...
    }
    
}
