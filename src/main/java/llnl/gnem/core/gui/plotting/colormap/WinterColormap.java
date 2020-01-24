package llnl.gnem.core.gui.plotting.colormap;

import java.util.Arrays;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Apr 12, 2006
 */
public class WinterColormap extends ArrayColormap {

    /**
     * The constructor for the WinterColormap. This constructor maps the supplied min and max
     * values to the base and top of the color table respectively.
     *
     * @param min The minimum value to be mapped to the color table base.
     * @param max The maximum value to be mapped to the color table top.
     */
    public WinterColormap( double min, double max )
    {
        red = new int[64];
        Arrays.fill( red, 0 );
        green = new int[]{0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76, 80, 85, 89, 93, 97,
                          101, 105, 109, 113, 117, 121, 125, 129, 133, 137, 141, 145, 149, 153, 157,
                          161, 165, 170, 174, 178, 182, 186, 190, 194, 198, 202, 206, 210, 214, 218,
                          222, 226, 230, 234, 238, 242, 246, 250, 255};
        blue = new int[]{255, 252, 250, 248, 246, 244, 242, 240, 238, 236, 234, 232, 230, 228, 226, 224, 222, 220,
                         218, 216, 214, 212, 210, 208, 206, 204, 202, 200, 198, 196, 194, 192, 190, 188, 186, 184,
                         182, 180, 178, 176, 174, 172, 170, 167, 165, 163, 161, 159, 157, 155, 153, 151, 149, 147,
                         145, 143, 141, 139, 137, 135, 133, 131, 129, 127};
        setMinMax( min, max );
    }


}

