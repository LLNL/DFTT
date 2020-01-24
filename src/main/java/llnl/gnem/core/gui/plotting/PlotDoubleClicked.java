package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.transforms.Coordinate;

import java.awt.event.MouseEvent;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Feb 24, 2006
 */
public class PlotDoubleClicked {
    private final MouseEvent me;
    private final Coordinate coordinate;

    public PlotDoubleClicked( MouseEvent me, Coordinate c )
    {
        this.me = me;
        this.coordinate = c;
    }

    public MouseEvent getMe()
    {
        return me;
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }
}
