package llnl.gnem.apps.detection.sdBuilder.arrayDisplay;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.core.signalProcessing.FKMeasurement;

import llnl.gnem.core.correlation.CorrelationComponent;

import llnl.gnem.core.waveform.filter.FilterClient;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class ArrayDisplayModel implements FilterClient {

    private ArrayDisplayViewer viewer;
    private final ArrayList<CorrelationComponent> elements;
    private double fWindowStart;
    private double fkWindowDuration;

    private ArrayDisplayModel() {
        elements = new ArrayList<>();
    }

    public static ArrayDisplayModel getInstance() {
        return ArrayDisplayModelHolder.INSTANCE;
    }

    public void setViewer(ArrayDisplayViewer viewer) {
        this.viewer = viewer;
    }

    public void setMatchingTraces(Collection<CorrelationComponent> matchingTraces) {
        elements.clear();
        this.elements.addAll(matchingTraces);
        viewer.dataWasLoaded(false, false);
    }

    public void clear() {
        elements.clear();
        if (viewer != null) {
            viewer.clear();
        }
    }

    public void removeComponent(CorrelationComponent selectedComponent) {
        boolean retainLimits = true;
        viewer.dataWasLoaded(retainLimits, false);
    }

    @Override
    public void applyFilter(StoredFilter filter) {
        for (CorrelationComponent comp : elements) {
            comp.applyFilter(filter);
        }

        notifyViewsTracesFiltered();
    }

    @Override
    public void unApplyFilter() {
        for (CorrelationComponent comp : elements) {
            comp.unApplyFilter();
        }
        notifyViewsTracesFiltered();
    }

    private void notifyViewsTracesFiltered() {
        viewer.updateForChangedTrace();
    }

    int getCurrentFilter() {
        for (CorrelationComponent comp : elements) {
            return comp.getFilterid();
        }
        throw new IllegalStateException("Failed to get current FILTERID!");
    }

    Collection<CorrelationComponent> getTraces() {
        return new ArrayList<>(elements);
    }

    void setWindowStart(double pickTime) {
        fWindowStart = pickTime;
    }

    void setWindowDuration(double duration) {
        fkWindowDuration = duration;
    }

    void adjustWindowStart(double delta) {
        fWindowStart += delta;
    }

    void adjustWindowDuration(double delta) {
        fkWindowDuration += delta;
    }
    
    public FKMeasurement computeFKStatistic()
    {
        float smax = 0.5f;
        int ns = 100;
        float f1 = 1;
        float f2 = 8;
        ArrayList<float[]> waveforms = new ArrayList<>();
        float[] xnorth = new float[elements.size()];
        float[] xeast = new float[elements.size()];
        float delta = 0;
        int idx = 0;
        for(CorrelationComponent comp : elements) {
            xnorth[idx] = (float)(double)comp.getDnorth();
            xeast[idx] = (float)(double)comp.getDeast();
            waveforms.add(comp.getSegment(fWindowStart, fkWindowDuration));
            delta = (float)comp.getSeismogram().getDelta();
            ++idx;
        }
        
        return new FKMeasurement( smax,
             ns,
             xnorth,
             xeast,
             waveforms,
             delta,
             f1,
             f2);
        
    }

    public int getDetectionID() {
        for (CorrelationComponent comp : elements) {
            return (int)comp.getEvent().getEvid();
        }
        throw new IllegalStateException("No DETECTIONID found!");
    }

    public double getWindowStart() {
        return fWindowStart;
    }

    public double getWindowDuration() {
        return fkWindowDuration;
    }

    private static class ArrayDisplayModelHolder {

        private static final ArrayDisplayModel INSTANCE = new ArrayDisplayModel();
    }
}
