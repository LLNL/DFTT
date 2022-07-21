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

import java.awt.Color;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.CorrelationTraceData;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.waveform.qc.DataSpike;
import llnl.gnem.core.waveform.qc.SpikeProcessor;

/**
 *
 * @author dodge1
 */
public class SingleDetectionWaveformView extends JMultiAxisPlot implements SingleDetectionView {

    private static final long serialVersionUID = -5865937208714595502L;

    private JSubplot subplot;

    public SingleDetectionWaveformView() {
        this.getSubplotManager().setplotSpacing(0);
    }

    @Override
    public void clear() {
        super.clear();
        subplot = null;

        repaint();
    }

    @Override
    public void traceWasAdded() {
        this.clear();
        clear();
        subplot = addSubplot();
        CorrelationComponent cc = SingleDetectionModel.getInstance().getCorrelationComponent();
        CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();

        Collection<DataSpike> spikes = new SpikeProcessor(25.0, 6.0, 20).scanForSpikes(td.getBackupSeismogram());
        float[] plotData = td.getPlotData();
        double traceStart = td.getTime().getEpochTime();
        double nominalPickTime = td.getNominalPick().getTime();
        double ccShift = cc.getShift();
        double start = traceStart - nominalPickTime + ccShift;
        double endTime = start + (plotData.length - 1) * td.getDelta();
        subplot.Plot(start, td.getDelta(), plotData);
        addNominalPick(cc);
        addCorrelationWindow();
        for (DataSpike ds : spikes) {
            double spikeStart = ds.getEpoch().getStart() - traceStart + start;
            double qual = ds.getLocalZScore();
            VPickLine vpl = new VPickLine(spikeStart, 0.7, String.format("Spike with quality %4.2f", qual));
            subplot.AddPlotObject(vpl);
        }
    }

    private void addNominalPick(CorrelationComponent cc) {
        CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
        NominalArrival arrival = td.getNominalPick();
        String phase = arrival.getPhase();
        String auth = arrival.getAuth();
        double pickTime = cc.getShift();
        String label = String.format("%s", phase);
        if (auth != null) {
            label = String.format("%s (%s)", phase, auth);
        }
        VPickLine vpl = new VPickLine(pickTime, 0.8, label);
        vpl.setColor(Color.black);
        vpl.setSelectable(false);
        subplot.AddPlotObject(vpl);
    }

    private void addCorrelationWindow() {
        VPickLine corrWindowPickLine = new VPickLine(0.0, 1.0, "");
        double duration = ParameterModel.getInstance().getCorrelationWindowLength();
        corrWindowPickLine.getWindow().setDuration(duration);
        corrWindowPickLine.getWindow().setVisible(true);
        corrWindowPickLine.getWindow().setCanDragX(true);
        corrWindowPickLine.getWindowHandle().setCanDragX(true);
        corrWindowPickLine.getWindow().setRightHandleFractionalWidth(1.0);
        corrWindowPickLine.getWindowHandle().setWidth(3);
        subplot.AddPlotObject(corrWindowPickLine);
    }

    @Override
    public void setTriggerStatistics(TriggerDataFeatures result) {
        // Do nothing for now...
    }

    @Override
    public void setFeatureValues(String featureName, Collection<Double> result) {
        // Not needed...
    }

    @Override
    public void detectionRetrieved() {
        Detection detection = SingleDetectionModel.getInstance().getDetection();
        if (detection != null) {
            this.getTitle().setText(detection.titleString());
            repaint();
        }
    }

}
