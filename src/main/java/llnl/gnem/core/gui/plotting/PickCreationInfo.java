package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;

import java.awt.event.MouseEvent;

/**
 * Created by: dodge1
 * Date: Jan 13, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PickCreationInfo {
    public PlotObject getSelectedObject()
    {
        if( clickedObject != null && clickedObject instanceof Line){
            return clickedObject;
        }
        else{
            if( owningPlot != null && owningPlot.getLineCount() == 1 ){
                Line[] lines = owningPlot.getLines();
                return lines[0];
            }
            else
                return null;
        }
    }

    public JSubplot getOwningPlot()
    {
        return owningPlot;
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public MouseEvent getMouseEvent()
    {
        return mouseEvent;
    }

    public void setClickedObject( PlotObject clickedObject )
    {
        this.clickedObject = clickedObject;
    }

    private PlotObject clickedObject;
    private final JSubplot   owningPlot;
    private final Coordinate coordinate;
    private final MouseEvent mouseEvent;

    public PickCreationInfo( PlotObject clickedObject, JSubplot   owningPlot,
                             Coordinate coordinate, MouseEvent mouseEvent )
    {
        this.clickedObject = clickedObject;
        this.owningPlot = owningPlot;
        this.coordinate = coordinate;
        this.mouseEvent = mouseEvent;
    }
}
