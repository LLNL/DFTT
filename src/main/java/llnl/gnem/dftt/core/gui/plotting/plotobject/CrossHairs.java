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
package llnl.gnem.dftt.core.gui.plotting.plotobject;

import java.awt.Color;
import java.awt.Graphics;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.PaintMode;
import llnl.gnem.dftt.core.gui.plotting.PenStyle;

/**
 *
 * @author dodge1
 */
public class CrossHairs  extends PlotObject{
    private final Line horizLine;
    private final Line vertLine;
    private static final int npts = 10;

    public CrossHairs( double xmin, double xmax, double ymin, double ymax, double xval, double yval, Color color, PenStyle penStyle){
        horizLine = addHorizontalLine(  xmin ,  xmax,  yval,  npts, color , penStyle );
        vertLine = addVerticalLine( xval,  ymin,  ymax,  npts, color , penStyle );
    }
    
    
    
    
    @Override
    public void render(Graphics g, JBasicPlot owner) {
        horizLine.render(g, owner);
        vertLine.render(g, owner);
    }

    @Override
    public void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy) {
        horizLine.ChangePosition(owner, graphics, dx, dy);
        vertLine.ChangePosition(owner, graphics, dx, dy);
    }
    
    private static Line addHorizontalLine(double xmin, double xmax, double yval, int npts, Color c, PenStyle style) {
        float dx = (float) (xmax - xmin) / (npts - 1);
        float[] x = new float[npts];
        float[] y = new float[npts];
        for (int j = 0; j < npts; ++j) {
            x[j] = (float) xmin + dx * j;
            y[j] = (float) yval;
        }
        return new Line(x, y, c, PaintMode.COPY, style, 1);
    }

    private static Line addVerticalLine(double xval, double ymin, double ymax, int npts, Color c, PenStyle style) {
        float dy = (float) (ymax - ymin) / (npts - 1);
        float[] x = new float[npts];
        float[] y = new float[npts];
        for (int j = 0; j < npts; ++j) {
            x[j] = (float) xval;
            y[j] = (float) ymin + dy * j;
        }
        return new Line(x, y, c, PaintMode.COPY, style, 1);
    }    
    
    
}
