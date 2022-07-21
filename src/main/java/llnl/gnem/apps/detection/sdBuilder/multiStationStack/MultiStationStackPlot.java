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
package llnl.gnem.apps.detection.sdBuilder.multiStationStack;

import llnl.gnem.apps.detection.sdBuilder.allStations.*;
import llnl.gnem.apps.detection.sdBuilder.picking.SavePicksWorker;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import llnl.gnem.apps.detection.dataAccess.dataobjects.PhasePick;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.HideTraceAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.MagnifyAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.ReduceAction;
import llnl.gnem.apps.detection.sdBuilder.allStations.actions.RemoveSinglePickAction;
import llnl.gnem.apps.detection.util.KeyPhaseMapper;

import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.MouseOverPlotObject;
import llnl.gnem.core.gui.plotting.PickCreationInfo;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickTextPosition;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class MultiStationStackPlot extends JMultiAxisPlot implements Observer {

    private static final long serialVersionUID = 5460335786323972206L;

    private JSubplot subplot;
    private final double overlap = 10.0; // percent overlap
    private final double shift = 1 - overlap / 100.0;
    private final Map<Line, StackElement> lineSeisMap;
    private final Map<Line, Double> lineStartTimeOffsetMap;
    private final Map<VPickLine, PhasePick> pickLinePickMap;
    private final JPopupMenu traceMenu;

    public MultiStationStackPlot() {
        subplot = addSubplot();
        lineSeisMap = new HashMap<>();
        lineStartTimeOffsetMap = new HashMap<>();
        pickLinePickMap = new HashMap<>();
        addPlotObjectObserver(this);
        ToolTipManager.sharedInstance().setDismissDelay(60000);
        addKeyListener(new PlotKeyListener());

        traceMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem(HideTraceAction.getInstance(this));
        traceMenu.add(item);

        item = new JMenuItem(RemoveSinglePickAction.getInstance(this));
        traceMenu.add(item);
        requestFocusInWindow();
    }

    @Override
    public void clear() {
        super.clear();
        lineSeisMap.clear();
        lineStartTimeOffsetMap.clear();
        pickLinePickMap.clear();
        repaint();
    }

    void updateForChangedData() {
        clear();
        subplot = addSubplot();

        double centerValue = 0;
        double overlap = 10.0; // percent overlap
        double shift = 1 - overlap / 100.0;
        double minStart = Double.MAX_VALUE;
        double maxEnd = -minStart;
        getTitle().setText("Stack");
        Collection<StackElement> data = MultiStationStackModel.getInstance().getStackData();

        for (StackElement esd : data) {
            float[] plotData = esd.getData();
            double traceStartTime = 0;

            double delta = esd.getDelta();
            double endTime = (plotData.length - 1) * delta;
            centerValue += shift;
            scaleAndShiftTrace(centerValue, plotData, plotData);
            Line line = new Line(traceStartTime, delta, plotData);
            Color lineColor = Color.BLUE;
            line.setColor(lineColor);
            subplot.AddPlotObject(line);
            line.setSelectable(true);
            lineSeisMap.put(line, esd);

            if (endTime > maxEnd) {
                maxEnd = endTime;
            }
            plotLeftText(subplot, esd.getKey().toString(), centerValue + 0.3);

        }

        subplot.setXlimits(0, maxEnd);
        double minY = 0;
        double maxY = centerValue + shift;
        subplot.getYaxis().setMin(minY);
        subplot.getYaxis().setMax(maxY);
        subplot.getYaxis().setVisible(false);

        setAllXlimits();

        setMouseMode(MouseMode.SELECT_ZOOM);
        repaint();

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
                StackElement traceData = this.lineSeisMap.get((Line) poc.po);
                if (traceData != null) {
                }
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
        } else if (obj instanceof MouseOverPlotObject) {
            MouseOverPlotObject mopo = (MouseOverPlotObject) obj;
            PlotObject po = mopo.getPlotObject();
            if (po instanceof Line) {
            }

        } else if (obj instanceof WindowDurationChangedState) {
            WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
        } else if (obj instanceof PickMovedState) {
            PickMovedState pms = (PickMovedState) obj;
            double deltaT = pms.getDeltaT();
            VPickLine vpl = pms.getPickLine();

            PhasePick dpp = pickLinePickMap.get(vpl);

            if (dpp != null) {
                //     AllStationsPickModel.getInstance().moveSinglePick(dpp, deltaT);
            }

        } else if (obj instanceof PickCreationInfo) {
            setMouseMode(MouseMode.SELECT_ZOOM);

            PickCreationInfo pci = (PickCreationInfo) obj;
            PlotObject po = pci.getSelectedObject();
            if (po instanceof Line) {
                Line line = (Line) po;

                StackElement btd = lineSeisMap.get(line);
                Double startOffset = lineStartTimeOffsetMap.get(line);
                if (btd != null && startOffset != null) {
                    Coordinate coord = pci.getCoordinate();
//                    double pointerXvalue = coord.getWorldC1();
//                    double timeReference = btd.getTime().getEpochTime() + startOffset;
//                    double pickEpochTime = timeReference + pointerXvalue;
//                    double pickStd = btd.estimatePickStdErr(pickEpochTime);
//                    PhasePick dpp = AllStationsPickModel.getInstance().addSinglePick(btd, pickEpochTime, pickStd);
//                    if (dpp != null) {
//                        createSinglePick(dpp, timeReference, coord.getWorldC2());
//                    }
                }
            }
        } else if (obj instanceof JPlotKeyMessage) {
            JPlotKeyMessage msg = (JPlotKeyMessage) obj;
            KeyEvent e = msg.getKeyEvent();
            if (e.getKeyChar() == '+') {
                zoomInAroundMouse(msg);
            }
        }

    }

    private void createSinglePick(PhasePick dpp, double referenceTime, double yValue) {
        double x = dpp.getTime() - referenceTime;
        double aheight = 40.0; // millimeters
        VPickLine vpl = new VPickLine(x, yValue, aheight, dpp.getPhase());
        vpl.setColor(Color.black);
        vpl.setSelectable(true);
        vpl.setDraggable(true);
        vpl.setShowErrorBars(true);
        vpl.setStd(dpp.getStd());

        subplot.AddPlotObject(vpl);
        pickLinePickMap.put(vpl, dpp);
        repaint();
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

    VPickLine addTimeMarker(Double xval, String text, Color lineColor, double yval, int textSize, PickTextPosition textPos, Double std) {
        if (xval != null) {
            double aheight = 40.0;//mm
            int awidth = 2;
            boolean draggable = false;
            VPickLine vpl = new VPickLine(xval, yval, aheight, text, lineColor, awidth, draggable, textSize, textPos);
            if (std != null) {
                vpl.setDraggable(true);
                vpl.setSelectable(true);
                vpl.setShowErrorBars(true);
                vpl.setStd(std);
            }
            subplot.AddPlotObject(vpl);
            return vpl;
        }
        return null;
    }

    private void zoom(double zoomFactor) {
        double xmin = this.getXaxis().getMin();
        double xmax = this.getXaxis().getMax();

        double newDurationFactor = (xmax - xmin) * zoomFactor / 200;
        double newBegin = xmin + newDurationFactor;
        double newEnd = xmax - newDurationFactor;

        zoomToNewXLimits(newBegin, newEnd);
        repaint();

    }

    private void pan(double panFactor) {
        double xmin = this.getXaxis().getMin();
        double xmax = this.getXaxis().getMax();

        double panUnits = (xmax - xmin) * panFactor / 100;
        double newBegin = xmin + panUnits;
        double newEnd = xmax + panUnits;

        zoomToNewXLimits(newBegin, newEnd);
        repaint();

    }

    void removeDeletedPick(PhasePick dpp) {
        for (VPickLine vpl : pickLinePickMap.keySet()) {
            PhasePick pp = pickLinePickMap.get(vpl);
            if (pp.equals(dpp)) {
                subplot.DeletePlotObject(vpl);
                pickLinePickMap.remove(vpl);
                repaint();
                return;
            }
        }
    }

    private class PlotKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {
            if ((event.getKeyCode() == KeyEvent.VK_Z) && ((event.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                Collection<PhasePick> picks = AllStationsPickModel.getInstance().getAllPicks();
                Collection<Integer> picksToRemove = AllStationsPickModel.getInstance().getPicksToRemove();
                new SavePicksWorker(picks, picksToRemove, AllStationsFrame.getInstance()).execute();
            }
            String phase = KeyPhaseMapper.getInstance().getMappedPhase(event.getKeyChar());
            if (phase != null) {
                AllStationsPickModel.getInstance().setCurrentPhase(phase);
                setMouseMode(MouseMode.CREATE_PICK);
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            setMouseMode(MouseMode.SELECT_ZOOM);
            switch (event.getKeyChar()) {
                case 'n':
                case 'N':
                    //            NextAction.getInstance(this).actionPerformed(null);
                    break;
                case 'v':
                case 'V':
                    //            PreviousAction.getInstance(this).actionPerformed(null);
                    break;
                case 'm':
                case 'M':
                    MagnifyAction.getInstance(this).actionPerformed(null);
                    break;
                case 'r':
                case 'R':
                    ReduceAction.getInstance(this).actionPerformed(null);
                    break;
                case 'b':
                case 'B':
                    //              BadAction.getInstance(this).actionPerformed(null);
                    break;
                default:
                    // NOP
                    break;
            }

            switch (event.getKeyCode()) {
                case 37:
                    pan(+20);
                    break;
                case 38: // up arrow
                    zoom(+25);
                    break;
                case 39:
                    pan(-20);
                    break;
                case 40: // down arrow
                    zoom(-25);
                    break;
                case KeyEvent.VK_ADD:
                    MagnifyAction.getInstance(this).actionPerformed(null);
                    break;
                case KeyEvent.VK_MINUS:
                    ReduceAction.getInstance(this).actionPerformed(null);
                    break;
                default:
                    // NOP
                    break;
            }
        }
    }

}
