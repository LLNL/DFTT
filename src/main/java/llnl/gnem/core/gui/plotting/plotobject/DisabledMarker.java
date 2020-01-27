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