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

import llnl.gnem.core.gui.plotting.Limits;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickErrorChangeState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.core.gui.plotting.ZoomType;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.XAxis;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.core.correlation.CorrelationTraceData;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.JPopupMenu;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PinnedText;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.plotting.HorizAlignment;
import llnl.gnem.core.gui.plotting.HorizPinEdge;
import llnl.gnem.core.gui.plotting.VertPinEdge;
import llnl.gnem.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class ArrayDisplayViewer extends JMultiAxisPlot implements Observer {
    
    private JSubplot subplot;
    private final Collection<PinnedText> pinnedText;
    private final Map<Line, CorrelationComponent> lineCompMap;
    private final Map<CorrelationComponent, Line> compLineMap;
    private final JPopupMenu traceMenu;
    private VPickLine corrWindowPickLine;
    private JSubplot stackSubPlot;
    private VPickLine stackPickLine;
    
    public ArrayDisplayViewer() {
        lineCompMap = new HashMap<>();
        compLineMap = new HashMap<>();
        XAxis axis = this.getXaxis();
        axis.setLabelText("Seconds Relative to Pick");
        pinnedText = new ArrayList<>();
        boolean isUseZoomBox = true;
        ZoomType zoomType = isUseZoomBox ? ZoomType.ZOOM_BOX : ZoomType.ZOOM_ALL;
        setZoomType(zoomType);
        
        subplot = addSubplot();
        
        traceMenu = new JPopupMenu();
 //       JMenuItem item = new JMenuItem(RemoveComponentAction.getInstance(this));
 //       traceMenu.add(item);
        addPlotObjectObserver(this);
        
    }
    
    @Override
    public void clear() {
        super.clear();
        subplot = null;
        stackSubPlot = null;
        pinnedText.clear();
        lineCompMap.clear();
        compLineMap.clear();
        updateButtonStates();
        getPlotRegion().setBackgroundColor(Color.white);
        repaint();
    }
    
    public void dataWasLoaded(boolean replotData, boolean plotStack) {
        getPlotRegion().setBackgroundColor(Color.white);
        Limits xLimits = null;
        if (replotData) {
            xLimits = subplot.getLastXLimits();
        }
        clear();
        subplot = addSubplot();
        
        Collection<CorrelationComponent> data = ArrayDisplayModel.getInstance().getTraces();
        if (data.isEmpty()) {
            return;
        }
        double centerValue = 0;
        double overlap = 10.0; // percent overlap
        double shift = 1 - overlap / 100.0;
        double minStart = Double.MAX_VALUE;
        double maxEnd = -minStart;
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
            float[] plotData = td.getPlotData();
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double endTime = start + (plotData.length - 1) * td.getDelta();
            centerValue += shift;
            scaleAndShiftTrace(centerValue, plotData, plotData);
            Line line = new Line(start, td.getDelta(), plotData);
            
            subplot.AddPlotObject(line);
            line.setSelectable(true);
            addNominalPick(cc, centerValue);
            lineCompMap.put(line, cc);
            compLineMap.put(cc, line);
            if (start < minStart) {
                minStart = start;
            }
            if (endTime > maxEnd) {
                maxEnd = endTime;
            }
            
            plotAllText(cc, centerValue);
        }
        
        
        addCorrelationWindow();
        
        subplot.setXlimits(minStart, maxEnd);
        double minY = 0;
        double maxY = centerValue + shift;
        subplot.getYaxis().setMin(minY);
        subplot.getYaxis().setMax(maxY);
        subplot.getYaxis().setVisible(false);
        
        if (replotData && plotStack) {
            plotStack();
        }
        
        setAllXlimits();
        
        
        if (replotData && xLimits != null) {
            this.zoomToNewXLimits(xLimits.getMin(), xLimits.getMax());
        } else {
//            double prePick = CorrelationProcessingParams.getInstance().getPrePickSeconds();
//            double postPick = CorrelationProcessingParams.getInstance().getPostPickSeconds();
//            double duration = postPick + prePick;
//            double center = (postPick - prePick) / 2;
//            double begin = center - 2 * duration;
//            double end = center + 2 * duration;
//            zoomToNewXLimits(begin, end);
        }
        
        
        repaint();
        
    }
    
    private void plotStack() {
 /*       if (stackSubPlot != null) {
            getSubplotManager().RemovePlot(stackSubPlot);
        }
        stackSubPlot = addSubplot();
        stackSubPlot.getPlotRegion().setDrawBox(true);
        setplotSpacing(2);
        CorrelationTraceData td = ArrayDisplayModel.getInstance().getCorrelationStack();
        float[] plotData = td.getPlotData();
        
        double traceStart = td.getTime().getEpochTime();
        double nominalPickTime = td.getNominalPick().getTime();
        double start = traceStart - nominalPickTime;
        
        stackSubPlot.Plot(start, td.getDelta(), plotData);
        
        
        NominalArrival arrival = td.getNominalPick();
        String phase = arrival.getPhase();
        
        double pickTime = arrival.getTime();
        String label = String.format("%s (Corr)", phase);
        
        stackPickLine = new VPickLine(pickTime, 0.6, label);
        stackPickLine.setColor(Color.black);
        stackPickLine.setSelectable(true);
        stackPickLine.setAllDraggable(true);
        stackPickLine.setErrorBarShowHandles(true);
        stackPickLine.setShowErrorBars(true);
        double std = stackPickLine.getStd();
        ArrayDisplayModel.getInstance().setStackPickStd(std);
        stackSubPlot.AddPlotObject(stackPickLine); */
    }
    
    private void plotAllText(CorrelationComponent component, double verticalPosition) {
        String text = String.format("%s DETID: %d",
                component.getCorrelationTraceData().getName(), component.getEvent().getEvid());
        
        plotLeftText(subplot, text, verticalPosition);
        double correlation = component.getCorrelation();
        double shift = component.getShift();
        if (correlation > 0) {
            text = String.format("CC = %5.3f, shift = %6.3f s", correlation, shift);
            plotLeftText(subplot, text, verticalPosition - 0.3);
        }
        
    }
    
    private void plotLeftText(JSubplot subplot, String label, double verticalPos) {
        
        XPinnedText text = new XPinnedText(5, verticalPos + 0.05, label);
        text.setVerticalAlignment(VertAlignment.BOTTOM);
        
        subplot.AddPlotObject(text);
        
    }
    
    public void magnifyTraces() {
        scaleTraces(2.0);
        repaint();
    }
    
    private void scaleTraces(double factor) {
        Line[] lines = subplot.getLines();
        for (Line line : lines) {
            float[] data = line.getYdata();
            double mean = SeriesMath.getMean(data);
            SeriesMath.RemoveMean(data);
            SeriesMath.MultiplyScalar(data, factor);
            data = SeriesMath.Add(data, mean);
            line.replaceYarray(data);
        }
        if (stackSubPlot != null) {
            lines = stackSubPlot.getLines();
            for (Line line : lines) {
                float[] data = line.getYdata();
                double mean = SeriesMath.getMean(data);
                SeriesMath.RemoveMean(data);
                SeriesMath.MultiplyScalar(data, factor);
                line.replaceYarray(data);
            }
            this.repaint();
        }
    }
    
    public void reduceTraces() {
        scaleTraces(0.5);
        repaint();
    }
    
    @Override
    public void scaleAllTraces(boolean resetYlimits) {
        double xmin = getXaxis().getMin();
        double xmax = getXaxis().getMax();
        Line[] lines = subplot.getLines();
        double centerValue = 0;
        double overlap = 10.0; // percent overlap
        double shift = 1 - overlap / 100.0;
        for (Line line : lines) {
            float[] data = line.getYdata();
            int idx1 = 0;
            int idx2 = data.length - 1;
            
            double delta = line.getIncrement();
            double lineStartTime = line.getXBegin();
            if (xmin > lineStartTime) {
                idx1 = (int) (Math.round(xmin - lineStartTime) / delta);
            }
            if (xmax < line.getXEnd()) {
                idx2 = (int) (Math.round(xmax - lineStartTime) / delta);
            }
            if (idx2 > idx1 + 1) {
                float[] cut = new float[idx2 - idx1 + 1];
                System.arraycopy(data, idx1, cut, 0, cut.length);
                centerValue += shift;
                scaleAndShiftTrace(centerValue, cut, data);
            }
        }
        
        if (stackSubPlot != null) {
            lines = stackSubPlot.getLines();
            for (Line line : lines) {
                float[] data = line.getYdata();
                int idx1 = 0;
                int idx2 = data.length - 1;
                
                double delta = line.getIncrement();
                double lineStartTime = line.getXBegin();
                if (xmin > lineStartTime) {
                    idx1 = (int) (Math.round(xmin - lineStartTime) / delta);
                }
                if (xmax < line.getXEnd()) {
                    idx2 = (int) (Math.round(xmax - lineStartTime) / delta);
                }
                float[] cut = new float[idx2 - idx1 + 1];
                System.arraycopy(data, idx1, cut, 0, cut.length);
                scaleAndShiftTrace(0, cut, data);
            }
        }
    }
    
    public void updateButtonStates() {
//        OpenFilterDialogAction.getInstance(this).setEnabled(DataModel.getInstance().hasData());
    }
    
    @Override
    public void update(Observable o, Object obj) {
        if (obj instanceof PlotObjectClicked) {
            PlotObjectClicked poc = (PlotObjectClicked) obj;
            if (poc.po instanceof Line) {
                CorrelationComponent cc = lineCompMap.get((Line) poc.po);
                if (cc != null) {
//                    RemoveComponentAction.getInstance(this).setComponent(cc);
                    if (poc.me.getButton() == MouseEvent.BUTTON3) {
                        traceMenu.show(poc.me.getComponent(),
                                poc.me.getX(), poc.me.getY());
                    }
                }
            }
            
        } else if (obj instanceof PickMovedState) {
            PickMovedState pms = (PickMovedState) obj;
            double delta = pms.getDeltaT();
            VPickLine vpl = pms.getPickLine();
            if (vpl == corrWindowPickLine) {
                ArrayDisplayModel.getInstance().adjustWindowStart(delta);
            } else {
//                ArrayDisplayModel.getInstance().addAnalystShift(delta);
            }
        } else if (obj instanceof PickErrorChangeState) {
            PickErrorChangeState pecs = (PickErrorChangeState) obj;
//            ArrayDisplayModel.getInstance().adjustAnalystShiftStd(pecs.getDeltaStd());
            pecs.getDeltaStd();
        } else if (obj instanceof WindowDurationChangedState) {
            WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
            if (wdcs.getWindowHandle().getAssociatedPick() == corrWindowPickLine) {
                double delta = wdcs.getDeltaD();
                ArrayDisplayModel.getInstance().adjustWindowDuration(delta);
            }
        } else if (obj instanceof MouseMode) {
            // System.out.println( "owner.setMouseModeMessage((MouseMode) obj);");
        } else if (obj instanceof JPlotKeyMessage) {
            JPlotKeyMessage msg = (JPlotKeyMessage) obj;
            KeyEvent e = msg.getKeyEvent();
            //       ControlKeyMapper controlKeyMapper = msg.getControlKeyMapper();
            int keyCode = e.getKeyCode();
            
            PlotObject po = msg.getPlotObject();
            if (keyCode == 127 && po instanceof Line) {
                CorrelationComponent cc = lineCompMap.get((Line) po);
                if (cc != null) {
//                    RemoveComponentAction.getInstance(this).setComponent(cc);
//                    RemoveComponentAction.getInstance(this).actionPerformed(null);
                }
            }
            
            
        } /*else if (obj instanceof ZoomInStateChange) {
            ZoomInStateChange zisc = (ZoomInStateChange) obj;
            this.getSubplotManager().zoomToBox(zisc.getZoomBounds());
        } else if (obj instanceof ZoomOutStateChange) {
            this.getSubplotManager().UnzoomAll();
        }*/
    }
    
    public static void scaleAndShiftTrace(double centerValue, float[] traceToMeasure, float[] traceToModify) {
        double minVal = Double.MAX_VALUE;
        double maxVal = -minVal;
        for (int j = 0; j < traceToMeasure.length; ++j) {
            float value = traceToMeasure[j];
            if (value > maxVal) {
                maxVal = value;
            }
            if (value < minVal) {
                minVal = value;
            }
        }
        double range = maxVal - minVal;
        double oldMean = SeriesMath.getMean(traceToModify);
        for (int j = 0; j < traceToModify.length; ++j) {
            traceToModify[j] = (float) ((traceToModify[j] - oldMean) / range + centerValue);
        }
    }
    
    private void addNominalPick(CorrelationComponent cc, double centerValue) {
        CorrelationTraceData td = (CorrelationTraceData) cc.getCorrelationTraceData();
        NominalArrival arrival = td.getNominalPick();
        String phase = arrival.getPhase();
        String auth = arrival.getAuth();
        double pickTime = cc.getShift();
        String label = String.format("%s", phase);
        if (auth != null) {
            int arid = (int)(long)arrival.getArid();
            
            label = String.format("%s (%s - %d)", phase, auth, arid);
        }
        VPickLine vpl = new VPickLine(pickTime, centerValue, 20.0, label);
        vpl.setColor(Color.black);
        vpl.setSelectable(false);
        subplot.AddPlotObject(vpl);
        ArrayDisplayModel.getInstance().setWindowStart(arrival.getTime());
    }
    
    private void addCorrelationWindow() {
        corrWindowPickLine = new VPickLine(0.0, 1.0, "");
        double duration = ParameterModel.getInstance().getCorrelationWindowLength();
        ArrayDisplayModel.getInstance().setWindowDuration(duration);
        corrWindowPickLine.getWindow().setDuration(duration);
        corrWindowPickLine.getWindow().setVisible(true);
        corrWindowPickLine.getWindow().setCanDragX(true);
        corrWindowPickLine.getWindowHandle().setCanDragX(true);
        corrWindowPickLine.getWindow().setRightHandleFractionalWidth(1.0);
        corrWindowPickLine.getWindowHandle().setWidth(3);
        subplot.AddPlotObject(corrWindowPickLine);
    }
    
    void updateForChangedTrace() {
        replaceLines();
        scaleAllTraces(false);
        repaint();
    }
    
    private void replaceLines() {
        Collection<CorrelationComponent> data = ArrayDisplayModel.getInstance().getTraces();
        for (CorrelationComponent cc : data) {
            Line line = compLineMap.get(cc);
            if (line != null) {
                float[] newData = cc.getCorrelationTraceData().getPlotData();
                line.replaceYarray(newData);
            }
        }
/*        
        if (stackSubPlot != null) {
            CorrelationTraceData td = ArrayDisplayModel.getInstance().getCorrelationStack();
            float[] plotData = td.getPlotData();
            
            Line[] lines = stackSubPlot.getLines();
            if (lines.length == 1) {
                lines[0].replaceYarray(plotData);
            }
        }
*/
    }
    
    void loadClusterResult() {
        dataWasLoaded(true, true);
    }
    
    void updateForFailedCorrelation() {
        String textString = "No Groups Built!";
        PinnedText ptext = new PinnedText(75.0, 100, textString, HorizPinEdge.LEFT, VertPinEdge.TOP, "Arial", 72.0, new Color(0, 0, 0, 50), HorizAlignment.LEFT, VertAlignment.CENTER);
        subplot.AddPlotObject(ptext);
        getPlotRegion().setBackgroundColor(new Color(255, 200, 200));
        
        repaint();
    }
}
