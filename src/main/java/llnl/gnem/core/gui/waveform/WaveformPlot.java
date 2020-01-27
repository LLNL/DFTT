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

import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.PickCreationInfo;
import llnl.gnem.core.gui.plotting.PlotObjectClicked;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.*;

/**
 *
 * @author dodge1
 */
public class WaveformPlot extends JMultiAxisPlot implements Observer {

    private static final long serialVersionUID = -5046347104031264112L;
    private boolean ctrlKeyIsDown;
   
    public WaveformPlot() {
        this(MouseMode.SELECT_ZOOM, "");
        
    }
    
    public WaveformPlot(MouseMode defaultMode, String xAxisLabel) {
        super(defaultMode, XAxisType.Standard);
        ctrlKeyIsDown = false;
        getXaxis().setLabelText(xAxisLabel);
        addPlotObjectObserver(this);
    }

    @Override
    public void update(Observable observable, Object obj) {
        // TODO redo this so we're not having to do so many instanceof calls.
        if (obj instanceof JPlotKeyMessage) {
            handleKeyMessage(obj);
        } else if (obj instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) obj;
            if (!keyEvent.isControlDown()) {
                ctrlKeyIsDown = false;
            }
        } else if (obj instanceof PlotObjectClicked) {
            handlePlotObjectClicked(obj);
        } else if (obj instanceof PickCreationInfo) {
            handlePickCreationInfo(obj);
        } else if (obj instanceof PickMovedState) {
            handlePickMovedState(obj);
        } else if (obj instanceof PickErrorChangeState) {
            handlePickErrorChangeState(obj);
        } else if (obj instanceof WindowDurationChangedState && !ctrlKeyIsDown) {
            resizeSingleWindow(obj);
        } else if (obj instanceof WindowDurationChangedState && ctrlKeyIsDown) {
            resizeAllWindows(obj);
        }
    }

    public void magnify() {
        scale(2.0);
    }

    public void reduce() {
        scale(0.5);
    }

    protected void handleKeyMessage(Object obj) {
        JPlotKeyMessage msg = (JPlotKeyMessage) obj;
        KeyEvent keyEvent = msg.getKeyEvent();
        if (keyEvent.isControlDown()) {
            ctrlKeyIsDown = true;
        }
    }

    protected void handlePlotObjectClicked(Object obj) {
    }

    protected void handlePickCreationInfo(Object obj) {
    }

    protected void handlePickMovedState(Object obj) {
    }

    protected void handlePickErrorChangeState(Object obj) {
    }

    protected void resizeSingleWindow(Object obj) {
    }

    protected void resizeAllWindows(Object obj) {
    }


}
