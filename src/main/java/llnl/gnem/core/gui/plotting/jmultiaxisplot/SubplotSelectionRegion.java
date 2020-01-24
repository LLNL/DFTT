package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import llnl.gnem.core.gui.plotting.ZoomLimits;

import java.util.*;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Class containing a JSubplot and a subregion within the JSubplot. Used for passing
 * selection messages from the JMultiAxisPlot MouseListener to interested observers.
 *
 * @author Doug Dodge
 */
public class SubplotSelectionRegion {
    /**
     * Constructor for the SubplotSelectionRegion object
     *
     * @param p      The JSubplot
     * @param region The selected region within this subplot
     */
    public SubplotSelectionRegion( JSubplot p, ZoomLimits region )
    {
        this.p = p;
        this.region = region;
    }

    /**
     * Gets the JSubplot
     *
     * @return The subplot value
     */
    public JSubplot getSubplot()
    {
        return p;
    }

    /**
     * Gets the selectedRegion for the contained JSubplot
     *
     * @return The selected Region
     */
    public ZoomLimits getSelectedRegion()
    {
        return region;
    }

    private JSubplot p;
    private ZoomLimits region;
}


