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
package llnl.gnem.core.gui.plotting;


import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

import java.awt.event.MouseEvent;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A class that holds a MouseEvent and a PlotObject. Used by PlotObjectObservable
 * to pass information about mouse button events involving PlotObjects to registered
 * listeners.
 *
 * @author Doug Dodge
 * @see llnl.gnem.plotting.PlotObjectObservable
 */
public class PlotObjectClicked {
    /**
     * The packaged MouseEvent
     */
    public MouseEvent me;
    /**
     * The packaged PlotObject
     */
    public PlotObject po;
    /**
     * The MouseMode in effect when this selection was made
     */
    public MouseMode mode;
    
    public ButtonState buttonState;

    public static enum ButtonState {PRESSED,RELEASED,CLICKED,UNKNOWN}
    /**
     * Constructor for the PlotObjectSelectInfo object
     *
     * @param me MouseEvent to be packaged
     * @param po PlotObject to be packaged
     */
    public PlotObjectClicked( MouseEvent me, PlotObject po, MouseMode mode )
    {
        this.me = me;
        this.po = po;
        this.mode = mode;
        buttonState = ButtonState.UNKNOWN;
    }
    
    public PlotObjectClicked( MouseEvent me, PlotObject po, MouseMode mode, ButtonState buttonState )
    {
        this.me = me;
        this.po = po;
        this.mode = mode;
        this.buttonState = buttonState;
    }
}

