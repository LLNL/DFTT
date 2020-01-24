package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;

import java.awt.event.MouseEvent;

/**
 * Created by dodge1
 * Date: Feb 7, 2008
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class PlotClicked {
    private final MouseEvent me;
    private final Coordinate coordinate;
    private final JSubplot subplot;

    public PlotClicked(MouseEvent me, Coordinate c, JSubplot subplot)
    {
        this.me = me;
        this.coordinate = c;
        this.subplot = subplot;
    }

    public MouseEvent getMe()
    {
        return me;
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public JSubplot getSubplot() {
        return subplot;
    }
}

