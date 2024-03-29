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
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import llnl.gnem.apps.detection.sdBuilder.RemoveComponentAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DisplayAllStationsAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DisplayArrayAction;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.ChannelCombo;
import llnl.gnem.apps.detection.sdBuilder.actions.ComputeCorrelationsAction;
import llnl.gnem.apps.detection.sdBuilder.actions.NextCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.PreviousCorrelationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.RemoveSinglePickAction;
import llnl.gnem.apps.detection.sdBuilder.actions.ShowOrHideCorrelationWindowAction;
import llnl.gnem.apps.detection.sdBuilder.actions.ShowSNRWindowsAction;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.picking.DetectionPhasePickModel;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePick;
import llnl.gnem.apps.detection.sdBuilder.picking.PredictedPhasePickModel;

import org.apache.batik.svggen.SVGGraphics2DIOException;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.correlation.CorrelationTraceData;
import llnl.gnem.dftt.core.correlation.clustering.GroupData;
import llnl.gnem.dftt.core.correlation.util.NominalArrival;
import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.HorizPinEdge;
import llnl.gnem.dftt.core.gui.plotting.Limits;
import llnl.gnem.dftt.core.gui.plotting.MouseMode;
import llnl.gnem.dftt.core.gui.plotting.MouseOverPlotObject;
import llnl.gnem.dftt.core.gui.plotting.PickCreationInfo;
import llnl.gnem.dftt.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.dftt.core.gui.plotting.PlotObjectClicked.ButtonState;
import llnl.gnem.dftt.core.gui.plotting.VertAlignment;
import llnl.gnem.dftt.core.gui.plotting.VertPinEdge;
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
import llnl.gnem.dftt.core.gui.plotting.plotobject.LineBounds;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PinnedText;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.util.ExceptionDialog;
import llnl.gnem.dftt.core.util.SeriesMath;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class ClusterViewer extends JMultiAxisPlot implements Observer, SeismogramViewer {

    private static enum SortType {
        CC, SNR
    }

    private static final long serialVersionUID = -6780212745942796784L;

    private JSubplot subplot;
    private final Collection<PinnedText> pinnedText;
    private final Map<Line, CorrelationComponent> lineCompMap;
    private final Map<Line, Double> lineCenterValueMap;
    private final Map<CorrelationComponent, Line> compLineMap;
    private final Map<VPickLine, PhasePick> pickLinePickMap;
    private final Map<VPickLine, PredictedPhasePick> pickLinePredPickMap;
    private final Map<PhasePick, VPickLine> detPhasePickVplMap;
    private final JPopupMenu traceMenu;
    private VPickLine corrWindowPickLine;
    private VPickLine snrWindowPickLine;
    private final Preferences viewerPrefs;
    private SortType sortType = SortType.CC;

    public ClusterViewer() {
        lineCompMap = new HashMap<>();
        compLineMap = new HashMap<>();
        pickLinePickMap = new HashMap<>();
        pickLinePredPickMap = new HashMap<>();
        detPhasePickVplMap = new HashMap<>();
        lineCenterValueMap = new HashMap<>();
        XAxis axis = this.getXaxis();
        axis.setLabelText("Seconds Relative to Pick");
        pinnedText = new ArrayList<>();
        boolean isUseZoomBox = true;
        ZoomType zoomType = isUseZoomBox ? ZoomType.ZOOM_BOX : ZoomType.ZOOM_ALL;
        setZoomType(zoomType);

        subplot = addSubplot();

        traceMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem(RemoveComponentAction.getInstance(this));
        traceMenu.add(item);

        item = new JMenuItem(DisplayAllStationsAction.getInstance(this));
        traceMenu.add(item);

        item = new JMenuItem(DisplayArrayAction.getInstance(this));
        traceMenu.add(item);

        item = new JMenuItem(RemoveSinglePickAction.getInstance(this));
        traceMenu.add(item);

        addPlotObjectObserver(this);

        viewerPrefs = Preferences.userNodeForPackage(getClass());

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                java.awt.event.InputEvent.CTRL_DOWN_MASK),
                "nextGroup");
        getActionMap().put("nextGroup", NextCorrelationAction.getInstance(this));

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                java.awt.event.InputEvent.CTRL_DOWN_MASK),
                "previousGroup");
        getActionMap().put("previousGroup", PreviousCorrelationAction.getInstance(this));

    }

    public Collection<CorrelationComponent> getVisibleTraces() {

        ArrayList<CorrelationComponent> result = new ArrayList<>();
        if (subplot != null && lineCenterValueMap != null) {
            double ymin = subplot.getYaxis().getMin();
            double ymax = subplot.getYaxis().getMax();
            for (Line line : lineCenterValueMap.keySet()) {
                double centerValue = lineCenterValueMap.get(line);
                if (centerValue >= ymin && centerValue <= ymax) {
                    CorrelationComponent cc = lineCompMap.get(line);
                    if (cc != null) {
                        result.add(cc);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void clear() {
        super.clear();
        subplot = null;

        pinnedText.clear();
        lineCompMap.clear();
        compLineMap.clear();
        pickLinePickMap.clear();
        pickLinePredPickMap.clear();
        detPhasePickVplMap.clear();
        lineCenterValueMap.clear();
        updateButtonStates();
        getTitle().setText("");
        repaint();
    }

    public void updateTraceColors() {
        Collection<CorrelationComponent> data = CorrelatedTracesModel.getInstance().getMatchingTraces();
        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = cc.getCorrelationTraceData();
            long detectionid = cc.getEvent().getEvid();
            Color color = CorrelatedTracesModel.getInstance().getTriggerClassification((int) detectionid)
                    .getTraceDisplayColor();
            Line line = compLineMap.get(cc);
            if (line != null) {
                line.setColor(color);
            }
        }
        repaint();
    }

    public void sortCurrentData() {
        sortType = SortType.SNR;
        dataWereLoaded(true);
        sortType = SortType.CC;
    }

    @Override
    public void dataWereLoaded(boolean replotData) {
        getPlotRegion().setBackgroundColor(Color.white);
        clear();
        subplot = addSubplot();

        GroupData gd = CorrelatedTracesModel.getInstance().getCurrent();
        Color residualColor = null;
        if (gd != null && gd.isResidualGroup()) {
            residualColor = Color.DARK_GRAY;
        }
        Collection<CorrelationComponent> data = CorrelatedTracesModel.getInstance().getMatchingTraces();
        if (data.isEmpty()) {
            return;
        }
        switch (sortType) {
            case CC:
                data = sortByCC(data);
                break;
            case SNR:
                data = sortBySNR(data);
                break;
        }

        double centerValue = 0;
        double overlap = 10.0; // percent overlap
        double verticalShift = 1 - overlap / 100.0;
        double minStart = Double.MAX_VALUE;
        double maxEnd = -minStart;
        int detectorid = CorrelatedTracesModel.getInstance().getCurrentDetectorid();
        CorrelationComponent component = data.iterator().next();
        String text = String.format(" %d detections for detectorid = %d on %s(%3.0f Hz)",
                data.size(), detectorid, component.getCorrelationTraceData().getName(),
                component.getSeismogram().getSamprate());
        getTitle().setText(text);

        for (CorrelationComponent cc : data) {

            CorrelationTraceData td = cc.getCorrelationTraceData();

            float[] plotData = td.getPlotData();
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double endTime = start + (plotData.length - 1) * td.getDelta();
            centerValue += verticalShift;
            scaleAndShiftTrace(centerValue, plotData, plotData);
            Line line = new Line(start, td.getDelta(), plotData);
            lineCenterValueMap.put(line, centerValue);
            long detectionid = component.getEvent().getEvid();
            Color color = CorrelatedTracesModel.getInstance().getTriggerClassification((int) detectionid)
                    .getTraceDisplayColor();
            if (residualColor != null) {
                color = residualColor;
            }
            line.setColor(color);
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
        addSNRWindow();

        subplot.setXlimits(minStart, maxEnd);
        double minY = 0;
        double maxY = centerValue + verticalShift;
        subplot.getYaxis().setMin(minY);
        subplot.getYaxis().setMax(maxY);
        subplot.getYaxis().setVisible(false);

        setAllXlimits();

        setMouseMode(MouseMode.SELECT_ZOOM);
        displayAllPicks();
        displayPredictedPicks();
        if (replotData && ParameterModel.getInstance().isAutoZoomEnabled()) {
            Double min = ParameterModel.getInstance().getPostCorrWindowStart();
            Double max = ParameterModel.getInstance().getPostCorrWindowEnd();
            if (min != null && max != null) {
                this.zoomToNewXLimits(min, max);
            }
        }

        repaint();

    }

    private void plotAllText(CorrelationComponent component, double verticalPosition) {
        String text = String.format("DETID: %d ", component.getEvent().getEvid());
        plotLeftText(subplot, text, verticalPosition - 0.15);
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
    public void scaleAllTraces(boolean resetYlimits) {
        double xmin = getXaxis().getMin();
        double xmax = getXaxis().getMax();
        Line[] lines = subplot.getLines();
        double centerValue = 0;
        double overlap = 10.0; // percent overlap
        double shift = 1 - overlap / 100.0;
        for (Line line : lines) {
            float[] data = line.getYdata();
            if (data != null && data.length > 1) {
                int idx1 = 0;
                int idx2 = data.length - 1;

                double delta = line.getIncrement();
                double lineStartTime = line.getXBegin();
                if (xmin > lineStartTime) {
                    idx1 = (int) (Math.round(xmin - lineStartTime) / delta);
                }
                if (xmax < line.getXEnd()) {
                    idx2 = Math.min((int) (Math.round(xmax - lineStartTime) / delta), data.length - 1);
                }
                if (idx2 > idx1 + 1) {
                    float[] cut = new float[idx2 - idx1 + 1];
                    System.arraycopy(data, idx1, cut, 0, cut.length);
                    centerValue += shift;
                    scaleAndShiftTrace(centerValue, cut, data);
                }
            }
        }

    }

    public void updateButtonStates() {
//        OpenFilterDialogAction.getInstance(this).setEnabled(DataModel.getInstance().hasData());
    }

    @Override
    public void update(Observable o, Object obj) {
        if (obj instanceof MouseOverPlotObject) {
            MouseOverPlotObject mopo = (MouseOverPlotObject) obj;
            PlotObject po = mopo.getPlotObject();
            if (po instanceof VPickLine) {
                VPickLine vpl = (VPickLine) po;
                PhasePick pp = pickLinePickMap.get(vpl);
                if (pp != null) {
                    setToolTipText(pp.toString());
                }
            }
        } else if (obj instanceof PlotObjectClicked && this.getMouseMode() != MouseMode.CREATE_PICK) {
            PlotObjectClicked poc = (PlotObjectClicked) obj;
            if (poc.po instanceof Line) {
                CorrelationComponent cc = lineCompMap.get(poc.po);
                if (cc != null) {
                    RemoveComponentAction.getInstance(this).setComponent(cc);
                    DisplayArrayAction.getInstance(this).setComponent(cc);
                    DisplayAllStationsAction.getInstance(this).setComponent(cc);
                    DisplayArrayAction.getInstance(this).setEnabled(cc.isArrayComponent());
                    ClusterBuilderFrame.getInstance().setSelectedDetection((int) cc.getEvent().getEvid());

                    if (poc.me.getClickCount() == 2 && poc.me.getButton() == MouseEvent.BUTTON1
                            && poc.buttonState == ButtonState.RELEASED) {
                        CorrelatedTracesModel.getInstance().setSelectedDetection((int) cc.getEvent().getEvid());
                    }
                    if (poc.me.getButton() == MouseEvent.BUTTON3) {
                        traceMenu.show(poc.me.getComponent(),
                                poc.me.getX(), poc.me.getY());
                    }
                }
                setLineSelected((Line) poc.po);
            } else if (poc.po instanceof VPickLine) {
                VPickLine vpl = (VPickLine) poc.po;
                PhasePick dpp = pickLinePickMap.get(vpl);
                if (dpp != null) {
                    RemoveSinglePickAction.getInstance(this).setSelectedPick(dpp);
                    if (poc.me.getButton() == MouseEvent.BUTTON3) {
                        traceMenu.show(poc.me.getComponent(),
                                poc.me.getX(), poc.me.getY());
                    }
                }
            }

        } else if (obj instanceof WindowDurationChangedState) {
            WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
            if (wdcs.getWindowHandle().getAssociatedPick() == corrWindowPickLine) {
                double delta = wdcs.getDeltaD();
                double newLength = ParameterModel.getInstance().getCorrelationWindowLength() + delta;
                ClusterBuilderFrame.getInstance().setCorrelationWindowLength(newLength);
                ParameterModel.getInstance().setCorrelationWindowLength(newLength);
            } else if (wdcs.getWindowHandle().getAssociatedPick() == snrWindowPickLine) {
                double delta = wdcs.getDeltaD();
                ParameterModel.getInstance().adjustSNRWindowLength(delta);
            }
        } else if (obj instanceof MouseMode) {
            // System.out.println( "owner.setMouseModeMessage((MouseMode) obj);");
        } else if (obj instanceof JPlotKeyMessage) {
            JPlotKeyMessage msg = (JPlotKeyMessage) obj;

            KeyEvent e = msg.getKeyEvent();
           int down = KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK;
            if ((e.getModifiersEx() & down) == down && (e.getKeyCode() == KeyEvent.VK_E || e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_Z)) {
                ChannelCombo.getInstance().changeChannel(e.getKeyCode());
            }

            if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
                switch (e.getKeyChar()) {
                    case 'q':
                    case 'Q': {
                        if (PreviousCorrelationAction.getInstance(this).isEnabled()) {
                            PreviousCorrelationAction.getInstance(this).actionPerformed(null);
                        }
                        e.consume();
                    }
                }
                return;
            }
            switch (e.getKeyChar()) {
                case '+': {
                    zoomInAroundMouse(msg);
                    return;
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

            int keyCode = e.getKeyCode();

            PlotObject po = msg.getPlotObject();
            if (keyCode == 127 && po instanceof Line) {
                CorrelationComponent cc = lineCompMap.get(po);
                if (cc != null) {
                    RemoveComponentAction.getInstance(this).setComponent(cc);
                    RemoveComponentAction.getInstance(this).actionPerformed(null);
                }
            }

        } else if (obj instanceof ZoomInStateChange) {
        } else if (obj instanceof ZoomOutStateChange) {
        } else if (obj instanceof PickMovedState) {
            PickMovedState pms = (PickMovedState) obj;
            double deltaT = pms.getDeltaT();
            VPickLine vpl = pms.getPickLine();
            if (corrWindowPickLine != null && vpl == corrWindowPickLine) {
                ParameterModel.getInstance().adjustWindowStart(deltaT);
                double newStart = ParameterModel.getInstance().getWindowStart();
                ClusterBuilderFrame.getInstance().setCorrelationWindowStart(newStart);
            } else if (snrWindowPickLine != null && vpl == snrWindowPickLine) {
                ParameterModel.getInstance().adjustSNRWindowStart(deltaT);
            } else {
                PhasePick dpp = pickLinePickMap.get(vpl);
                if (dpp != null) {
                    DetectionPhasePickModel.getInstance().moveSinglePick(dpp, deltaT);
                }
            }
        } else if (obj instanceof PickCreationInfo) {
            PickCreationInfo pci = (PickCreationInfo) obj;
            PlotObject po = pci.getSelectedObject();
            if (po instanceof Line) {
                Line line = (Line) po;
                // BaseTraceData
                CorrelationComponent cc = lineCompMap.get(line);
                if (cc != null) {
                    Coordinate coord = pci.getCoordinate();
                    double pointerXvalue = coord.getWorldC1();
                    CorrelationTraceData td = cc.getCorrelationTraceData();
                    double nominalPickTime = td.getNominalPick().getTime();
                    double ccShift = cc.getShift();
                    double pickEpochTime = nominalPickTime - ccShift + pointerXvalue;
                    double pickStd = cc.estimatePickStdErr(pickEpochTime);
                    // Do we already have a pick displayed for this component and phase?
                    String phase = DetectionPhasePickModel.getInstance().getCurrentPhase();
                    PhasePick dpp = DetectionPhasePickModel.getInstance().getPickForComponentAndPhase(cc, phase);
                    setMouseMode(MouseMode.SELECT_ZOOM);
                    if (dpp != null) {
                        VPickLine vpl = detPhasePickVplMap.get(dpp);
                        pickLinePickMap.remove(vpl);
                        detPhasePickVplMap.remove(dpp);
                        subplot.DeletePlotObject(vpl);
                    }
                    dpp = DetectionPhasePickModel.getInstance().addSinglePick(cc, pickEpochTime, pickStd,
                            td.getStreamKey());
                    if (dpp != null) {
                        double traceStart = td.getTime().getEpochTime();
                        createSinglePick(dpp, traceStart, ccShift, line);
                    }

                }
            }
        } else if (obj instanceof PickErrorChangeState) {
            PickErrorChangeState pecs = (PickErrorChangeState) obj;
            VPickLine vpl = pecs.getPickLine();
            double deltaT = pecs.getDeltaStd();
            PhasePick dpp = pickLinePickMap.get(vpl);
            if (dpp != null) {
                DetectionPhasePickModel.getInstance().adjustSinglePickStd(dpp, deltaT);
            }
        }
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

    private void addNominalPick(CorrelationComponent cc, double centerValue) {
        CorrelationTraceData td = cc.getCorrelationTraceData();
        NominalArrival arrival = td.getNominalPick();
        String phase = arrival.getPhase();
        String auth = arrival.getAuth();
        double pickTime = cc.getShift();
        String label = String.format("%s", phase);
        if (auth != null) {
            label = String.format("%s (%s)", phase, auth);
        }
        VPickLine vpl = new VPickLine(pickTime, centerValue, 20.0, label);
        vpl.setColor(Color.black);
        vpl.setSelectable(false);
        subplot.AddPlotObject(vpl);
    }

    private void addCorrelationWindow() {

        corrWindowPickLine = new VPickLine(ParameterModel.getInstance().getWindowStart(), 1.0, "");
        double duration = ParameterModel.getInstance().getCorrelationWindowLength();
        corrWindowPickLine.getWindow().setDuration(duration);
        boolean showCorrelationWindow = ParameterModel.getInstance().isShowCorrelationWindow();
        corrWindowPickLine.getWindow().setVisible(showCorrelationWindow);
        corrWindowPickLine.getWindow().setCanDragX(true);
        corrWindowPickLine.getWindowHandle().setCanDragX(true);
        corrWindowPickLine.getWindow().setRightHandleFractionalWidth(1.0);
        corrWindowPickLine.getWindowHandle().setWidth(3);
        subplot.AddPlotObject(corrWindowPickLine);
    }

    public void setCorrelationWindowVisible(boolean value) {
        if (corrWindowPickLine != null) {
            corrWindowPickLine.getWindow().setVisible(value);
        }
        repaint();
    }

    private void addSNRWindow() {

        snrWindowPickLine = new VPickLine(ParameterModel.getInstance().getSNRRefTime(), 1.0, "");
        double duration = ParameterModel.getInstance().getSNRWinLength();
        snrWindowPickLine.getWindow().setDuration(duration);
        boolean showCorrelationWindow = ShowSNRWindowsAction.getInstance(this).isShowSNRWindow();
        snrWindowPickLine.getWindow().setVisible(showCorrelationWindow);
        snrWindowPickLine.getWindow().setCanDragX(true);
        snrWindowPickLine.getWindowHandle().setCanDragX(true);
        snrWindowPickLine.getWindow().setRightHandleFractionalWidth(1.0);
        snrWindowPickLine.getWindowHandle().setWidth(3);
        subplot.AddPlotObject(snrWindowPickLine);
    }

    public void setSNRWindowVisible(boolean value) {
        snrWindowPickLine.getWindow().setVisible(value);
        repaint();
    }

    @Override
    public void updateForChangedTrace() {
        replaceLines();
        scaleAllTraces(false);
        repaint();
    }

    private void replaceLines() {
        Collection<CorrelationComponent> data = CorrelatedTracesModel.getInstance().getMatchingTraces();
        for (CorrelationComponent cc : data) {
            Line line = compLineMap.get(cc);
            if (line != null) {
                float[] newData = cc.getCorrelationTraceData().getPlotData();
                if (newData != null && newData.length == line.length()) {
                    line.replaceYarray(newData);
                }
            }
        }

    }

    @Override
    public void loadClusterResult() {
        dataWereLoaded(true);
    }

    @Override
    public void updateForFailedCorrelation() {
        String textString = "No Groups Built!";
        PinnedText ptext = new PinnedText(75.0, 100, textString, HorizPinEdge.LEFT, VertPinEdge.TOP, "Arial", 72.0,
                new Color(0, 0, 0, 50), HorizAlignment.LEFT, VertAlignment.CENTER);
        subplot.AddPlotObject(ptext);
        getPlotRegion().setBackgroundColor(new Color(255, 200, 200));

        repaint();
    }

    @Override
    public void exportSVG() {
        FileFilter svgFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".svg");
            }

            @Override
            public String getDescription() {
                return "SVG Files";
            }
        };

        String dir = viewerPrefs.get("PLOT_FILE_PATH", "");

        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(svgFilter);
        chooser.setFileFilter(svgFilter);
        if (!dir.isEmpty()) {
            chooser.setCurrentDirectory(new File(dir));
        }
        File saveFile = new File("plot.svg");
        chooser.setSelectedFile(saveFile);
        int rval = chooser.showSaveDialog(null);
        if (rval == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();

            try {
                this.exportSVG(saveFile);
                File file = chooser.getCurrentDirectory();
                viewerPrefs.put("PLOT_FILE_PATH", file.getAbsolutePath());

            } catch (UnsupportedEncodingException | FileNotFoundException | SVGGraphics2DIOException e) {
                ExceptionDialog.displayError(e);
            }
        }
    }

    public Limits getCurrentXLimits() {
        return subplot.getLastXLimits();
    }

    private Collection<CorrelationComponent> sortByCC(Collection<CorrelationComponent> data) {
        Collection<CorrelationComponent> result = new ArrayList<>();
        TreeMap<Double, Collection<CorrelationComponent>> cv = new TreeMap<>();
        for (CorrelationComponent cc : data) {
            Collection<CorrelationComponent> ccc = cv.get(cc.getCorrelation());
            if (ccc == null) {
                ccc = new ArrayList<>();
                cv.put(cc.getCorrelation(), ccc);
            }
            ccc.add(cc);
        }
        for (Double correlation : cv.keySet()) {
            Collection<CorrelationComponent> ccc = cv.get(correlation);
            result.addAll(ccc);
        }

        return result;
    }

    private Collection<CorrelationComponent> sortBySNR(Collection<CorrelationComponent> data) {
        Collection<CorrelationComponent> result = new ArrayList<>();
        TreeMap<Double, Collection<CorrelationComponent>> cv = new TreeMap<>();
        double refTime = ParameterModel.getInstance().getSNRRefTime();
        double winLen = ParameterModel.getInstance().getSNRWinLength();

        for (CorrelationComponent cc : data) {
            CorrelationTraceData td = cc.getCorrelationTraceData();

            float[] plotData = td.getPlotData();
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            int pickIndex = (int) Math.round((refTime - start) * td.getSampleRate());
            int winSamples = (int) Math.round(winLen * td.getSampleRate()) + 1;
            double snr = SeriesMath.getSnr(plotData, td.getSampleRate(), pickIndex, pickIndex - winSamples,
                    pickIndex + pickIndex);

            Collection<CorrelationComponent> ccc = cv.get(snr);
            if (ccc == null) {
                ccc = new ArrayList<>();
                cv.put(snr, ccc);
            }
            ccc.add(cc);
        }
        for (Double correlation : cv.keySet()) {
            Collection<CorrelationComponent> ccc = cv.get(correlation);
            result.addAll(ccc);
        }

        return result;
    }

    private void setLineSelected(Line line) {
        for (Line aline : lineCompMap.keySet()) {
            if (line == aline) {
                line.setColor(Color.red);
            } else {
                aline.setColor(Color.blue);
            }
        }
        repaint();
    }

    @Override
    public void maybeHighlightTrace(CorrelationComponent cc) {
        Line line = compLineMap.get(cc);
        if (line != null) {
            setLineSelected(line);
        }
    }

    @Override
    public void adjustWindow(double windowStart, double winLen) {
        corrWindowPickLine.setXval(windowStart);
        corrWindowPickLine.getWindow().setDuration(winLen);
        repaint();
    }

    @Override
    public void displayAllPicks() {
        // First clear out existing...
        for (VPickLine vpl : pickLinePickMap.keySet()) {
            subplot.DeletePlotObject(vpl);
        }
        pickLinePickMap.clear();
        detPhasePickVplMap.clear();
        Map<CorrelationComponent, Collection<PhasePick>> ccPickMap = DetectionPhasePickModel.getInstance()
                .getAllPicks();
        for (CorrelationComponent cc : ccPickMap.keySet()) {
            StreamKey compKey = cc.getStreamKey();
            Line line = compLineMap.get(cc);
            if (line != null) {

                Collection<PhasePick> picks = ccPickMap.get(cc);
                CorrelationTraceData td = cc.getCorrelationTraceData();
                double ccShift = cc.getShift();
                double traceStart = td.getTime().getEpochTime();
                for (PhasePick dpp : picks) {
                    StreamKey pickKey = dpp.getKey();
                    if (compKey.getSta().equals(pickKey.getSta()) && compKey.getChan().equals(pickKey.getChan())){
                        createSinglePick(dpp, traceStart, ccShift, line);
                    }
                }
            }
        }
        repaint();
    }

    private void createSinglePick(PhasePick dpp, double traceStart, double ccShift, Line line) {
        double x = dpp.getTime() - traceStart - ParameterModel.getInstance().getPrepickSeconds() + ccShift;
        double aheight = 20.0; // millimeters
        double yval = line.getValueAt(x);
        VPickLine vpl = new VPickLine(x, yval, aheight, dpp.getPhase());
        vpl.setColor(Color.black);
        vpl.setSelectable(true);
        vpl.setDraggable(true);
        vpl.setShowErrorBars(true);
        vpl.setStd(dpp.getStd());

        subplot.AddPlotObject(vpl);
        pickLinePickMap.put(vpl, dpp);
        detPhasePickVplMap.put(dpp, vpl);
        repaint();
    }

    public void displayPredictedPicks() {
        // First clear out existing...
        for (VPickLine vpl : pickLinePredPickMap.keySet()) {
            subplot.DeletePlotObject(vpl);
        }
        pickLinePredPickMap.clear();
        Map<CorrelationComponent, Collection<PredictedPhasePick>> ccPickMap = PredictedPhasePickModel.getInstance()
                .getAllPicks();
        for (CorrelationComponent cc : ccPickMap.keySet()) {
            Line line = compLineMap.get(cc);
            if (line != null) {
                Collection<PredictedPhasePick> picks = ccPickMap.get(cc);
                CorrelationTraceData td = cc.getCorrelationTraceData();
                double ccShift = cc.getShift();
                double traceStart = td.getTime().getEpochTime();
                for (PredictedPhasePick dpp : picks) {
                    double x = dpp.getTime() - traceStart - ParameterModel.getInstance().getPrepickSeconds() + ccShift;
                    double aheight = 20.0; // millimeters
                    LineBounds bounds = line.getLineBounds();
                    if (x >= bounds.xmin && x <= bounds.xmax) {
                        double yval = line.getValueAt(x);
                        VPickLine vpl = new VPickLine(x, yval, aheight, dpp.getPhase());
                        vpl.setColor(Color.RED);
                        vpl.setWidth(4);
                        vpl.setSelectable(false);
                        vpl.setDraggable(false);
                        vpl.setShowErrorBars(false);
                        subplot.AddPlotObject(vpl);
                        pickLinePredPickMap.put(vpl, dpp);
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void clearAllPicks() {
        for (VPickLine vpl : pickLinePickMap.keySet()) {
            subplot.DeletePlotObject(vpl);
        }
        pickLinePickMap.clear();
        repaint();
    }

    void setCorrelationWindowLength(double duration) {
        corrWindowPickLine.getWindow().setDuration(duration);
        repaint();
    }

    void setCorrelationWindowStart(double newStart) {
        corrWindowPickLine.setXval(newStart);
        repaint();
    }
}
