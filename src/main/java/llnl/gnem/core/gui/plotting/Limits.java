package llnl.gnem.core.gui.plotting;

/**
 * Created by: dodge1
 * Date: Jan 27, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class Limits {

    public double getMin()
    {
        return min;
    }

    public double getMax()
    {
        return max;
    }

    private double min;
    private double max;
    public Limits( double min, double max )
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString()
    {
        return  "Limits are " + min + " to " + max;
    }

    public boolean equals( Limits other )
    {
        return other != null && other instanceof Limits &&
                other.min == min && other.max == max;
    }
}
