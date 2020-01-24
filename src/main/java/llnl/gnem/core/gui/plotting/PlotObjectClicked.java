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

