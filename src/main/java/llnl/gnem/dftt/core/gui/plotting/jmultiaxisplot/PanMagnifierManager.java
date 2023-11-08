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
package llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot;

import llnl.gnem.dftt.core.gui.plotting.Limits;

/**
 * Created by: dodge1
 * Date: Jan 27, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class PanMagnifierManager {
    private static double LOG_MAX_MAGNIFICATION = 2.6;
    private static double LOG_SCALE_FACTOR = 2.0;
    private int windowTop;
    private int startY;
    private int startOffset;
    private double yStart;

    private double logMagnificationFactor;
    private double dataRange;


    public PanMagnifierManager( int windowTop,
                                int windowHeight,
                                int startY,
                                double startYMin,
                                double startYMax,
                                double yStart )
    {
        this.windowTop = windowTop;
        this.startY = startY;
        this.startOffset = startY - windowTop;
        this.yStart = yStart;

        logMagnificationFactor = LOG_MAX_MAGNIFICATION / windowHeight;
        dataRange = startYMax - startYMin;
    }

    public Limits getCurrentYLimits( int currentYPixelValue )
    {
        int currentOffset = currentYPixelValue - windowTop;
        int deviation = startOffset - currentOffset;
        double magnification = Math.pow( 10.0, logMagnificationFactor * deviation );
        double newRange = dataRange / magnification;
        double yMin = yStart - newRange / 2;
        double yMax = yStart + newRange / 2;
        return new Limits( yMin, yMax );
    }

    public double getMagnification( int currentYPixelValue )
    {
        double logMagnification = 1.0;
        double denominator = ( startY - windowTop );
        if( denominator != 0 )
            logMagnification = LOG_SCALE_FACTOR * ( startY - currentYPixelValue ) / denominator;
        if( logMagnification < -LOG_SCALE_FACTOR )
            logMagnification = -LOG_SCALE_FACTOR;
        return Math.pow( 10.0, logMagnification );
    }
}
