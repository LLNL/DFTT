/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.stackViewer;

import llnl.gnem.apps.detection.sdBuilder.picking.DetectionPhasePickModel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import llnl.gnem.apps.detection.sdBuilder.ChannelCombo;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.SeismogramViewer;
import llnl.gnem.core.correlation.CorrelationComponent;

import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.PickCreationInfo;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.ZoomType;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickErrorChangeState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.XAxis;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomInStateChange;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomOutStateChange;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.BaseTraceData;

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

    public StackViewer() {
        XAxis axis = this.getXaxis();
        axis.setLabelText("Seconds Relative to Pick");
        //    pinnedText = new ArrayList<>();
        boolean isUseZoomBox = true;
        ZoomType zoomType = isUseZoomBox ? ZoomType.ZOOM_BOX : ZoomType.ZOOM_ALL;
        setZoomType(zoomType);

        subplot = addSubplot();

        addPlotObjectObserver(this);
        viewerPrefs = Preferences.userNodeForPackage(getClass());
        stackPickLines = new HashMap<>();
        lineStackMap = new HashMap<>();
    }

    @Override
    public void clear() {
        super.clear();
        subplot = null;
        stackPickLines.clear();
        lineStackMap.clear();
        repaint();
    }

    @Override
    public void dataWereLoaded(boolean replotData) {
        getPlotRegion().setBackgroundColor(Color.white);
        clear();
        subplot = addSubplot();

        Map<StreamKey, SingleComponentStack> keyStackMap = CorrelatedTracesModel.getInstance().getKeyStackMap();
        double prePickSeconds = ParameterModel.getInstance().getPrepickSeconds();
        double centerValue = 0;
        double overlap = 10.0; // percent overlap
        double shift = 1 - overlap / 100.0;
        double minStart = Double.MAX_VALUE;
        double maxEnd = -minStart;

        for (StreamKey key : keyStackMap.keySet()) {
            SingleComponentStack stack = keyStackMap.get(key);
            BaseTraceData data = stack.produceStack();
            float[] plotData = data.getPlotData();
            double start = -prePickSeconds;
            double endTime = start + (plotData.length - 1) * stack.getDelta();
            centerValue += shift;
            scaleAndShiftTrace(centerValue, plotData, plotData);
            StreamKey akey = (StreamKey) ChannelCombo.getInstance().getSelectedItem();
            Color lineColor = key.equals(akey) ? Color.blue : Color.GRAY;
            Line line = new Line(-prePickSeconds, stack.getDelta(), plotData);
            lineStackMap.put(line, data);
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
        setAllXlimits();
        setMouseMode(MouseMode.SELECT_ZOOM);
        repaint();

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
            if (poc.po instanceof Line) {
            }

        } else if (obj instanceof WindowDurationChangedState) {
            WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
        } else if (obj instanceof MouseMode) {
            // System.out.println( "owner.setMouseModeMessage((MouseMode) obj);");
        } else if (obj instanceof JPlotKeyMessage) {
            JPlotKeyMessage msg = (JPlotKeyMessage) obj;
            KeyEvent e = msg.getKeyEvent();
            //       ControlKeyMapper controlKeyMapper = msg.getControlKeyMapper();
            int keyCode = e.getKeyCode();

            PlotObject po = msg.getPlotObject();

        } else if (obj instanceof ZoomInStateChange) {
//            ZoomInStateChange zisc = (ZoomInStateChange) obj;
//            this.getSubplotManager().zoomToBox(zisc.getZoomBounds());
        } else if (obj instanceof ZoomOutStateChange) {
//            this.getSubplotManager().UnzoomAll();
        } else if (obj instanceof PickMovedState) {
            PickMovedState pms = (PickMovedState) obj;
            double deltaT = pms.getDeltaT();
            String phase = pms.getPickLine().getText();
            DetectionPhasePickModel.getInstance().adjustAllPickTimesForPhase(phase, deltaT);
        } else if (obj instanceof PickCreationInfo) {
            PickCreationInfo pci = (PickCreationInfo) obj;
            PlotObject po = pci.getSelectedObject();
            if (po instanceof Line) {
                Line line = (Line) po;
                BaseTraceData btd = lineStackMap.get(line);
                if (btd != null) {
                    Coordinate coord = pci.getCoordinate();
                    double pointerXvalue = coord.getWorldC1();
                    double pickStd = btd.estimatePickStdErr(pointerXvalue);
                    DetectionPhasePickModel.getInstance().createPickForCurrentPhase(pointerXvalue, pickStd);
                    this.setMouseMode(MouseMode.SELECT_ZOOM);

                    Color pickColor = prefs.getPickPrefs().getColor();
                    int pickWidth = prefs.getPickPrefs().getWidth();
                    boolean draggable = true;
                    int textSize = prefs.getPickPrefs().getTextSize();
                    boolean showErrorBars = true;
                    String phase = DetectionPhasePickModel.getInstance().getCurrentPhase();
                    VPickLine stackPickLine = stackPickLines.get(phase);
                    if (stackPickLine == null) {
                        stackPickLine = new VPickLine(pointerXvalue, 0.9, phase, pickColor, pickWidth, draggable, textSize, prefs.getPickPrefs().getTextPosition());
                        stackPickLine.setStd(pickStd);
                        stackPickLine.setShowErrorBars(showErrorBars);
                        stackPickLine.setPenStyle(prefs.getPickPrefs().getPenStyle());
                        subplot.AddPlotObject(stackPickLine);
                        stackPickLines.put(phase, stackPickLine);
                    }

                }
            }
            repaint();
        } else if (obj instanceof PickErrorChangeState) {
            PickErrorChangeState pecs = (PickErrorChangeState) obj;
            VPickLine vpl = pecs.getPickLine();
            double deltaT = pecs.getDeltaStd();
            String phase = vpl.getText();
            DetectionPhasePickModel.getInstance().adjustAllPickStdValuesForPhase(phase, deltaT);
        }
    }

    @Override
    public void displayAllPicks() {
        // Do nothing
    }
    @Override
    public void clearAllPicks() {
        for(VPickLine vpl : stackPickLines.values()){
            subplot.DeletePlotObject(vpl);
        }
        stackPickLines.clear();
        repaint();
    }

}
