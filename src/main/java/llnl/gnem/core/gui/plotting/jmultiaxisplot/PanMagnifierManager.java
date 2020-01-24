package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import llnl.gnem.core.gui.plotting.Limits;

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
