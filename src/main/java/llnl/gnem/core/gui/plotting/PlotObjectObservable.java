package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomInStateChange;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.SubplotSelectionRegion;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickErrorChangeState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.ZoomOutStateChange;
import llnl.gnem.core.gui.plotting.plotobject.MarginButton;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Observable;
import llnl.gnem.core.gui.plotting.PlotObjectClicked.ButtonState;

/*
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2002 Lawrence Livermore
 * National Laboratory.
 */
/**
 * An Observable that allows interested classes to respond to mouse button
 * activity related to PlotObjects in a JMultiAxisPlot
 *
 * @author Doug Dodge
 */
public class PlotObjectObservable extends Observable {

    public void MouseWheelAction(MouseWheelEvent event) {
        setChanged();
        notifyObservers(event);
    }

    /**
     * Broadcast a message that a mouse button action has occurred that is
     * related to a PlotObject
     *
     * @param me The MouseEvent that occurred
     * @param po The PlotObject that was acted upon
     * @param mode The MouseMode in effect when this selection was made
     */
    public void MouseButtonAction(MouseEvent me, PlotObject po, MouseMode mode) {
        setChanged();
        notifyObservers(new PlotObjectClicked(me, po, mode));
    }
    
    
        public void MouseButtonAction(MouseEvent me, PlotObject po, MouseMode mode, ButtonState buttonState) {
        setChanged();
        notifyObservers(new PlotObjectClicked(me, po, mode,buttonState));
    }


    public void RegionSelectionAction(ArrayList<SubplotSelectionRegion> regions) {
        setChanged();
        notifyObservers(regions);
    }

    public void DeleteAction(PlotObject po) {
        setChanged();
        notifyObservers(po);
    }

    public void notifyMouseModeChange(MouseMode mouseMode) {
        setChanged();
        notifyObservers(mouseMode);
    }

    public void MouseKeyboardSelectAction(JPlotKeyMessage p) {
        setChanged();
        notifyObservers(p);
    }

    public void MouseOverAction(PlotObject po) {
        setChanged();
        notifyObservers(new MouseOverPlotObject(po));
    }

    public void PlotZoomStateChanged() {
        setChanged();
        notifyObservers(new PlotStateChange());
    }

    public void MouseMove(Coordinate c) {
        setChanged();
        notifyObservers(c);
    }

    public void DoubleClickObject(final PlotObject po) {
        setChanged();
        notifyObservers(new PlotObjectDoubleClicked(po));

    }

    public void FinishDraggingPick(PickMovedState pms) {
        setChanged();
        notifyObservers(pms);
    }

    public void finishChangingWindowDuration(WindowDurationChangedState state) {
        state.finishChange();
        setChanged();
        notifyObservers(state);
    }

    public void finishChangingPickError(PickErrorChangeState state) {
        setChanged();
        notifyObservers(state);
    }

    public void createNewPick(PickCreationInfo info) {
        setChanged();
        notifyObservers(info);
    }

    public void finishedDrawingPolygon(ArrayList<Coordinate> points) {
        ArrayList<Coordinate> dest = new ArrayList<Coordinate>();
        dest.addAll(points);
        setChanged();
        notifyObservers(dest);
    }

    public void sendNewScaleFactor(double factor) {
        setChanged();
        notifyObservers(new PlotScaleFactor(factor));
    }

    public void sendPanStartMessage(PanStyle style) {
        setChanged();
        notifyObservers(new PanInfo(style, false));
    }

    public void sendPanCompleteMessage(PanStyle style) {
        setChanged();
        notifyObservers(new PanInfo(style, true));
    }

    public void sendPlotDoubleClickedMessage(MouseEvent me, Coordinate c) {
        setChanged();
        notifyObservers(new PlotDoubleClicked(me, c));
    }

    public void sendPlotClickedMessage(MouseEvent me, Coordinate c, JSubplot subplot) {
        setChanged();
        notifyObservers(new PlotClicked(me, c, subplot));
    }

    public void sendPickSelectionStateChangeMessage(VPickLine vpl, boolean isSelected) {
        setChanged();
        notifyObservers(new PickSelectionStateChange(vpl, isSelected));
    }

    public void marginButtonClicked(MarginButton marginButton) {
        setChanged();
        notifyObservers(marginButton);
    }

    public void KeyReleasedAction(KeyEvent e) {
        setChanged();
        notifyObservers(e);
    }

    public void sendZoomInMessage(ZoomInStateChange zoomInStateChange) {
        setChanged();
        notifyObservers(zoomInStateChange);
    }

    public void sendZoomOutMessage() {
        setChanged();
        notifyObservers(new ZoomOutStateChange());
    }
}