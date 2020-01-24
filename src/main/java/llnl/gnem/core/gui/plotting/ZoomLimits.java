package llnl.gnem.core.gui.plotting;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Class containing the axis limits for a particular stage of zooming for a
 * single JSubplot.
 *
 * @author Doug Dodge
 */
public class ZoomLimits {
    /**
     * Constructor for the ZoomLimits object
     *
     * @param xmin Minimum limit for the X-axis
     * @param xmax Maximum limit for the X-axis
     * @param ymin Minimum limit for the Y-axis
     * @param ymax Maximum limit for the Y-axis
     */
    public ZoomLimits( double xmin, double xmax, double ymin, double ymax )
    {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public ZoomLimits(Limits xlimits, Limits ylimits)
    {
        xmin = xlimits.getMin();
        xmax = xlimits.getMax();
        ymin = ylimits.getMin();
        ymax = ylimits.getMax();
    }

    @Override
    public String toString() {
        return "ZoomLimits{" + "xmin=" + xmin + ", xmax=" + xmax + ", ymin=" + ymin + ", ymax=" + ymax + '}';
    }

    /**
     * Copy Constructor for the ZoomLimits object
     *
     * @param orig ZoomLimits object to copy
     */
    public ZoomLimits( ZoomLimits orig )
    {
        this.xmin = orig.xmin;
        this.xmax = orig.xmax;
        this.ymin = orig.ymin;
        this.ymax = orig.ymax;
    }

    /**
     * Minimum limit for the X-axis
     */
    public double xmin;
    /**
     * Maximum limit for the X-axis
     */
    public double xmax;
    /**
     * Minimum limit for the Y-axis
     */
    public double ymin;
    /**
     * Maximum limit for the Y-axis
     */
    public double ymax;
}


