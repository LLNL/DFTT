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
package llnl.gnem.apps.detection.sdBuilder.stackViewer;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import llnl.gnem.apps.detection.sdBuilder.ChannelCombo;
import llnl.gnem.apps.detection.sdBuilder.actions.ComputeCorrelationsAction;
import llnl.gnem.apps.detection.sdBuilder.actions.NextCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.PreviousCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.ShowOrHideCorrelationWindowAction;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.picking.DetectionPhasePickModel;
import llnl.gnem.apps.detection.sdBuilder.picking.MultiOperationApplier;
import llnl.gnem.apps.detection.sdBuilder.stackViewer.StackModel.StackData;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.SeismogramViewer;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.gui.plotting.PickCreationInfo;
import llnl.gnem.dftt.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.dftt.core.gui.plotting.VertAlignment;
import llnl.gnem.dftt.core.gui.plotting.ZoomType;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.PickErrorChangeState;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.XAxis;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.ZoomInStateChange;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.ZoomOutStateChange;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Line;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.util.PairT;
import llnl.gnem.dftt.core.util.SeriesMath;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.waveform.BaseTraceData;

/**
 *
 * @author vieceli1
 */
public class StackViewer extends JMultiAxisPlot implements Observer, SeismogramViewer {

    private static final long serialVersionUID = 7573276550349491357L;

    private JSubplot subplot;
    private final Preferences viewerPrefs;
    private final Map<String, VPickLine> stackPickLines;
    private final Map<Line, BaseTraceData> lineStackMap;
    private final Map<VPickLine, BaseTraceData> vplTraceDataMap;
    private final Map<Line, StackModel.StackData> lineStackDataMap;
    private VPickLine corrWindowPickLine;
    private VPickLine fkWindowPickLine;
    private boolean allowAutoProcessing = true;

    public StackViewer() {
        XAxis axis = this.getXaxis();
        axis.setLabelText("Seconds Relative to Pick");
        boolean isUseZoomBox = true;
        ZoomType zoomType = isUseZoomBox ? ZoomType.ZOOM_BOX : ZoomType.ZOOM_ALL;
        setZoomType(zoomType);

        subplot = addSubplot();

        addPlotObjectObserver(this);
        viewerPrefs = Preferences.userNodeForPackage(getClass());
        stackPickLines = new HashMap<>();
        lineStackMap = new HashMap<>();
        vplTraceDataMap = new HashMap<>();
        lineStackDataMap = new HashMap<>();

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                java.awt.event.InputEvent.CTRL_DOWN_MASK),
                "nextGroup");
        getActionMap().put("nextGroup", NextCorrelationAction.getInstance(this));

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                java.awt.event.InputEvent.CTRL_DOWN_MASK),
                "previousGroup");
        getActionMap().put("previousGroup", PreviousCorrelationAction.getInstance(this));

    }

    @Override
    public void clear() {
        super.clear();
        subplot = null;
        corrWindowPickLine = null;
        fkWindowPickLine = null;
        stackPickLines.clear();
        lineStackMap.clear();
        vplTraceDataMap.clear();
        lineStackDataMap.clear();

        repaint();
    }

    @Override
    public void dataWereLoaded(boolean replotData) {
        getPlotRegion().setBackgroundColor(Color.white);
        clear();
        subplot = addSubplot();

        double prePickSeconds = ParameterModel.getInstance().getPrepickSeconds();
        double centerValue = 0;
        double overlap = 10.0; // percent overlap
        double shift = 1 - overlap / 100.0;
        double minStart = Double.MAX_VALUE;
        double maxEnd = -minStart;

        for (StreamKey key : StackModel.getInstance().getElementKeys()) {
            StackData stackData = StackModel.getInstance().getStackData(key);
            BaseTraceData data = stackData.getData();
            float[] plotData = data.getPlotData();
            double start = -prePickSeconds;
            double endTime = start + (plotData.length - 1) * data.getDelta();
            centerValue += shift;
            scaleAndShiftTrace(centerValue, plotData, plotData);
            StreamKey akey = (StreamKey) ChannelCombo.getInstance().getSelectedItem();
            Color lineColor = key.equals(akey) ? Color.blue : Color.GRAY;
            Line line = new Line(-prePickSeconds, data.getDelta(), plotData);
            lineStackMap.put(line, data);
            lineStackDataMap.put(line, stackData);
            line.setColor(lineColor);
            subplot.AddPlotObject(line);
            line.setSelectable(true);
            if (start < minStart) {
                minStart = start;
            }
            if (endTime > maxEnd) {
                maxEnd = endTime;
            }
            plotLeftText(subplot, key.toString(), centerValue - 0.15);
        }

        subplot.setXlimits(minStart, maxEnd);
        double minY = 0;
        double maxY = centerValue + shift;
        subplot.getYaxis().setMin(minY);
        subplot.getYaxis().setMax(maxY);
        subplot.getYaxis().setVisible(false);
        addCorrelationWindow();
        addfkWindow();
        setAllXlimits();
        if (replotData && ParameterModel.getInstance().isAutoZoomEnabled()) {
            Double min = ParameterModel.getInstance().getPostCorrWindowStart();
            Double max = ParameterModel.getInstance().getPostCorrWindowEnd();
            if (min != null && max != null) {
                this.zoomToNewXLimits(min, max);
            }
        }
        setMouseMode(MouseMode.SELECT_ZOOM);
        repaint();
        allowAutoProcessing = true;
    }

    private void addCorrelationWindow() {
        double start = ParameterModel.getInstance().getWindowStart();
        corrWindowPickLine = new VPickLine(start, 1.0, "");
        double duration = ParameterModel.getInstance().getCorrelationWindowLength();
        corrWindowPickLine.getWindow().setDuration(duration);
        corrWindowPickLine.setVisible(true);
        corrWindowPickLine.getWindow().setCanDragX(true);
        corrWindowPickLine.getWindowHandle().setCanDragX(true);
        corrWindowPickLine.getWindow().setRightHandleFractionalWidth(1.0);
        corrWindowPickLine.getWindowHandle().setWidth(3);
        boolean visible = ParameterModel.getInstance().isShowCorrelationWindow();
        corrWindowPickLine.getWindow().setVisible(visible);

        subplot.AddPlotObject(corrWindowPickLine);
    }

    private void addfkWindow() {
        double start = ParameterModel.getInstance().getWindowStart();
        FKWindowParams.getInstance().setWindowStart(start);
        fkWindowPickLine = new VPickLine(start, 0.9, "FK");
        double duration = FKWindowParams.getInstance().getFkWindowLength();
        fkWindowPickLine.getWindow().setDuration(duration);
        fkWindowPickLine.getWindow().setVisible(true);
        fkWindowPickLine.getWindow().setCanDragX(true);
        fkWindowPickLine.getWindowHandle().setCanDragX(true);
        fkWindowPickLine.getWindow().setRightHandleFractionalWidth(1.0);
        fkWindowPickLine.getWindowHandle().setWidth(2);
        fkWindowPickLine.getWindow().setVisible(FKWindowParams.getInstance().isShowFKWindow());
        subplot.AddPlotObject(fkWindowPickLine);
    }

    @Override
    public void updateForFailedCorrelation() {
        // Do nothing
    }

    @Override
    public void loadClusterResult() {
        dataWereLoaded(true);
    }

    @Override
    public void updateForChangedTrace() {
        dataWereLoaded(true);
    }

    @Override
    public void setMouseMode(MouseMode mouseMode) {
        super.setMouseMode(mouseMode);
    }

    @Override
    public void maybeHighlightTrace(CorrelationComponent cc) {
        // Do nothing yet
    }

    @Override
    public void adjustWindow(double windowStart, double winLen) {
        // Do nothing yet
    }

    private static void scaleAndShiftTrace(double centerValue, float[] traceToMeasure, float[] traceToModify) {
        double minVal = Double.MAX_VALUE;
        double maxVal = -minVal;
        for (float value : traceToMeasure) {
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

    private void plotLeftText(JSubplot subplot, String label, double verticalPos) {

        XPinnedText text = new XPinnedText(5, verticalPos + 0.05, label);
        text.setVerticalAlignment(VertAlignment.BOTTOM);

        subplot.AddPlotObject(text);

    }

    public void magnifyTraces() {
        scaleTraces(2.0);

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

        this.repaint();

    }

    public void reduceTraces() {
        scaleTraces(0.5);
    }

    @Override
    public void update(Observable o, Object obj) {
        if (obj instanceof PlotObjectClicked) {
            PlotObjectClicked poc = (PlotObjectClicked) obj;
            if (poc.mode == MouseMode.WINDOW) {
                Point point = poc.me.getPoint();
                PairT<Double, Double> coords = pointToCoordinate(point);
                if (allowAutoProcessing) {
                    allowAutoProcessing = false;
                    MultiOperationApplier.getInstance().performOperations(coords.getFirst());
                }
            }
            if (poc.po instanceof Line) {
            }

        } else if (obj instanceof WindowDurationChangedState) {
            WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
            double delta = wdcs.getDeltaD();
            if (wdcs.getWindowHandle().getAssociatedPick() == corrWindowPickLine) {
                double newLength = ParameterModel.getInstance().getCorrelationWindowLength() + delta;
                ClusterBuilderFrame.getInstance().setCorrelationWindowLength(newLength);
                ParameterModel.getInstance().setCorrelationWindowLength(newLength);
            } else if (wdcs.getWindowHandle().getAssociatedPick() == fkWindowPickLine) {
                double newLength = FKWindowParams.getInstance().getFkWindowLength() + delta;
                FKWindowParams.getInstance().setFkWindowLength(newLength);
            }
        } else if (obj instanceof MouseMode) {
            // System.out.println( "owner.setMouseModeMessage((MouseMode) obj);");
        } else if (obj instanceof MouseWheelEvent) {
            MouseWheelEvent mwe = (MouseWheelEvent) obj;
            int rotation = mwe.getWheelRotation();
            if (rotation < 0) {
                ClusterBuilderFrame.getInstance().magnifyTraces();
            } else {
                ClusterBuilderFrame.getInstance().reduceTraces();
            }
        } else if (obj instanceof JPlotKeyMessage) {
            JPlotKeyMessage msg = (JPlotKeyMessage) obj;
            KeyEvent e = msg.getKeyEvent();

            int down = KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK;
            if ((e.getModifiersEx() & down) == down && (e.getKeyCode() == KeyEvent.VK_E || e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_Z)) {
                ChannelCombo.getInstance().changeChannel(e.getKeyCode());
            }

            switch (e.getKeyChar()) {
                case '+': {
                    zoomInAroundMouse(msg);
                    return;
                }
                case KeyEvent.VK_TAB: {
                    if (NextCorrelationAction.getInstance(this).isEnabled()) {
                        NextCorrelationAction.getInstance(this).actionPerformed(null);
                    }
                    return;
                }
                case 'q':
                case 'Q': {
                    if (PreviousCorrelationAction.getInstance(this).isEnabled()) {
                        PreviousCorrelationAction.getInstance(this).actionPerformed(null);
                        return;
                    }
                }
                case 'f':
                case 'F': {
                    ClusterBuilderFrame.getInstance().applyCurrentFilter();
                    break;
                }
                case 'u':
                case 'U': {
                    CorrelatedTracesModel.getInstance().unApplyFilter();
                    break;
                }
                case 'c':
                case 'C': {
                    ComputeCorrelationsAction.getInstance(this).actionPerformed(null);
                    break;
                }
                case 'w':
                case 'W':
                    ShowOrHideCorrelationWindowAction.getInstance(this).actionPerformed(null);
                    break;

                case 'r':
                case 'R':
                    ClusterBuilderFrame.getInstance().classifyAndNext("r", true);
                    break;
                case 'l':
                case 'L':
                    ClusterBuilderFrame.getInstance().classifyAndNext("l", true);
                    break;

            }

        } else if (obj instanceof ZoomInStateChange) {
//            ZoomInStateChange zisc = (ZoomInStateChange) obj;
//            this.getSubplotManager().zoomToBox(zisc.getZoomBounds());
        } else if (obj instanceof ZoomOutStateChange) {
//            this.getSubplotManager().UnzoomAll();
        } else if (obj instanceof PickMovedState) {
            PickMovedState pms = (PickMovedState) obj;
            double deltaT = pms.getDeltaT();
            String phase = pms.getPickLine().getText();
            VPickLine vpl = pms.getPickLine();

            if (corrWindowPickLine != null && vpl == corrWindowPickLine) {
                ParameterModel.getInstance().adjustWindowStart(deltaT);
                double newStart = ParameterModel.getInstance().getWindowStart();
                ClusterBuilderFrame.getInstance().setCorrelationWindowStart(newStart);
            } else if (fkWindowPickLine != null && vpl == fkWindowPickLine) {
                FKWindowParams.getInstance().adjustWindowStart(deltaT);
            } else {
                BaseTraceData btd = vplTraceDataMap.get(vpl);
                if (btd != null) {
                    DetectionPhasePickModel.getInstance().adjustAllPickTimesForPhase(phase, deltaT, btd.getStreamKey());
                }
            }
        } else if (obj instanceof PickCreationInfo) {
            PickCreationInfo pci = (PickCreationInfo) obj;
            PlotObject po = pci.getSelectedObject();
            if (po instanceof Line) {
                Line line = (Line) po;
                BaseTraceData btd = lineStackMap.get(line);
                StackData stackData = lineStackDataMap.get(line);
                if (btd != null && stackData != null) {
                    Coordinate coord = pci.getCoordinate();
                    double pointerXvalue = coord.getWorldC1();
                    double pickStd = btd.estimatePickStdErr(pointerXvalue);
                    Collection<CorrelationComponent> channels = stackData.getInputStackData().getStackData();
                    DetectionPhasePickModel.getInstance().createMultiplePicksForCurrentPhase(pointerXvalue, pickStd,
                            btd.getStreamKey(), channels);
                    this.setMouseMode(MouseMode.SELECT_ZOOM);

                    Color pickColor = prefs.getPickPrefs().getColor();
                    int pickWidth = prefs.getPickPrefs().getWidth();
                    boolean draggable = true;
                    int textSize = prefs.getPickPrefs().getTextSize();
                    boolean showErrorBars = true;
                    String phase = DetectionPhasePickModel.getInstance().getCurrentPhase();
                    VPickLine stackPickLine = stackPickLines.get(phase);
                    if (stackPickLine == null) {
                        stackPickLine = new VPickLine(pointerXvalue, 0.9, phase, pickColor, pickWidth, draggable,
                                textSize, prefs.getPickPrefs().getTextPosition());
                        stackPickLine.setStd(pickStd);
                        stackPickLine.setShowErrorBars(showErrorBars);
                        stackPickLine.setPenStyle(prefs.getPickPrefs().getPenStyle());
                        subplot.AddPlotObject(stackPickLine);
                        stackPickLines.put(phase, stackPickLine);
                        vplTraceDataMap.put(stackPickLine, btd);
                    }

                }
            }
            repaint();
        } else if (obj instanceof PickErrorChangeState) {
            PickErrorChangeState pecs = (PickErrorChangeState) obj;
            VPickLine vpl = pecs.getPickLine();
            double deltaT = pecs.getDeltaStd();
            String phase = vpl.getText();
            BaseTraceData btd = vplTraceDataMap.get(vpl);
            if (btd != null) {
                DetectionPhasePickModel.getInstance().adjustAllPickStdValuesForPhase(phase, deltaT, btd.getStreamKey());
            }
        }
    }

    @Override
    public void displayAllPicks() {
        // Do nothing
    }

    @Override
    public void clearAllPicks() {
        for (VPickLine vpl : stackPickLines.values()) {
            subplot.DeletePlotObject(vpl);
        }
        stackPickLines.clear();
        repaint();
    }

    public void setCorrelationWindowLength(double duration) {
        if (corrWindowPickLine != null) {
            corrWindowPickLine.getWindow().setDuration(duration);
        }
        repaint();
    }

    public void setCorrelationWindowStart(double newStart) {
        if (corrWindowPickLine != null) {
            corrWindowPickLine.setXval(newStart);
        }
        repaint();
    }

    public void setCorrelationWindowVisible(boolean value) {
        if (corrWindowPickLine != null) {
            corrWindowPickLine.getWindow().setVisible(value);
        }
        repaint();
    }

    public void setfkWindowVisible(boolean value) {
        if (fkWindowPickLine != null) {
            fkWindowPickLine.getWindow().setVisible(value);
        }
        repaint();
    }

}
