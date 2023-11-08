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
package llnl.gnem.dftt.core.gui.plotting.jgeographicplot;

import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.plotobject.DataText;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Line;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.dftt.core.util.Geometry.EModel;
import llnl.gnem.dftt.core.polygon.Vertex;

import java.awt.*;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Jan 26, 2006
 */
public class SmallCircle extends PlotObject {

    private int npts = 100;
    private LineData lineData;
    private Color color = Color.red;

    /**
     * Creates a SmallCircle object centered at epicenter and with a radius of range.
     *
     * @param epicenter The center of the small circle.
     * @param range     The radius of the small circle in degrees
     * @param label     The label to be plotted at the southern-most part of the small circle.
     */
    public SmallCircle( Vertex epicenter, double range, String label )
    {
        lineData = makeLine( epicenter, range, label );


    }

    private LineData makeLine( Vertex epicenter, double range, String label )
    {
        double minLat = Double.MAX_VALUE;
        double associatedLon = 0;
        Vertex[] circle1 = EModel.smallCircle( epicenter, range, npts );
        float[] x = new float[circle1.length];
        float[] y = new float[circle1.length];
        for ( int j = 0; j < x.length; ++j ){
            x[j] = (float) circle1[j].getLat();
            y[j] = (float) circle1[j].getLon();
            if( x[j] < minLat ){
                minLat = x[j];
                associatedLon = y[j];
            }

        }
        Line line1 = new Line( x, y );
        line1.setColor( getColor() );
        DataText text = new DataText( minLat, associatedLon, label );
        return new LineData( line1, text );
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor( Color color )
    {
        this.color = color;
        lineData.line.setColor( getColor() );
    }

    class LineData {
        Line line;
        DataText text;

        public LineData( Line line, DataText text )
        {
            this.line = line;
            this.text = text;
        }
    }


    public void render( Graphics g, JBasicPlot owner )
    {
        if( !visible )
            return;
        lineData.line.render( g, owner );
        lineData.text.render( g, owner );
    }

    public void ChangePosition( JBasicPlot owner, Graphics graphics, double dx, double dy )
    {

    }


}
