package llnl.gnem.core.gui.plotting.colormap;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Apr 12, 2006
 */
public class PrismColormap extends ArrayColormap {

    /**
     * The constructor for the PrismColormap. This constructor maps the supplied min and max
     * values to the base and top of the color table respectively.
     *
     * @param min The minimum value to be mapped to the color table base.
     * @param max The maximum value to be mapped to the color table top.
     */
    public PrismColormap( double min, double max )
    {
        red = new int[]{255, 255, 255, 0, 0, 170, 255, 255, 255, 0, 0, 170, 255, 255, 255, 0, 0, 170, 255, 255,
                        255, 0, 0, 170, 255, 255, 255, 0, 0, 170, 255, 255, 255, 0, 0, 170, 255, 255, 255, 0,
                        0, 170, 255, 255, 255, 0, 0, 170, 255, 255, 255, 0, 0, 170, 255, 255, 255, 0, 0, 170,
                        255, 255, 255, 0};
        green = new int[]{0, 127, 255, 255, 0, 0, 0, 127, 255, 255, 0, 0, 0, 127, 255, 255, 0, 0, 0,
                          127, 255, 255, 0, 0, 0, 127, 255, 255, 0, 0, 0, 127, 255, 255, 0, 0, 0,
                          127, 255, 255, 0, 0, 0, 127, 255, 255, 0, 0, 0, 127, 255, 255, 0, 0, 0,
                          127, 255, 255, 0, 0, 0, 127, 255, 255};
        blue = new int[]{0, 0, 0, 0, 255, 255, 0, 0, 0, 0, 255, 255, 0, 0, 0, 0, 255, 255, 0,
                         0, 0, 0, 255, 255, 0, 0, 0, 0, 255, 255, 0, 0, 0, 0, 255, 255, 0, 0,
                         0, 0, 255, 255, 0, 0, 0, 0, 255, 255, 0, 0, 0, 0, 255, 255, 0, 0, 0,
                         0, 255, 255, 0, 0, 0, 0};

        setMinMax( min, max );
    }


}

