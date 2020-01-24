package llnl.gnem.core.gui.plotting.colormap;

import java.awt.*;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Apr 12, 2006
 */
public class ArrayColormap implements Colormap {
    protected int[] red;
    protected int[] green;
    protected int[] blue;
    private double min;
    private double max;
    private double factor;

    /**
     * Resets the mapping between values and their display colors within this
     * Colormap.
     *
     * @param min The value of the dependent variable corresponding to the base
     *            of the color table.
     * @param max The value of the dependent variable corresponding to the top of the color table.
     */
    public void setMinMax( double min, double max )
    {
        if( max == min )
            throw new IllegalArgumentException( "Max value must be bigger than Min value!" );

        this.min = min;
        this.max = max;
        if( max < min ){
            double tmp = max;
            max = min;
            min = tmp;
        }
        double range = max - min;

        factor = ( blue.length - 1 ) / range;
    }

    /**
     * Gets the color to represent the current value
     *
     * @param value The value to be mapped to a color.
     * @return The Color corresponding to the input value.
     */
    public Color getColor( final double value )
    {
        int idx = getIndex( value );
        return new Color( red[idx], green[idx], blue[idx] );
    }

    private int getIndex( double value )
    {
        value = Math.max( Math.min( max, value ), min );
        return (int) ( ( value - min ) * factor );
    }

    public double getMin()
    {
        return min;
    }

    public double getMax()
    {
        return max;
    }
}
