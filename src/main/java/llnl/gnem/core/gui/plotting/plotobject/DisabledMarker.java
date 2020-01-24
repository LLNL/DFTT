package llnl.gnem.core.gui.plotting.plotobject;


import llnl.gnem.core.gui.plotting.JBasicPlot;

import java.awt.*;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

public class DisabledMarker extends PlotObject {

    /**
     * render this Symbol to the supplied graphics context
     *
     * @param g     The graphics context
     * @param owner The JBasicPlot that owns this symbol
     */
    @Override
    public void render( Graphics g, JBasicPlot owner )
    {
        if( g == null || !visible || owner == null || !owner.getCanDisplay() )
            return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaintMode(); // Make sure that we are not in XOR mode.

        int top = owner.getPlotTop();
        int height = owner.getPlotHeight();
        int left = owner.getPlotLeft();
        int width = owner.getPlotWidth();
        g2d.setPaint(Color.red);
        g2d.drawLine(left,top,left + width, top + height);
        g2d.drawLine(left,top+height,left+width,top);
    }



    @Override
    public void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy) {
        // This object is not allowed to change position.
    }


}