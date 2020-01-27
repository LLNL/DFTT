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


import llnl.gnem.core.gui.plotting.keymapper.ControlKeyMapper;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

import java.awt.event.KeyEvent;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A class containing information about selected plot objects and the keyboard state.
 * Instances of this class are sent to observers when the user selects a plot object
 * and enters a key code while the object is selected. Interested observers can
 * act on this information as desired.
 *
 * @author Doug Dodge
 */
public class JPlotKeyMessage {
    /**
     * Constructor for the JPlotKeyMessage object
     *
     * @param e The KeyEvent that triggered this message to be sent.
     * @param p The currently-selected subplot.
     * @param o The selected PlotObject. (Could be null if the user clicked inside
     *          the axis boundaries while entering the key combination.)
     * @param controlKeyMapper  provides platform-specific key mappings
     */
    public JPlotKeyMessage( KeyEvent e, JSubplot p, PlotObject o, ControlKeyMapper controlKeyMapper )
    {
        this.keyEvent = e;
        this.subplot = p;
        this.plotObject = o;
        this.controlKeyMapper = controlKeyMapper;
    }

    /**
     * Gets the JSubplot
     *
     * @return The contained JSubplot
     */
    public JSubplot getSubplot()
    {
        return subplot;
    }

    /**
     * Gets the plotObject attribute of the JPlotKeyMessage object
     *
     * @return The plotObject value
     */
    public PlotObject getPlotObject()
    {
        return plotObject;
    }

    /**
     * Gets the keyEvent attribute of the JPlotKeyMessage object
     *
     * @return The keyEvent value
     */
    public KeyEvent getKeyEvent()
    {
        return keyEvent;
    }

    public ControlKeyMapper getControlKeyMapper()
    {
        return controlKeyMapper;
    }

    private final JSubplot subplot;
    private final PlotObject plotObject;
    private final KeyEvent keyEvent;
    private final ControlKeyMapper controlKeyMapper;
}


