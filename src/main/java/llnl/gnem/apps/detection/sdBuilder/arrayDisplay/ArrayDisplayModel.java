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
package llnl.gnem.apps.detection.sdBuilder.arrayDisplay;

import java.util.ArrayList;
import java.util.Collection;

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

    public FKInputData getFKInputData() {
        ArrayList<float[]> waveforms = new ArrayList<>();
        float[] xnorth = new float[elements.size()];
        float[] xeast = new float[elements.size()];
        float delta = 0;
        int idx = 0;
        for (CorrelationComponent comp : elements) {
            xnorth[idx] = (float) (double) comp.getDnorth();
            xeast[idx] = (float) (double) comp.getDeast();
            waveforms.add(comp.getSegment(fWindowStart, fkWindowDuration));
            delta = (float) comp.getSeismogram().getDelta();
            ++idx;
        }
        return new FKInputData(waveforms,xnorth, xeast, delta );
    }
    
    public static class FKInputData{
        private final ArrayList<float[]> waveforms;
        private final float[] xnorth;
        private final float[] xeast;
        private final float delta;

        public FKInputData(ArrayList<float[]> waveforms, float[] xnorth, float[] xeast, float delta) {
            this.waveforms = new ArrayList<>(waveforms);
            this.xnorth = xnorth;
            this.xeast = xeast;
            this.delta = delta;
        }

        public ArrayList<float[]> getWaveforms() {
            return new ArrayList<>(waveforms);
        }

        public float[] getXnorth() {
            return xnorth;
        }

        public float[] getXeast() {
            return xeast;
        }

        public float getDelta() {
            return delta;
        }
        
    }

    public int getDetectionID() {
        for (CorrelationComponent comp : elements) {
            return (int) comp.getEvent().getEvid();
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
