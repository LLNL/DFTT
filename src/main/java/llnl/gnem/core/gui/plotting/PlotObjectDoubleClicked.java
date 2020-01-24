package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

/**
 * Created by: dodge1
 * Date: Aug 5, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PlotObjectDoubleClicked {
    public PlotObjectDoubleClicked( PlotObject po )
    {
        this.po = po;
    }

    public PlotObject getPlotObject()
    {
        return po;
    }

    private final PlotObject po;

}
