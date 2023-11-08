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

import llnl.gnem.dftt.core.util.Utility;


/**
 * A class that maps X-values back and forth between real-world values and
 * pixel values. Used for data point conversions, not for positioning elements
 * of the axis.
 *
 * @author Doug Dodge
 */
public class XValueMapper {
    /**
     * Constructor for the XValueMapper object
     */
    public XValueMapper()
    {
        theXaxisDir = XaxisDir.RIGHT;
        XAxisScale = AxisScale.LINEAR;
    }

    /**
     * Gets the X-axis direction
     *
     * @return The X-axis direction
     */
    public XaxisDir getXAxisDir()
    {
        return theXaxisDir;
    }

    /**
     * Sets the X-axis direction
     *
     * @param d The X-axis direction
     */
    public void setXAxisDir( XaxisDir d )
    {
        theXaxisDir = d;
    }

    /**
     * Gets the scaling ( LINEAR or LOG )
     *
     * @return The Scale value
     */
    public AxisScale getXScale()
    {
        return XAxisScale;
    }

    /**
     * Sets the scaling ( LINEAR or LOG )
     *
     * @param s The Scale value
     */
    public void setXScale( AxisScale s )
    {
        XAxisScale = s;
    }

    /**
     * Method called just prior to rendering that is used to set the current
     * scaling between data values and pixel values.
     *
     * @param xmin  Minimum data value of axis
     * @param xmax  Maximum data value of axis
     * @param left  Left edge of axis in pixels
     * @param width Width of axis in pixels
     */
    public void Initialize( double xmin, double xmax, int left, int width )
    {
        if( XAxisScale == AxisScale.LOG ) {
            if( xmax <= 0 )
                throw new IllegalArgumentException( "Xmax is <= 0 and XAxisScale is logarithmic!" );
            if( xmin <= 0 )
                xmin = xmax / 100;
        }
        Xmin = XAxisScale == AxisScale.LINEAR ? xmin : Utility.log10( xmin );
        Xmax = XAxisScale == AxisScale.LINEAR ? xmax : Utility.log10( xmax );
        Left = left;
        Width = width;
        double xrange = Xmax - Xmin;
        XpixelsPerDataUnit = xrange != 0 ? Width / xrange : MaxPixel;
        if( theXaxisDir == XaxisDir.RIGHT ) {
            factor1 = Left - Xmin * XpixelsPerDataUnit;
            factor2 = Xmin - Left / XpixelsPerDataUnit;
        }
        else {
            factor1 = Left + Width + Xmin * XpixelsPerDataUnit;
            factor2 = Xmin + ( Width + Left ) / XpixelsPerDataUnit;
            XpixelsPerDataUnit *= -1;
        }
    }
//---------------------------------------------------------------------------


    /**
     * Gets the X-value in user-space (pixels) for the given real-world X-value
     *
     * @param Xval Real-world X-value
     * @return The x pixel value
     */
    public double getXpixel( double Xval )
    {
        if( XAxisScale == AxisScale.LOG ) {
            if( Xval > 0 )
                Xval = Utility.log10( Xval );
            else
                Xval = Xmin;
        }
        return factor1 + Xval * XpixelsPerDataUnit;
    }
//---------------------------------------------------------------------------


    /**
     * Gets the real-world X-value corresponding to the supplied user-space
     * (pixel) value.
     *
     * @param Xpixel User-space value
     * @return Real-world X-value
     */
    public double getXvalue( double Xpixel )
    {
        double result = factor2 + Xpixel / XpixelsPerDataUnit;
        return XAxisScale == AxisScale.LINEAR ? result : Math.pow( 10.0, result );
    }
//---------------------------------------------------------------------------

    public AxisScale getAxisScale()
    {
        return XAxisScale;
    }
    
    public int getWidth() {
        return Width;
    }


    private double Xmin;
    private double Xmax;
    private double XpixelsPerDataUnit;
    private XaxisDir theXaxisDir;
    private AxisScale XAxisScale;
    private double Left;
    private int Width;
    private double factor1;
    private double factor2;
    private final static int MaxPixel = 32766;
}

