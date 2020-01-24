package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import llnl.gnem.core.gui.plotting.ZoomLimits;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Class containing the information controlling display and limits for a single
 * JSubplot.
 *
 * @author Doug Dodge
 */
class SubplotDisplayInfo {
    /**
     * Constructor for the SubplotDisplayInfo object
     *
     * @param displayable Whether this JSubplot should be displayed
     * @param current     The current axis limits for this JSubplot
     */
    public SubplotDisplayInfo( boolean displayable, ZoomLimits current )
    {
        this.displayable = displayable;
        this.Limits = new ZoomLimits( current );
    }

    boolean displayable;

    @Override
    public String toString() {
        return "SubplotDisplayInfo{" + "displayable=" + displayable + ", Limits=" + Limits + '}';
    }
    ZoomLimits Limits;
}


