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
package llnl.gnem.dftt.core.gui.plotting;

import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Line;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;

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
