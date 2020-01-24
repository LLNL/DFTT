package llnl.gnem.core.gui.plotting.colormap;

import java.util.Arrays;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Apr 12, 2006
 */
public class CoolColormap extends ArrayColormap {

    /**
     * The constructor for the CoolColormap. This constructor maps the supplied min and max
     * values to the base and top of the color table respectively.
     *
     * @param min The minimum value to be mapped to the color table base.
     * @param max The maximum value to be mapped to the color table top.
     */
    public CoolColormap( double min, double max )
    {
        red = new int[]{0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76, 80, 85, 89, 93, 97,
                        101, 105, 109, 113, 117, 121, 125, 129, 133, 137, 141, 145, 149, 153, 157, 161, 165, 170, 174, 178, 182,
                        186, 190, 194, 98, 202, 206, 210, 214, 218, 222, 226, 230, 234, 238, 242, 246, 250, 255};
        green = new int[]{255, 250, 246, 242, 238, 234, 230, 226, 222, 218, 214, 210, 206, 202, 198, 194, 190, 186, 182, 178,
                          174, 170, 165, 161, 157, 153, 149, 145, 141, 137, 133, 129, 125, 121, 117, 113, 109, 105, 101, 97,
                          93, 89, 85, 80, 76, 72, 68, 64, 60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0};
        blue = new int[64];
        Arrays.fill( blue, 255 );
        setMinMax( min, max );
    }


}

