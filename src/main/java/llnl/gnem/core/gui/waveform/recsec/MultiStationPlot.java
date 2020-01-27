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
package llnl.gnem.core.gui.waveform.recsec;

import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.PenStyle;
import llnl.gnem.core.gui.plotting.ZoomType;
import llnl.gnem.core.gui.plotting.MouseOverPlotObject;
import llnl.gnem.core.gui.plotting.PickCreationInfo;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.PaintMode;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickDataBridge;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.SubplotSelectionRegion;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickErrorChangeState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.XAxis;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickTextPosition;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickMovedState;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.core.gui.map.origins.OriginInfo;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.DistanceType;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.DisplayArrival;
import llnl.gnem.core.gui.waveform.WaveformPlot;
import llnl.gnem.core.gui.waveform.phaseVisibility.UsablePhaseManager;
import llnl.gnem.core.gui.waveform.recsec.commands.ChangeDeltimCommand;
import llnl.gnem.core.gui.waveform.recsec.commands.DeleteArrivalCommand;
import llnl.gnem.core.gui.waveform.recsec.commands.MoveArrivalCommand;
import llnl.gnem.core.gui.plotting.keymapper.ControlKeyMapper;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.core.traveltime.Ak135.TraveltimeCalculatorProducer;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.phaseVisibility.BasePreferredPhaseManager;
import llnl.gnem.core.gui.waveform.phaseVisibility.PreferredPhaseListener;

/**
 * User: Doug Date: Sep 26, 2009 Time: 12:53:57 PM COPYRIGHT NOTICE Copyright
 * (C) 2008 Doug Dodge.
 */
public abstract class MultiStationPlot extends WaveformPlot implements PreferredPhaseListener {

    private final MultiStationPlotPresentationPrefs msPrefs;
    private final Map<BaseSingleComponent, LineAndHolder> channelToHolderMap;
    private final Map<Line, WaveformHolder> lineWaveformMap;
    private BaseSingleComponent currentChannel;
    private final JSubplot subplot;
    private final Map<VPickLine, DisplayArrival> pickLineArrivalMap;
    private final Map<DisplayArrival, VPickLine> arrivalPicklineMap;
    private Line originLine = null;
    private final Map<Line, String> predictedPhaseLines;
    private TimeReductionType timeReduction;
    private final Map<BaseSingleComponent, XPinnedText> channelDataTextMap;
    private ScalingType scalingType;
    private DistanceRenderPolicy policy;
    private boolean hasOrigin = false;
    private final JPopupMenu pickMenu;
    protected MultiStationWaveformDataModel dataModel;
    private MultiStationViewer owner;
    private final RsDeleteArrivalAction rsDeleteArrival;
    private static DistanceType distanceType = DistanceType.km;
    private static final long serialVersionUID = -4531112745853932502L;

    @Override
    public void updatePhases() {
        for (Line line : predictedPhaseLines.keySet()) {
            subplot.DeletePlotObject(line);
        }
        predictedPhaseLines.clear();
        OriginInfo info = dataModel.getCurrentOriginInfo();
        if (info != null) {
            UsablePhaseManager apm = WaveformViewerFactoryHolder.getInstance().getUsablePhaseManager();
            for (SeismicPhase phase : apm.getUsablePhases()) {
                try {
                    plotTheoreticalTime(info, phase);
                } catch (IllegalArgumentException e) {
                    ApplicationLogger.getInstance().log(Level.FINE, "Failed plotting theoretical time!", e);
                }
            }
        }
        repaint();
    }

    public static void setDistanceType(DistanceType type) {
        distanceType = type;
        WaveformHolderManager.setDistanceType(type);
    }

    private void setStatusbarMessage() {
        String distOrderString;
        if (hasOrigin) {
            if (policy == DistanceRenderPolicy.ORDER_BY_DISTANCE) {
                distOrderString = "Ordered By Distance";
            } else {
                distOrderString = "At Hypocentral Distance";
            }
        } else {
            distOrderString = "Unordered";
        }

        String scalingString = String.format("Scaling: %s", scalingType);

        String timeReductionString = String.format("Reduction Type: %s", timeReduction);

        String message = String.format("%s, %s, Traces: %s", scalingString, timeReductionString, distOrderString);
        owner.getStatusbar().setCenterText(message);
    }

    public MultiStationPlot(MultiStationWaveformDataModel dataModel,
            MultiStationPlotPresentationPrefs prefs) {
        this.dataModel = dataModel;
        this.msPrefs = prefs;
        scalingType = RecordSectionViewerProperties.getInstance().getScalingType();
        policy = DistanceRenderPolicyPrefs.getInstance().getPolicy();
        channelToHolderMap = new HashMap<>();
        lineWaveformMap = new HashMap<>();
        pickLineArrivalMap = new HashMap<>();
        arrivalPicklineMap = new HashMap<>();
        channelDataTextMap = new HashMap<>();

        XAxis axis = this.getXaxis();
        RecordSectionViewerProperties props = RecordSectionViewerProperties.getInstance();
        timeReduction = props.getTimeReductionType();
        String xAxisText = getXAxisText();
        axis.setLabelText(xAxisText);
        boolean isUseZoomBox = props.isUseZoomBox();
        ZoomType zoomType = isUseZoomBox ? ZoomType.ZOOM_BOX : ZoomType.ZOOM_ALL;
        setZoomType(zoomType);
        predictedPhaseLines = new HashMap<>();

        currentChannel = null;
        addPlotObjectObserver(this);
        subplot = addSubplot();
        pickMenu = new JPopupMenu();

        rsDeleteArrival = new RsDeleteArrivalAction();
        JMenuItem item = new JMenuItem(rsDeleteArrival);
        pickMenu.add(item);
        BasePreferredPhaseManager.getInstance().addListener(this);
    }

    public MultiStationWaveformDataModel getDataModel() {
        return dataModel;
    }

    DistanceType getDistanceType() {
        return distanceType;
    }

    private String getXAxisText() {
        String xAxisText = "Seconds Relative to Reference Time";
        if (timeReduction == TimeReductionType.Ptime) {
            xAxisText = "Seconds Relative to Predicted P-Arrival";
        }
        return xAxisText;
    }

    public void setOwner(MultiStationViewer multiStationViewerContainer) {
        owner = multiStationViewerContainer;
    }

    public void updateForChangedOrigin() {
        if (originLine != null && DistanceRenderPolicyPrefs.getInstance().getPolicy() == DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE) {

            OriginInfo info = dataModel.getCurrentOriginInfo();
            if (info != null) {

                hasOrigin = true;
                double factor = WaveformHolderManager.getTraceAmpToDistFactor(info, channelToHolderMap.keySet());
                Map<BaseSingleComponent, LineAndHolder> tmpMap = new HashMap<>();
                for (BaseSingleComponent bsc : channelToHolderMap.keySet()) {
                    double dist = info.getPoint3D().dist_geog(bsc.getPoint3D());
                    LineAndHolder lah = channelToHolderMap.get(bsc);
                    Line line = lah.line;
                    WaveformHolder holder = lah.holder;
                    WaveformHolder newHolder = new HasOriginWaveformHolder(bsc, dist, holder.getDataRange(),
                            holder.getMaxDataRange(), holder.getScalingType(), factor, info.getTime());
                    double meanAdjustment = convertY((float) (newHolder.getCenter() - holder.getCenter()));
                    float[] data = line.getYdata();
                    SeriesMath.AddScalar(data, meanAdjustment);
                    double start = newHolder.getTime();
                    double reductionTime = newHolder.getTimeReduction(timeReduction, info);

                    line.replaceYarray(data);
                    line.setStart(start - reductionTime);
                    lineWaveformMap.remove(line);
                    lineWaveformMap.put(line, newHolder);
                    LineAndHolder lah2 = new LineAndHolder(line, newHolder);
                    tmpMap.put(bsc, lah2);

                    XPinnedText text = channelDataTextMap.get(bsc);
                    if (text != null) {
                        final float y_label = convertY((float) newHolder.getCenter());
                        text.setYValue(y_label);
                    }
//                    Collection<? extends DisplayArrival> arrivals = bsc.getArrivals();
//                    for (DisplayArrival arrival : arrivals) {
//                        VPickLine vpl = arrivalPicklineMap.get(arrival);
//                        if (vpl != null) {
//                            PickDataBridge pdb = vpl.getDataBridge();
//                            if (pdb instanceof MSPlotPickDataBridge) {
//                                MSPlotPickDataBridge mpdb = (MSPlotPickDataBridge) pdb;
//                                double oldReductionTime = mpdb.getReductionTime();
//                                double shift = oldReductionTime - reductionTime;
//                                vpl.setXval(vpl.getXval() + shift);
//                                mpdb.setReductionTime(reductionTime);
//                            }
//                            vpl.setYval(vpl.getYval() + meanAdjustment);
//                        }
//                    }

                }
                channelToHolderMap.clear();
                channelToHolderMap.putAll(tmpMap);
                subplot.DeletePlotObject(originLine);
                plotOriginLine(info);
                repaint();
            }
        } else {
            updateForNewEvent();
        }
        setStatusbarMessage();
    }

    @Override
    public void clear() {
        subplot.Clear();
        channelToHolderMap.clear();
        lineWaveformMap.clear();
        pickLineArrivalMap.clear();
        arrivalPicklineMap.clear();
        channelDataTextMap.clear();
        predictedPhaseLines.clear();
        getTitle().setText("");
        updateButtonStates();
        repaint();
    }

    public void updateForNewEvent() {
        RecordSectionViewerProperties props = RecordSectionViewerProperties.getInstance();
        OriginInfo info = dataModel.getCurrentOriginInfo();

        Collection<BaseSingleComponent> channels = dataModel.getWaveformChannels();
        Collection<WaveformHolder> holders = WaveformHolderManager.getWaveformHolders(info,
                channels, props.getScalingType());

        subplot.Clear();
        channelToHolderMap.clear();
        lineWaveformMap.clear();
        pickLineArrivalMap.clear();
        arrivalPicklineMap.clear();
        predictedPhaseLines.clear();
        hasOrigin = info != null;
        if (info != null && DistanceRenderPolicyPrefs.getInstance().getPolicy() == DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE) {
            subplot.getYaxis().setLabelText("Distance (" + distanceType + ")");
            plotOriginLine(info);
            UsablePhaseManager apm = WaveformViewerFactoryHolder.getInstance().getUsablePhaseManager();
            for (SeismicPhase phase : apm.getUsablePhases()) {
                try {
                    plotTheoreticalTime(info, phase);
                } catch (IllegalArgumentException e) {
                    ApplicationLogger.getInstance().log(Level.FINE, "Failed plotting theoretical time!", e);
                }
            }
        } else {
            subplot.getYaxis().setLabelText("Traces in Distance Order (Bottom to Top)");
            originLine = null;
            if (info != null && DistanceRenderPolicyPrefs.getInstance().getPolicy() == DistanceRenderPolicy.ORDER_BY_DISTANCE
                    && timeReduction == TimeReductionType.Ptime) {
                plotZeroLine(holders.size());
            }
        }

        double maxDist = 0;
        double maxTime = 0;
        for (WaveformHolder wh : holders) {
            float[] yData = wh.getPlotArray();
            double dist = convertY((float) wh.getCenter());
            if (dist > maxDist) {
                maxDist = dist;
            }
            double start = wh.getTime();
            double reductionTime = wh.getTimeReduction(timeReduction, info);
            double delta = 1.0 / wh.getSamprate();
            maxTime = updateMaxTime(yData, delta, maxTime);
            yData = convertY(yData);

            Line line = new Line(start - reductionTime, delta, yData);
            Color color = msPrefs.getTraceColor(wh.getChannelData().getStationInfo());
            line.setColor(color);

            subplot.AddPlotObject(line);
            BaseSingleComponent cd = wh.getChannelData();
            channelToHolderMap.put(cd, new LineAndHolder(line, wh));
            lineWaveformMap.put(line, wh);

            String label = String.format("%s", cd.getShortName());
            double y_label = convertY((float) (wh.getCenter()));
            XPinnedText text = new XPinnedText(5, y_label, label);
            text.setVerticalAlignment(VertAlignment.BOTTOM);
            subplot.AddPlotObject(text);
            channelDataTextMap.put(cd, text);

        }
        currentChannel = dataModel.getCurrentChannel();
        LineAndHolder lah = channelToHolderMap.get(currentChannel);
        if (lah != null) {
            lah.line.setColor(msPrefs.getSelectedTraceColor());
        }

        subplot.SetAxisLimits(0, maxTime, 0, maxDist);
        String titleText = buildTitleText();
        getTitle().setText(titleText);
        subplot.getYaxis().setTicksVisible(DistanceRenderPolicyPrefs.getInstance().getPolicy() == DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE);
        repaint();
        updateButtonStates();
        setStatusbarMessage();
    }

    @Override
    protected void setPlotProperties(JSubplot plot) {
        super.setPlotProperties(plot);
        plot.getYaxis().setTicksVisible(DistanceRenderPolicyPrefs.getInstance().getPolicy() == DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE);
    }

    private float convertY(float y) {

        return (float) (y * distanceType.getScaleFactor());

    }

    private float[] convertY(float[] y_val) {

        for (int i = 0; i < y_val.length; ++i) {
            y_val[i] *= distanceType.getScaleFactor();
        }

        return y_val;
    }

    private void plotZeroLine(int ntraces) {
        int npts = 1000;
        float[] ptimes = new float[npts];
        float[] yvals = new float[npts];
        double increment = (double) ntraces / npts;
        for (int j = 0; j < npts; ++j) {
            yvals[j] = (float) (j * increment);
            double pTime = 0;
            ptimes[j] = (float) pTime;
        }
        Color color = msPrefs.getPickPrefs().getColor();
        Line line = new Line(ptimes, yvals, color, PaintMode.COPY, PenStyle.SOLID, 2);
        subplot.AddPlotObject(line);
        predictedPhaseLines.put(line, "Origin");
    }

    private void plotTheoreticalTime(OriginInfo origin, SeismicPhase phase) {
        try {
            SinglePhaseTraveltimeCalculator calculator = TraveltimeCalculatorProducer.getInstance().getSinglePhaseTraveltimeCalculator(phase.getName());
            SinglePhaseTraveltimeCalculator pCalc = TraveltimeCalculatorProducer.getInstance().getSinglePhaseTraveltimeCalculator("P");
            int npts = 1000;
            double maxDelta = dataModel.getMaxDelta(origin) + 1;
            double increment = maxDelta / npts;
            ArrayList<PairT<Double, Double>> points = new ArrayList<>();
            for (int j = 0; j < npts; ++j) {
                double delta = (float) (j * increment);
                double phaseTime = calculator.getTT1D(delta, origin.getNonNullDepth());
                double pTime = pCalc.getTT1D(delta, origin.getNonNullDepth());
                if (phaseTime > 0) {
                    double time = timeReduction == TimeReductionType.Ptime ? phaseTime - pTime : phaseTime;
                    if (delta > 110) {// kludge to handle distances where ak135 model does not predict a time.
                        break;
                    }
                    points.add(new PairT<>(delta, time));
                }
            }
            float[] times = new float[points.size()];
            float[] delta = new float[points.size()];
            for (int j = 0; j < points.size(); ++j) {
                PairT<Double, Double> point = points.get(j);
                delta[j] = convertY(point.getFirst().floatValue());
                times[j] = point.getSecond().floatValue();
            }
            Color color = msPrefs.getPredPickPrefs().getColor();
            Line line = new Line(times, delta, color, PaintMode.COPY, PenStyle.SOLID, 2);
            subplot.AddPlotObject(line);
            predictedPhaseLines.put(line, phase.getName());
        } catch (IOException | ClassNotFoundException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
        }
    }

    private void plotOriginLine(OriginInfo origin) {
        try {
            double originTime = 0.0;
            SinglePhaseTraveltimeCalculator calculator = TraveltimeCalculatorProducer.getInstance().getSinglePhaseTraveltimeCalculator("P");

            int npts = 1000;
            float[] xvals = new float[npts];
            float[] yvals = new float[npts];
            double maxDelta = dataModel.getMaxDelta(origin) + 1;
            maxDelta = maxDelta < 110 ? maxDelta : 110;
            double increment = maxDelta / npts;
            for (int j = 0; j < npts; ++j) {
                yvals[j] = (float) (j * increment);
                double predTime = originTime;
                if (timeReduction == TimeReductionType.Ptime) {
                    // Y value here must always be in degrees for computation
                    predTime = -calculator.getTT1D(yvals[j], origin.getNonNullDepth());
                }
                xvals[j] = (float) predTime;
                yvals[j] = convertY(yvals[j]);
            }
            originLine = new Line(xvals, yvals, Color.LIGHT_GRAY, PaintMode.COPY, PenStyle.DASH, 1);
            subplot.AddPlotObject(originLine);
        } catch (IOException | ClassNotFoundException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
        }
    }

    private void addArrivalToPlot(DisplayArrival arrival, WaveformHolder wh, double reductionTime) {
        double yAxisFraction = msPrefs.getPickPrefs().getHeight();
        double yCenter = wh.getCenter();
        double allowableHeight = wh.getHeight();
        double pickHeight = ((double) convertY((float) (allowableHeight * yAxisFraction)));

        int pickLineWidth = msPrefs.getPickPrefs().getWidth();
        boolean draggable = true;
        Color pickColor = arrival.getRenderColor();

        PickDataBridge pdb = new MSPlotPickDataBridge(arrival, wh.getChannelData(), reductionTime);

        final float y_pos = convertY((float) yCenter);
        VPickLine vpl = new VPickLine(pdb, y_pos, pickHeight, arrival.getPhase(),
                pickColor, pickLineWidth, draggable, msPrefs.getPickPrefs().getTextSize(),
                PickTextPosition.BOTTOM);
        vpl.setVisible(true);
        vpl.setDraggable(arrival.isMutable());
        vpl.setErrorBarShowHandles(true);
        vpl.setShowErrorBars(true);
        vpl.setPenStyle(msPrefs.getPickPrefs().getPenStyle());

        subplot.AddPlotObject(vpl);
        pickLineArrivalMap.put(vpl, arrival);
        arrivalPicklineMap.put(arrival, vpl);
    }

    public void updateForChangedChannel() {
        if (currentChannel != null) {
            LineAndHolder lah = channelToHolderMap.get(currentChannel);
            if (lah != null) {
                Color color = msPrefs.getTraceColor(currentChannel.getStationInfo());
                lah.line.setColor(color);
            }
        }

        currentChannel = dataModel.getCurrentChannel();
        if (currentChannel != null) {
            LineAndHolder lah = channelToHolderMap.get(currentChannel);
            if (lah != null) {
                lah.line.setColor(msPrefs.getSelectedTraceColor());
            }
        }
        repaint();
    }

    public void updateForChangedWaveform(BaseSingleComponent channelData) {
        updateButtonStates();
        Collection<WaveformHolder> waveformHolders = lineWaveformMap.values();
        WaveformHolderManager.renormalizeHolders(waveformHolders);
        LineAndHolder lah = channelToHolderMap.get(channelData);
        if (lah != null) {
            Line line = lah.line;
            float[] newData = convertY(lah.holder.getPlotArray());
            line.replaceYarray(newData);
            repaint();
        }
    }

    public void updateForChangedWaveform() {
        updateButtonStates();
        Collection<LineAndHolder> holders = channelToHolderMap.values();
        Collection<WaveformHolder> waveformHolders = lineWaveformMap.values();
        WaveformHolderManager.renormalizeHolders(waveformHolders);
        for (LineAndHolder lah : holders) {
            Line line = lah.line;
            float[] newData = convertY(lah.holder.getPlotArray());
            line.replaceYarray(newData);
        }
        repaint();
    }

    private double getReductionTime(LineAndHolder lah) {

        OriginInfo info = dataModel.getCurrentOriginInfo();
        return info != null ? lah.holder.getTimeReduction(timeReduction, info) : 0;
    }

    public BaseSingleComponent getAssociatedChannelData(Line line, JSubplot plot) {
        WaveformHolder wh = lineWaveformMap.get(line);
        if (wh != null) {
            return wh.getChannelData();
        } else {
            return null;
        }
    }

    public void updateButtonStates() {
//        RedoAction.getInstance(null).setEnabled(canRedo);
//        RedoAllAction.getInstance(null).setEnabled(canRedo);
//        boolean canUndo = cm.canUndo();
//        UndoAction.getInstance(null).setEnabled(canUndo);
//        UndoAllAction.getInstance(null).setEnabled(canUndo);
//
//        SavePicksAction.getInstance(null).setEnabled(dataModel.isSaveRequired());
    }

    @Override
    public void update(Observable o, Object obj) {
        super.update(o, obj);
        if (obj instanceof Vector) {
            Vector regions = (Vector) obj;
            for (Object region : regions) {
                if (region instanceof SubplotSelectionRegion) {
                }
            }

        } else if (obj instanceof MouseOverPlotObject) {
            MouseOverPlotObject mopo = (MouseOverPlotObject) obj;
            PlotObject po = mopo.getPlotObject();
            if (po instanceof Line) {
                Line line = (Line) po;
                WaveformHolder wh = lineWaveformMap.get(line);
                if (wh != null) {
                    BaseSingleComponent cd = wh.getChannelData();
                    setToolTipText(cd.getSta() + " - " + cd.toString());
                } else if (line == originLine) {
                    setToolTipText("Origin Time");
                } else {
                    String phase = predictedPhaseLines.get(line);
                    if (phase != null) {
                        setToolTipText(phase);
                    }
                }
            }
        } else if (obj instanceof MouseMode) {
            // System.out.println( "owner.setMouseModeMessage((MouseMode) obj);");
        } else if (obj instanceof MouseWheelEvent) {
            MouseWheelEvent mwe = (MouseWheelEvent) obj;
            maybeDoWheelZoom(mwe);
        }
    }

    @Override
    protected void handleKeyMessage(Object obj) {
        JPlotKeyMessage msg = (JPlotKeyMessage) obj;
        KeyEvent e = msg.getKeyEvent();
        ControlKeyMapper controlKeyMapper = msg.getControlKeyMapper();
        int keyCode = e.getKeyCode();
        PlotObject po = msg.getPlotObject();

        if (po != null && po instanceof VPickLine) {
            VPickLine vpl = (VPickLine) po;
            DisplayArrival arrival = pickLineArrivalMap.get(vpl);
            if (arrival != null) {
                if (controlKeyMapper.isDeleteKey(keyCode)) {
                    BaseSingleComponent cd = dataModel.getChannelForExistingArrival(arrival);
                    if (cd != null) {
                        Command cmd = new DeleteArrivalCommand(cd, arrival);
                        owner.invoke(cmd);
                    }
                }
            }
        }
    }

    @Override
    protected void handlePlotObjectClicked(Object obj) {
        PlotObjectClicked poc = (PlotObjectClicked) obj;
        if (poc.po instanceof VPickLine) {
            VPickLine vpl = (VPickLine) poc.po;
            DisplayArrival arrival = pickLineArrivalMap.get(vpl);
            if (arrival != null) {
                rsDeleteArrival.setArrival(arrival);
                BaseSingleComponent cd = dataModel.getChannelForExistingArrival(arrival);
                rsDeleteArrival.setChannel(cd);
                if (poc.me.getButton() == MouseEvent.BUTTON3) {
                    pickMenu.show(poc.me.getComponent(),
                            poc.me.getX(), poc.me.getY());
                }
            }
        } else if (poc.po instanceof Line) {
            Line line = (Line) poc.po;
            WaveformHolder wh = lineWaveformMap.get(line);
            if (wh != null) {
                BaseSingleComponent cd = wh.getChannelData();
                dataModel.setCurrentChannel(cd);
            }
        }
    }

    @Override
    protected void handlePickCreationInfo(Object obj) {
        PickCreationInfo pci = (PickCreationInfo) obj;
        if (pci.getOwningPlot() != null && pci.getSelectedObject() != null) {
            if (pci.getSelectedObject() instanceof Line) {
                String phase = dataModel.getPickManager().getCurrentPhase();
//                DisplayArrival existing = getExistingMutablePick(phase, pci);
//                if (existing != null) {
//                    double deltaT = pci.getCoordinate().getWorldC1() - existing.getTime();
//                    Command cmd = new MoveArrivalCommand(currentChannel, deltaT, existing);
//                    owner.invoke(cmd);
//                } else {
//
//                    OriginInfo info = dataModel.getCurrentOriginInfo();
//                    LineAndHolder lah = channelToHolderMap.get(currentChannel);
//                    double reductionTime = info != null ? lah.holder.getTimeReduction(timeReduction, info) : 0;
//                    double pickTime = pci.getCoordinate().getWorldC1() + reductionTime;
//                    Command cmd = new CreateArrivalCommand(currentChannel, pickTime, phase);
//                    owner.invoke(cmd);
//                }
            }
        }
    }

    @Override
    protected void handlePickMovedState(Object obj) {
        PickMovedState pms = (PickMovedState) obj;
        double deltaT = pms.getDeltaT();
        VPickLine vpl = pms.getPickLine();
        if (vpl != null) {
            DisplayArrival arrival = pickLineArrivalMap.get(vpl);
            if (arrival != null) {
                Command cmd = new MoveArrivalCommand(currentChannel, deltaT, arrival);
                owner.invoke(cmd);
            }
        }
    }

    @Override
    protected void handlePickErrorChangeState(Object obj) {
        PickErrorChangeState pecs = (PickErrorChangeState) obj;
        double delta = pecs.getDeltaStd();
        VPickLine vpl = pecs.getPickLine();
        if (vpl != null) {
            DisplayArrival arrival = pickLineArrivalMap.get(vpl);
            if (arrival != null) {
                Command cmd = new ChangeDeltimCommand(currentChannel, delta, arrival);
                owner.invoke(cmd);
            }
        }
    }

    public void setAxisTickVisibility() {
        RecordSectionViewerProperties props = RecordSectionViewerProperties.getInstance();
        boolean visible = props.isAxisTicksVisible();
        Collection<JSubplot> plotVector = this.getSubplotManager().getSubplots();
        for (JSubplot subplot : plotVector) {
            subplot.getYaxis().setTicksVisible(visible);
        }
        this.repaint();
    }

    public void setWaveformScalingType(ScalingType type) {
        this.scalingType = type;
        Collection<LineAndHolder> holders = channelToHolderMap.values();
        for (LineAndHolder lah : holders) {
            lah.holder.setScalingType(type);
            Line line = lah.line;
            float[] newData = convertY(lah.holder.getPlotArray());
            line.replaceYarray(newData);
        }
        repaint();
        setStatusbarMessage();
    }

    @Override
    public void magnify() {
        magnifyTraces();
    }

    public void magnifyTraces() {
        for (Map.Entry<BaseSingleComponent, LineAndHolder> entry : channelToHolderMap.entrySet()) {
            BaseSingleComponent bsc = entry.getKey();
            LineAndHolder lah = entry.getValue();

            lah.holder.magnify();
            Line line = lah.line;
            float[] newData = convertY(lah.holder.getPlotArray());
            line.replaceYarray(newData);

            XPinnedText text = channelDataTextMap.get(bsc);
            if (text != null) {
                final float y_label = convertY((float) lah.holder.getCenter());
                text.setYValue(y_label);
            }
        }

        repaint();
    }

    @Override
    public void reduce() {
        reduceTraces();
    }

    public void reduceTraces() {

        for (Map.Entry<BaseSingleComponent, LineAndHolder> entry : channelToHolderMap.entrySet()) {
            BaseSingleComponent bsc = entry.getKey();
            LineAndHolder lah = entry.getValue();

            lah.holder.reduce();
            Line line = lah.line;
            float[] newData = convertY(lah.holder.getPlotArray());
            line.replaceYarray(newData);

            XPinnedText text = channelDataTextMap.get(bsc);
            if (text != null) {
                final float y_label = convertY((float) lah.holder.getCenter());
                text.setYValue(y_label);
            }
        }

        repaint();
    }

    public void setTimeReductionType(TimeReductionType type) {
        timeReduction = type;
        updateForNewEvent();
        setStatusbarMessage();
    }

    public void distanceRenderPolicyChanged(DistanceRenderPolicy policy) {
        this.policy = policy;
        updateForNewEvent();
        setStatusbarMessage();
    }

    protected abstract String buildTitleText();

    private double updateMaxTime(float[] yData, double delta, double maxTime) {
        double endTime = (yData.length - 1) * delta;
        if (endTime > maxTime) {
            maxTime = endTime;
        }
        return maxTime;
    }

    static class LineAndHolder {

        Line line;
        WaveformHolder holder;

        LineAndHolder(Line line, WaveformHolder holder) {
            this.line = line;
            this.holder = holder;
        }
    }
//
//    public DisplayArrival getExistingMutablePick(String phase, PickCreationInfo pci) {
//        for (DisplayArrival arrival : pickLineArrivalMap.values()) {
//            if (arrival.getPhase().equals(phase) && arrival.isMutable()) {
//                Line line = (Line) pci.getSelectedObject();
//                WaveformHolder wh = lineWaveformMap.get(line);
//                BaseSingleComponent cd = wh.getChannelData();
//                Collection<? extends DisplayArrival> currentChannelArrivals = cd.getArrivals();
//                if (currentChannelArrivals.contains(arrival)) {
//                    return arrival;
//                }
//            }
//        }
//        return null;
//    }

    public double getReductionTimeForChannel(BaseSingleComponent channelData) {

        OriginInfo info = dataModel.getCurrentOriginInfo();
        LineAndHolder lah = channelToHolderMap.get(channelData);
        return info != null ? lah.holder.getTimeReduction(timeReduction, info) : 0;

    }

    public void maybeDoWheelZoom(MouseWheelEvent mwe) {
        if (mwe.getWheelRotation() < 0) {
            double reftime = 0.0;

            double xmin = getXaxis().getMin();
            double xmax = getXaxis().getMax();

            double range = xmax - xmin;
            double newRange = 0.9 * range;
            double halfRange = newRange / 2;

            double newXmin = reftime - halfRange;
            double newXmax = reftime + halfRange;
            if (newXmin < xmin) {
                newXmin = xmin;
                newXmax = reftime + reftime - newXmin;
            } else if (newXmax > xmax) {
                newXmax = xmax;
                newXmin = reftime - (xmax - reftime);
            }

            zoomToNewXLimits(newXmin, newXmax);
            repaint();

        } else {
            zoomOut();
            repaint();
        }

    }

    public class RsDeleteArrivalAction extends AbstractAction {

        private static final long serialVersionUID = -2535596789458138833L;

        private DisplayArrival arrival = null;
        private BaseSingleComponent currentChannel = null;

        private RsDeleteArrivalAction() {
            super("Delete", Utility.getIcon(MultiStationPlot.this, "miscIcons/delete16.gif"));
            putValue(SHORT_DESCRIPTION, "Delete Selected Arrival");
            putValue(MNEMONIC_KEY, KeyEvent.VK_D);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentChannel != null && arrival != null) {
                Command cmd = new DeleteArrivalCommand(currentChannel, arrival);
                owner.invoke(cmd);
            }
        }

        public void setArrival(DisplayArrival arrival) {
            this.setEnabled(arrival.isMutable());
            this.arrival = arrival;
        }

        public void setChannel(BaseSingleComponent currentChannel) {
            this.currentChannel = currentChannel;
        }
    }
}
