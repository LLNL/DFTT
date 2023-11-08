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
package llnl.gnem.dftt.core.gui.plotting;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Observable;

import llnl.gnem.dftt.core.gui.plotting.PlotObjectClicked.ButtonState;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JPlotKeyMessage;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JPlotKeyReleasedMessage;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.PickErrorChangeState;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.PickMovedState;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.SubplotSelectionRegion;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.WindowDurationChangedState;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.ZoomInStateChange;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.ZoomOutStateChange;
import llnl.gnem.dftt.core.gui.plotting.plotobject.MarginButton;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;

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
     * @param me   The MouseEvent that occurred
     * @param po   The PlotObject that was acted upon
     * @param mode The MouseMode in effect when this selection was made
     */
    public void MouseButtonAction(MouseEvent me, PlotObject po, MouseMode mode) {
        setChanged();
        notifyObservers(new PlotObjectClicked(me, po, mode));
    }

    public void MouseButtonAction(MouseEvent me, PlotObject po, MouseMode mode, ButtonState buttonState) {
        setChanged();
        notifyObservers(new PlotObjectClicked(me, po, mode, buttonState));
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
        ArrayList<Coordinate> dest = new ArrayList<>(points);
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
        notifyObservers(new JPlotKeyReleasedMessage(e));
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
