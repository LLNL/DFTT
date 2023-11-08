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
package llnl.gnem.dftt.core.gui.plotting.colormap;

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

