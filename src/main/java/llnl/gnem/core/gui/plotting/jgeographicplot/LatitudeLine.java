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
package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.PenStyle;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.util.Geometry.EModel;

import java.awt.*;

/**
 * A line of latitude that can be rendered on a JBasicPlot.
 */
public final class LatitudeLine {
    private static final int MIN_LINE_POINTS = 10;

    private final static double MIN_LONGITUDE = -180;
    private final static double MAX_LONGITUDE = 180;


    private double lineLatitude;
    private PenStyle penstyle;
    private Color color = new Color( 128, 128, 128, 128 );
    private int npts;

    /**
     * Constructor for the LatitudeLine object that sets the latitude of this line
     * and the number of points used in drawing the line.
     *
     * @param latitudeValue The latitude of the line.
     * @param npts          The number of points used in drawing the line.
     */
    public LatitudeLine( double latitudeValue, int npts )
    {
        penstyle = PenStyle.SOLID;
        this.lineLatitude = latitudeValue;
        this.npts = Math.max( npts, MIN_LINE_POINTS );
    }


    Line createLine( double originLat, double originLon, double renderRadius )
    {
        double delta = EModel.getGreatCircleDelta( originLat, originLon, lineLatitude, originLon );
        if( delta <= renderRadius ){
            double maxLon = MAX_LONGITUDE;
            double minLon = MIN_LONGITUDE;
            float[] x = new float[npts];
            float[] y = new float[npts];
            double dlon = ( maxLon - minLon ) / ( npts - 1 );
            for ( int j = 0; j < npts; ++j ){
                x[j] = (float) lineLatitude;
                y[j] = (float) ( minLon + j * dlon );
            }
            Line line = new Line( x, y );
            line.setColor( color );
            line.setPenStyle( penstyle );
            return line;
        }
        else{
            return null;
        }
    }

    public double getLineLatitude()
    {
        return lineLatitude;
    }

    public void setColor( Color color )
    {
        this.color = color;
    }
}
