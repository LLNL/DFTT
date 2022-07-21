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
package llnl.gnem.core.gui.waveform;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.gui.map.events.EventModel;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.gui.map.stations.StationModel;
import llnl.gnem.core.gui.plotting.HorizAlignment;
import llnl.gnem.core.gui.plotting.HorizPinEdge;
import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.PickCreationInfo;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.VertPinEdge;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JWindowRegion;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickDataBridge;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickErrorChangeState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickTextPosition;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomInStateChange;
import llnl.gnem.core.gui.plotting.keymapper.ControlKeyMapper;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PinnedText;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.SymbolStyle;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.factory.commands.ChangeAllWindowDurationsCommand;
import llnl.gnem.core.gui.waveform.factory.commands.ChangeDeltimCommand;
import llnl.gnem.core.gui.waveform.factory.commands.ChangeSingleWindowDurationCommand;
import llnl.gnem.core.gui.waveform.factory.commands.CreateArrivalCommand;
import llnl.gnem.core.gui.waveform.factory.commands.DeleteArrivalCommand;
import llnl.gnem.core.gui.waveform.factory.commands.MoveArrivalCommand;
import llnl.gnem.core.gui.waveform.phaseVisibility.UsablePhaseManager;
import llnl.gnem.core.gui.waveform.plotPrefs.PlotPreferenceModel;
import llnl.gnem.core.traveltime.Ak135.TraveltimeCalculatorProducer;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.util.CommandManager;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.util.Invokable;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.BaseTraceData;
import llnl.gnem.core.waveform.components.ComponentSet;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.components.RotationStatus;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;

public class ComponentSetPlot<T extends BaseSingleComponent> extends WaveformPlot implements ArrivalListener<T, DisplayArrival>, ArrivalChangeListener<DisplayArrival>, Invokable {

    private final ComponentSet<T> set;
    private final ComponentSetPlotHolder parent;
    private final Map<Integer, Line> componentLineMap;
    private final Map<JSubplot, T> plotComponentMap;
    private final Map<VPickLine, DisplayArrival> pickLineArrivalMap;
    private double plotRefTime;
    private final JPopupMenu pickMenu;
    private final JPopupMenu traceMenu;
    private final Collection<SingleComponentOperationAction> traceActions;
    private final Map<T, ComponentView<T>> componentViews;

    public ComponentSetPlot(ComponentSet<T> set, ComponentSetPlotHolder parent) {
        // TODO perhaps the Observer role can be removed from this class in
        // favor of using Actions or Listeners, but for now we'll shoehorn the
        // parent class in here so we can send messages back up.
        this.set = set;
        this.parent = parent;

        componentLineMap = new HashMap<>();

        plotComponentMap = new HashMap<>();
        pickLineArrivalMap = new HashMap<>();

        configurePlotFromPrefs();

        pickMenu = new JPopupMenu();
        DeleteArrivalAction action = new DeleteArrivalAction();
        getActionMap().put(DeleteArrivalAction.class, action);
        pickMenu.add(new JMenuItem(action));

        traceMenu = new JPopupMenu();
        updateButtonStates();
        traceActions = new ArrayList<>();
        componentViews = new HashMap<>();
        for (T component : set.getComponents()) {
            componentViews.put(component, new ComponentView<>(component));
        }

        set.addArrivalListener(this);
        addPlotObjectObserver(this);
    }

    public void addTraceMenuItem(SingleComponentOperationAction action) {
        if (!traceActions.contains(action)) {
            JMenuItem item = new JMenuItem(action);
            traceMenu.add(item);
            traceActions.add(action);
        }
    }

    @Override
    protected void handleKeyMessage(Object obj) {
        super.handleKeyMessage(obj);
        JPlotKeyMessage msg = (JPlotKeyMessage) obj;
        KeyEvent keyEvent = msg.getKeyEvent();
        KeyEvent e = msg.getKeyEvent();
        ControlKeyMapper controlKeyMapper = msg.getControlKeyMapper();
        int keyCode = e.getKeyCode();
        PlotObject po = msg.getPlotObject();
        if (keyEvent.isControlDown()) {
            if (keyEvent.getKeyCode() == 66 || keyEvent.getKeyCode() == 98) {// B or b
                JSubplot plot = msg.getSubplot();
                if (plot != null) {
                    T component = plotComponentMap.get(plot);
                    if (component != null) {
                        setMouseMode(MouseMode.SELECT_ZOOM);
                        requestFocusInWindow();
                    }
                }
            }
        } else if (po != null && po instanceof VPickLine && (controlKeyMapper.isDeleteKey(keyCode) || keyCode == KeyEvent.VK_D)) {
            VPickLine vpl = (VPickLine) po;
            deleteArrival(vpl, po);
        } else if (po != null && po instanceof JWindowRegion && (controlKeyMapper.isDeleteKey(keyCode) || keyCode == KeyEvent.VK_D)) {
            JWindowRegion jwr = (JWindowRegion) po;
            VPickLine vpl = jwr.getAssociatedPick();
            deleteArrival(vpl, po);
        }
    }

    private void deleteArrival(VPickLine vpl, PlotObject po) {
        DisplayArrival arrival = pickLineArrivalMap.get(vpl);
        if (arrival != null) {
            JBasicPlot aPlot = po.getOwner();
            if (aPlot instanceof JSubplot) {
                T component = plotComponentMap.get((JSubplot) aPlot);
                if (component != null) {
                    DeleteArrivalAction action = (DeleteArrivalAction) getActionMap().get(DeleteArrivalAction.class);
                    action.invoke(component, arrival);
                }
            }
        }
    }

    public boolean canModifyPicks() {
        return true;
    }

    @Override
    protected void handlePickCreationInfo(Object obj) {
        PickCreationInfo pci = (PickCreationInfo) obj;
        if (canModifyPicks() && pci.getOwningPlot() != null && pci.getSelectedObject() != null) {
            if (pci.getSelectedObject() instanceof Line) {
                String phase = getDataModel().getPickManager().getCurrentPhase();
                String auth = getDataModel().getPickManager().getUser();
                T component = plotComponentMap.get(pci.getOwningPlot());
                if (component != null) {
                    if (!set.hasArrival(component, phase)) {
                        createPick(component, phase, pci, auth);
                    }
                }
            }
        }
    }

    public void createPick(T component, String phase, PickCreationInfo pci, String auth) {
        ApplicationLogger.getInstance().log(Level.FINE, String.format("Creating arrival command for component(%s)  with relTime = %f in handlePickCreationInfo...",
                component, pci.getCoordinate().getWorldC1()));

        if (component.isPickAllowable() && set.canCreatePicks()) {
            Command cmd = new CreateArrivalCommand(set, component, pci.getCoordinate().getWorldC1(), plotRefTime, phase, auth);
            CommandManager cm = parent.getCommandManager();
            cm.invokeCommand(cmd);
        }
    }

    @Override
    protected void handlePickErrorChangeState(Object obj) {
        PickErrorChangeState pecs = (PickErrorChangeState) obj;
        double delta = pecs.getDeltaStd();
        VPickLine vpl = pecs.getPickLine();
        if (canModifyPicks() && vpl != null) {
            DisplayArrival arrival = pickLineArrivalMap.get(vpl);
            if (arrival != null) {
                JBasicPlot aPlot = vpl.getOwner();
                if (aPlot instanceof JSubplot) {
                    T component = plotComponentMap.get((JSubplot) aPlot);
                    if (component != null) {
                        Command cmd = new ChangeDeltimCommand(component, delta, arrival);
                        CommandManager cm = parent.getCommandManager();
                        cm.invokeCommand(cmd);
                    }
                }
            }
        }
    }

    @Override
    protected void handlePickMovedState(Object obj) {
        PickMovedState pms = (PickMovedState) obj;
        double deltaT = pms.getDeltaT();
        VPickLine vpl = pms.getPickLine();
        if (canModifyPicks() && vpl != null) {
            DisplayArrival arrival = pickLineArrivalMap.get(vpl);
            if (arrival != null) {
                if (arrival.isMovable()) {
                    JBasicPlot aPlot = vpl.getOwner();
                    if (aPlot instanceof JSubplot) {
                        T component = plotComponentMap.get((JSubplot) aPlot);
                        if (component != null) {
                            Command cmd = new MoveArrivalCommand(deltaT, arrival);
                            CommandManager cm = parent.getCommandManager();
                            cm.invokeCommand(cmd);
                        }
                    }
                } else {
                    // Move the VPickLine back to where it was before move.
                    arrivalChanged(arrival);
                }
            }
        }

    }

    @Override
    protected void handlePlotObjectClicked(Object obj) {
        PlotObjectClicked poc = (PlotObjectClicked) obj;
        if (poc.po instanceof VPickLine) {
            VPickLine vpl = (VPickLine) poc.po;
            handlePicklineClick(vpl, poc);
        } else if (poc.po instanceof JWindowRegion) {
            JWindowRegion jwr = (JWindowRegion) poc.po;
            VPickLine vpl = jwr.getAssociatedPick();
            handlePicklineClick(vpl, poc);
        } else if (poc.po instanceof Line) {
            Line line = (Line) poc.po;
            JBasicPlot aPlot = line.getOwner();
            if (aPlot instanceof JSubplot) {
                T component = plotComponentMap.get((JSubplot) aPlot);
                if (component != null) {
                    for (SingleComponentOperationAction action : traceActions) {
                        action.setComponent(component);
                    }
                    if (poc.me.getButton() == MouseEvent.BUTTON3) {
                        if (!traceMenu.isVisible()) {
                            traceMenu.show(poc.me.getComponent(),
                                    poc.me.getX(), poc.me.getY());
                        }
                    }
                }
            }
        }
    }

    private void handlePicklineClick(VPickLine vpl, PlotObjectClicked poc) {
        DisplayArrival arrival = pickLineArrivalMap.get(vpl);
        if (arrival != null) {
            JBasicPlot aPlot = vpl.getOwner();
            if (aPlot instanceof JSubplot) {
                T component = plotComponentMap.get((JSubplot) aPlot);
                if (component != null) {
                    DeleteArrivalAction action = (DeleteArrivalAction) getActionMap().get(DeleteArrivalAction.class);
                    action.setArrival(arrival);
                    action.setComponent(component);
                    if (poc.me.getButton() == MouseEvent.BUTTON3) {
                        pickMenu.show(poc.me.getComponent(),
                                poc.me.getX(), poc.me.getY());
                    }
                }
            }
        }
    }

    @Override
    protected void resizeAllWindows(Object obj) {
        WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
        reset(wdcs);
        Command cmd = new ChangeAllWindowDurationsCommand(wdcs, pickLineArrivalMap.keySet());
        parent.getCommandManager().invokeCommand(cmd);
    }

    @Override
    protected void resizeSingleWindow(Object obj) {
        WindowDurationChangedState wdcs = (WindowDurationChangedState) obj;
        reset(wdcs);
        Command cmd = new ChangeSingleWindowDurationCommand(wdcs);
        parent.getCommandManager().invokeCommand(cmd);
    }
//
//    @Override
//    public void handleZoomIn(ZoomInStateChange zisc) {
//        parent.getPlotContainer().zoomToNewXLimits(zisc);
//    }

    @Override
    public void handleZoomIn(ZoomInStateChange zisc) {
        parent.getPlotContainer().zoomToNewXLimits(zisc);
    }

    @Override
    public void handleZoomOut() {
        parent.getPlotContainer().unzoomTraces();
    }

    private void plotComponents(ComponentSet<T> set) {
        EventModel<? extends AbstractEventInfo> eventModel = getDataModel().getEventModel();
        AbstractEventInfo eventInfo = eventModel.getCurrent();
        T component = set.getVerticalComponent();
        if (component != null) {
            plotSingleComponent(eventInfo, component, 0);
        }

        if (set.horizontalComponentsAvailable()) {
            component = set.getHorizontalComponent1();
            if (component != null) {
                plotSingleComponent(eventInfo, component, 1);
            }
            component = set.getHorizontalComponent2();
            if (component != null) {
                plotSingleComponent(eventInfo, component, 2);
            }
        }
        if (set.hasUncategorizedComponent()) {
            int count = set.getUncategorizedComponentCount();
            for (int j = 0; j < count; ++j) {
                component = set.getUncategorizedComponent(j);
                plotSingleComponent(eventInfo, component, 3 + j);
            }
        }

        repaint();
    }

    protected void plotSingleComponent(AbstractEventInfo eventInfo, T component, int componentNumber) {
        JSubplot plot = this.addSubplot();
        setPlotProperties(plot);
        plot.getPlotRegion().setBackgroundColor(getBackgroundColor(component.getTransferStatus(), false));

        CssSeismogram seis = component.getSeismogram();
        BaseTraceData td = component.getTraceData();
        plotRefTime = eventInfo.getTime().getEpochTime();
        double startTime = seis.getTimeAsDouble() - plotRefTime;
        Line line = (Line) plot.Plot(startTime, seis.getDelta(), td.getPlotData());
        if (prefs.isPlotLineSymbols()) {
            line.setSymbolStyle(SymbolStyle.CIRCLE);
        }
        Color color = PlotPreferenceModel.getInstance().getPrefs().getTraceColor();
        line.setColor(color);
        componentLineMap.put(componentNumber, line);
        addTextToPlot(component, eventInfo, plot);
        plotComponentEventInfo(component, eventInfo, plot);

        plotComponentMap.put(plot, component);
        componentViews.get(component).setPlot(plot);
    }

    protected JSubplot getSubplot(T component) {
        return componentViews.get(component).getPlot();
    }

    @Override
    protected void setPlotProperties(JSubplot plot) {
        super.setPlotProperties(plot);
        plot.getPlotRegion().setFillRegion(true);
        plot.getPlotRegion().setDrawBox(true);
    }

    protected void plotComponentEventInfo(T component, AbstractEventInfo eventInfo, JSubplot plot) {
        addTheoreticalPicks(component, eventInfo, plot);
    }

    private Color getBackgroundColor(TransferStatus transferStatus, boolean isSelected) {
        switch (transferStatus) {
            case TRANSFERRED:
                return isSelected ? new Color(220, 220, 255) : new Color(240, 255, 240);
            case TRANSFER_FAILED:
                return new Color(255, 240, 240);
            default:
                return set.getType().getBackground();
        }
    }

    public void updateForComponentRotation(Collection<ComponentSet> rotatable) {
        if (rotatable.contains(set)) {
            EventModel<? extends AbstractEventInfo> eventModel = getDataModel().getEventModel();
            AbstractEventInfo eventInfo = eventModel.getCurrent();
            T component = set.getHorizontalComponent1();
            if (component != null) {
                updateSinglePlotWaveform(eventInfo, component, 1);
            }
            component = set.getHorizontalComponent2();
            if (component != null) {
                updateSinglePlotWaveform(eventInfo, component, 2);
            }
        }
        repaint();
    }

    private void replaceLines() {
        EventModel<? extends AbstractEventInfo> eventModel = getDataModel().getEventModel();
        AbstractEventInfo eventInfo = eventModel.getCurrent();
        for (JSubplot plot : plotComponentMap.keySet()) {
            T comp = plotComponentMap.get(plot);
            float[] newData = comp.getTraceData().getPlotData();
            Line[] lines = plot.getLines();
            Collection<Line> seisLines = componentLineMap.values();
            for (Line line : lines) {
                if (seisLines.contains(line)) {
                    line.replaceYarray(newData);
                }
            }
            addTextToPlot(comp, eventInfo, plot);
        }
        this.scaleAllTraces(true);
    }

    private void updateSinglePlotWaveform(AbstractEventInfo eventInfo, T component, int componentNumber) {
        Line line = componentLineMap.get(componentNumber);
        CssSeismogram seis = component.getSeismogram();
        BaseTraceData td = component.getTraceData();
        float[] data = td.getPlotData();
        double start = seis.getTimeAsDouble() - eventInfo.getTime().getEpochTime();
        line.setStart(start);
        line.replaceYarray(data);
        if (component.getRotationStatus() == RotationStatus.FAILED_TO_ROTATE) {
            line.setColor(Color.RED);
        } else {
            Color color = PlotPreferenceModel.getInstance().getPrefs().getTraceColor();
            line.setColor(color);
        }
        JSubplot subplot = (JSubplot) line.getOwner();
        subplot.getPlotRegion().setBackgroundColor(getBackgroundColor(component.getTransferStatus(), false));
        subplot.getPlotRegion().setFillRegion(true);
        addTextToPlot(component, eventInfo, subplot);
    }

    private void addTextToPlot(T component, AbstractEventInfo eventInfo, JSubplot subplot) {
        subplot.clearText();
        double vOffset = 4;
        StreamKey key = component.getStreamEpochInfo().getStreamInfo().getStreamKey();
        String text = String.format("Source = %s", key.getAgency());
        plotText(subplot, text, vOffset);
        vOffset += 6;
        text = String.format("Network = %s (%d)", key.getNet(), key.getNetJdate());
        plotText(subplot, text, vOffset);
        vOffset += 6;
        text = String.format(" %s %s (%s)", key.getSta(), key.getChan(), key.getLocationCode());
        plotText(subplot, text, vOffset);
        vOffset += 6;
        plotText(subplot, component.getEpoch().getTime().toString(), vOffset);
        Double azimuth = component.getAzimuth();
        if (azimuth != null && azimuth != -1) {
            vOffset += 6;
            text = String.format("Azimuth = %5.1f deg", azimuth);
            plotText(subplot, text, vOffset);
        }
        vOffset += 6;
        Double dip = component.getDip();
        if (dip != null) {
            text = String.format("Dip = %5.1f deg", dip);
            plotText(subplot, text, vOffset);
        }
        
        
        vOffset = 4;
        text = String.format("Sample Rate = %6.2f", component.getSeismogram().getSamprate());
        plotLeftText(subplot, text, vOffset);
        vOffset += 6;
        text = String.format("Data Type = %s", component.getDataType().toString());
        plotLeftText(subplot, text, vOffset);
        vOffset += 6;
        text = String.format("WFID = %d", component.getSeismogram().getWaveformID());
        plotLeftText(subplot, text, vOffset);

        if (eventInfo != null) {
            vOffset += 6;
            StationModel<? extends StationInfo> stationModel = getDataModel().getStationModel();
            StationInfo info = stationModel.getCurrent();
            double dist = EModel.getDistanceWGS84(info.getLat(), info.getLon(), eventInfo.getLat(), eventInfo.getLon());
            double delta = EModel.getDeltaWGS84(info.getLat(), info.getLon(), eventInfo.getLat(), eventInfo.getLon());
            text = String.format("Distance = %5.3f km (%5.1f deg)", dist, delta);
            plotLeftText(subplot, text, vOffset);

            vOffset += 6;
            double baz = EModel.getAzimuthWGS84(info.getLat(), info.getLon(), eventInfo.getLat(), eventInfo.getLon());
            text = String.format("BAZ = %5.1f deg", baz);
            plotLeftText(subplot, text, vOffset);
        }

//        Collection<String> auxilliaryText = component.getAuxilliaryText();
//        if (!auxilliaryText.isEmpty()) {
//            plotAuxilliaryText(subplot, auxilliaryText);
//        }
        if (component.getTransferStatus() == TransferStatus.TRANSFER_FAILED) {
            String textString = "Transfer Failed";
            PinnedText ptext = new PinnedText(75.0, vOffset, textString, HorizPinEdge.LEFT, VertPinEdge.TOP, "Arial", 72.0, new Color(0, 0, 0, 50), HorizAlignment.LEFT, VertAlignment.CENTER);
            subplot.AddPlotObject(ptext);
        }
    }

    private static void plotText(JSubplot subplot, String textString, double vOffset) {
        boolean textVisible = true;
        PinnedText text = new PinnedText(75.0, vOffset, textString, HorizPinEdge.RIGHT, VertPinEdge.TOP, "Arial", 14.0, Color.black, HorizAlignment.LEFT, VertAlignment.CENTER);
        text.setVisible(textVisible);
        subplot.AddPlotObject(text);
    }

    private static PinnedText plotLeftText(JSubplot subplot, String textString, double vOffset) {
        boolean textVisible = true;
        PinnedText text = new PinnedText(15.0, vOffset, textString, HorizPinEdge.LEFT, VertPinEdge.TOP, "Arial", 14.0, Color.black, HorizAlignment.LEFT, VertAlignment.CENTER);
        text.setVisible(textVisible);
        subplot.AddPlotObject(text);
        return text;
    }

    private void plotAuxilliaryText(JSubplot subplot, Collection<String> auxilliaryText) {
        double vOffset = 4;
        boolean textVisible = true;
        for (String string : auxilliaryText) {
            PinnedText text = new PinnedText(15.0, vOffset, string, HorizPinEdge.LEFT, VertPinEdge.BOTTOM, "Arial", 14.0, Color.black, HorizAlignment.LEFT, VertAlignment.CENTER);
            text.setVisible(textVisible);
            subplot.AddPlotObject(text);

        }
    }

    public void updateForUndoneTransferOperation() {
        plot();
    }

    public void updateForTransferredComponentSet() {
        plot();
    }

    public void plot() {
        clear();
        componentLineMap.clear();
        plotComponents(set);
        for (PairT<T, DisplayArrival> state : set.getActiveArrivals()) {
            arrivalAdded(state.getFirst(), state.getSecond());
        }
    }

    private void reset(WindowDurationChangedState wdcs) {
        // TODO this is seemingly done so we can "erase" the change made on the
        // JWindowRegion and replace it with a Command that does the same thing.
        // Instead we should have the original action create the command.
        double deltaD = wdcs.getDeltaD();
        VPickLine vpl = wdcs.getWindowHandle().getAssociatedPick();
        JWindowRegion window = vpl.getWindow();
        double duration = window.getDuration();
        window.setDurationNoNotify(duration - deltaD);
    }

    void unselectAll() {
        for (ComponentView<T> componentView : componentViews.values()) {
            JSubplot plot = componentView.getPlot();
            Color newColor = getBackgroundColor(componentView.getComponent().getTransferStatus(), false);
            plot.getPlotRegion().setBackgroundColor(newColor);
            repaint();
        }
    }

    public void updateForFilterOperation() {
        replaceLines();
        repaint();
    }

    public void updateForChangedData() {
        replaceLines();
        repaint();
    }

    public ThreeComponentModel getDataModel() {
        return parent.getDataModel();
    }

    public ComponentSetPlotHolder getHolder() {
        return parent;
    }

    @Override
    public void invoke(Command command) {
        parent.getCommandManager().invokeCommand(command);
    }

    public DisplayArrival getExistingPick(String phase) {
        for (DisplayArrival arrival : pickLineArrivalMap.values()) {
            if (arrival.getPhase().equals(phase)) {
                return arrival;
            }
        }
        return null;
    }

    public JSubplot getPlot(T component) {
        return componentViews.get(component).getPlot();
    }

    protected VPickLine getVPickLine(T component, DisplayArrival arrival) {
        return componentViews.get(component).get(arrival);
    }

    protected double getPlotRefTime() {
        return plotRefTime;
    }

    public void updateButtonStates() {
        parent.updateButtonStates();
    }

    public ComponentSet<T> getSet() {
        return set;
    }

    public T getSingleComponent(JSubplot plot) {
        return plotComponentMap.get(plot);
    }

    @Override
    public void arrivalAdded(T component, DisplayArrival arrival) {
        ComponentView<T> componentView = componentViews.get(component);
        if (componentView != null && !componentView.hasArrival(arrival)) {
            addArrivalToPlot(arrival, componentView.getPlot(), component);
            updateButtonStates();
            repaint();
            getDataModel().getPickManager().exitPickMode();
        }
    }

    @Override
    public void arrivalRemoved(DisplayArrival arrival) {
        for (ComponentView<T> componentView : componentViews.values()) {
            if (componentView.hasArrival(arrival)) {
                JSubplot plot = componentView.getPlot();
                VPickLine vpl = componentView.get(arrival);
                remove(plot, vpl, componentView.getComponent(), arrival);

            }
        }
        updateButtonStates();
        repaint();
    }

    protected VPickLine getPickLineForArrival(DisplayArrival arrival) {
        for (ComponentView<T> componentView : componentViews.values()) {
            if (componentView.hasArrival(arrival)) {
                return componentView.get(arrival);
            }
        }
        return null;
    }

    @Override
    public void arrivalChanged(DisplayArrival arrival) {
        for (ComponentView<T> componentView : componentViews.values()) {
            if (componentView.hasArrival(arrival)) {
                updateArrivalOnComponent(arrival, componentView);
            }
        }
        updateButtonStates();
        repaint();
    }

    protected void updateArrivalOnComponent(DisplayArrival arrival, ComponentView<T> componentView) {
        VPickLine vpl = componentView.get(arrival);
        vpl.setXval(arrival.getTime());
        vpl.setYval(componentView.getComponent().getSeismogram().getValueAt(arrival.getEpochTime()));
        if (vpl.getStd() != arrival.getDeltim()) {
            vpl.setStd(arrival.getDeltim());
        }
    }

    protected VPickLine addArrivalToPlot(DisplayArrival arrival, JSubplot plot, T component) {
        int pickLineWidth = 1;
        boolean draggable = true;
        Color pickColor = Color.BLACK;
        ApplicationLogger.getInstance().log(Level.FINE, String.format("ComponentSetPlot::addArrivalToPlot-->Adding arrival (%s) to component(%s)...", arrival, component));

        PickDataBridge pdb = new PlotPickDataBridge(arrival, component);

        double yValue = component.getSeismogram().getValueAt(arrival.getEpochTime());
        VPickLine vpl = new VPickLine(pdb, yValue, 100, arrival.getPhase(),
                pickColor, pickLineWidth, draggable, 10,
                PickTextPosition.BOTTOM);

        vpl.setDraggable(canModifyPicks());
        vpl.setVisible(true);
        vpl.setErrorBarShowHandles(true);
        vpl.setShowErrorBars(true);

        add(plot, vpl, component, arrival);

        return vpl;
    }

    protected void add(JSubplot plot, VPickLine vpl, T component, DisplayArrival arrival) {
        String objectId = Integer.toHexString(System.identityHashCode(vpl));
        ApplicationLogger.getInstance().log(Level.FINE, String.format("ComponentSetPlot::add-->Adding VPickLine(%s) with reltime = %f  ...", objectId, vpl.getXval()));

        plot.AddPlotObject(vpl);
        pickLineArrivalMap.put(vpl, arrival);
        componentViews.get(component).put(arrival, vpl);
        arrival.addListener(this);
    }

    protected void remove(JSubplot plot, VPickLine vpl, T component, DisplayArrival arrival) {
        vpl.setSelected(false);
        plot.DeletePlotObject(vpl);
        pickLineArrivalMap.remove(vpl);
        componentViews.get(component).remove(arrival);
        arrival.removeListener(this);
    }

    private void addTheoreticalPicks(T component, AbstractEventInfo eventInfo, JSubplot plot) {
        UsablePhaseManager mgr = WaveformViewerFactoryHolder.getInstance().getUsablePhaseManager();
        Collection<SeismicPhase> phases = mgr.getUsablePhases();
        for (SeismicPhase phase : phases) {

            try {
                SinglePhaseTraveltimeCalculator calculator = TraveltimeCalculatorProducer.getInstance().getSinglePhaseTraveltimeCalculator(phase.getName());
                Point3D staPos = component.getPoint3D();
                Point3D eventPos = eventInfo.getPoint3D();
                double ttime = calculator.getTT(eventPos, staPos);
                if (ttime > 0) {
                    int pickLineWidth = 1;
                    boolean draggable = false;
                    Color pickColor = Color.gray;
                    VPickLine vpl = new VPickLine(ttime, 0.7, phase.getName(),
                            pickColor, pickLineWidth, draggable, 10,
                            PickTextPosition.TOP);

                    vpl.setVisible(true);
                    vpl.setErrorBarShowHandles(false);
                    vpl.setShowErrorBars(false);
                    plot.AddPlotObject(vpl);
                }
            } catch (IllegalArgumentException e) {
                ApplicationLogger.getInstance().log(Level.FINE, "Failed plotting theoretical time!", e);
            } catch (IOException ex) {
                ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
            }
        }
    }

    public ComponentView<T> getComponentView(T component) {
        return componentViews.get(component);
    }

    @Override
    public void configurePlotFromPrefs() {
        super.configurePlotFromPrefs();
        getPlotRegion().setDrawBox(false);
        setplotSpacing(2.0);
    }

    @SuppressWarnings({"NonThreadSafeLazyInitialization"})
    public class DeleteArrivalAction extends AbstractAction {

        private T component;
        private DisplayArrival arrival;

        private DeleteArrivalAction() {
            super("", Utility.getIcon(ComponentSetPlot.this, "miscIcons/delete16.gif"));
            putValue(NAME, "Delete");
            putValue(SHORT_DESCRIPTION, "Delete selected Arrival");
            putValue(MNEMONIC_KEY, KeyEvent.VK_DELETE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            invoke(component, arrival);
        }

        public void invoke(T component, DisplayArrival arrival) {
            if (component != null && arrival != null && arrival.isCanBeDeleted() && canModifyPicks()) {
                Command cmd = new DeleteArrivalCommand(set, arrival);
                CommandManager cm = parent.getCommandManager();
                cm.invokeCommand(cmd);
            }
        }

        public void setComponent(T component) {
            this.component = component;
        }

        public void setArrival(DisplayArrival arrival) {
            this.arrival = arrival;
            setEnabled(arrival.isCanBeDeleted());
        }
    }

    protected class ComponentView<T> {

        private final T component;
        private final Map<DisplayArrival, VPickLine> arrivalPicklineMap;
        private JSubplot plot;

        public ComponentView(T component) {
            this.component = component;
            arrivalPicklineMap = new HashMap<>();
            plot = null;
        }

        public boolean hasArrival(DisplayArrival arrival) {
            return arrivalPicklineMap.containsKey(arrival);
        }

        public VPickLine get(DisplayArrival arrival) {
            return arrivalPicklineMap.get(arrival);
        }

        public boolean hasPlot() {
            return plot != null;
        }

        public JSubplot getPlot() {
            return plot;
        }

        public T getComponent() {
            return component;
        }

        public void setPlot(JSubplot plot) {
            this.plot = plot;
        }

        public void put(DisplayArrival arrival, VPickLine vpl) {
            arrivalPicklineMap.put(arrival, vpl);
        }

        public void remove(DisplayArrival arrival) {
            arrivalPicklineMap.remove(arrival);
        }
    }
}
