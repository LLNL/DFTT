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
package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import java.util.Stack;
import llnl.gnem.core.gui.plotting.ZoomLimits;

/*
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2002 Lawrence Livermore
 * National Laboratory.
 */
/**
 * Class containing a JSubplot and its zoomdata.
 *
 * @author Doug Dodge
 */
class SubplotZoomData {

    private final JSubplot p;
    private final Stack<SubplotDisplayInfo> stack;
    private SubplotDisplayInfo initialState;
    private boolean displayable;

    /**
     * Constructor for the SubplotZoomData object
     *
     * @param p The JSubplot
     */
    public SubplotZoomData(JSubplot p) {
        this.p = p;
        stack = new Stack<>();
        initialState = null;
        displayable = true;
    }

    public void initLimits(ZoomLimits limits) {
        unzoomAll();
        initialState = getCurrentState();
        zoomIn(p.getCanDisplay(), limits);
    }

    public Stack<ZoomLimits> getZoomLimits() {
        Stack<ZoomLimits> result = new Stack<>();
        for (SubplotDisplayInfo sdi : stack) {
            result.push(sdi.Limits);
        }
        return result;
    }

    /**
     * Zoom in to the state held in the input argument
     *
     * @param newState The state to zoom to
     */
    public void zoomIn(boolean visible, ZoomLimits limits) {
        saveCurrentState();
        SubplotDisplayInfo newState = new SubplotDisplayInfo(visible && p.getCanDisplay(), limits);
        setState(newState);
    }

    /**
     * Zoom out to the last state stored in the state vector
     *
     * @return true if zoom is successful.
     */
    public boolean zoomOut() {
        if (!stack.isEmpty()) {
            setState(stack.pop());
            return true;
        }
        return false;
    }

    /**
     * Zoom to the first state in the state vector
     */
    public void unzoomAll() {
        if (initialState != null) {
            setState(initialState);
        } else if (!stack.isEmpty()) {
            setState(stack.get(0));
        }
        stack.clear();
    }

    public void setDisplayable(boolean v) {
        displayable = v;
    }

    public boolean isDisplayable() {
        return displayable;
    }

    public boolean isVisible() {
        return isDisplayable() && p.getCanDisplay();
    }

    /**
     * Gets the JSubplot
     *
     * @return The subplot value
     */
    public JSubplot getSubplot() {
        return p;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SubplotZoomData{" + "subplot(" + Integer.toHexString(System.identityHashCode(p)) + "), state=:");
        for (SubplotDisplayInfo sdi : stack) {
            sb.append("\n\t\t");
            sb.append(sdi);
        }
        sb.append(", displayable=" + displayable);
        return sb.toString();
    }

    private void saveCurrentState() {
        stack.push(getCurrentState());
    }

    private SubplotDisplayInfo getCurrentState() {
        XAxis ax = p.getXaxis();
        YAxis ay = p.getYaxis();
        ZoomLimits currentLimits = new ZoomLimits(ax.getMin(), ax.getMax(), ay.getMin(), ay.getMax());
        return new SubplotDisplayInfo(p.getCanDisplay(), currentLimits);
    }

    private void setState(SubplotDisplayInfo state) {
        XAxis ax = p.getXaxis();
        YAxis ay = p.getYaxis();

        p.setCanDisplay(displayable && state.displayable);

        ax.setMin(state.Limits.xmin);
        ax.setMax(state.Limits.xmax);
        ay.setMin(state.Limits.ymin);
        ay.setMax(state.Limits.ymax);
    }
}
