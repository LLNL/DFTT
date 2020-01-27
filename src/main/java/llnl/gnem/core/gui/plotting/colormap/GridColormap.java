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
package llnl.gnem.core.gui.plotting.colormap;

import java.awt.*;

/**
 * Created by dodge1
 * Date: Oct 30, 2007
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"MagicNumber"})
public class GridColormap implements Colormap
    {
        private double min;
        private double max;

        public GridColormap()
        {
        }

        /**
         * The constructor for the GridColormap. This constructor maps the supplied min and max
         * values to the base and top of the color table respectively.
         *
         * @param min The minimum value to be mapped to the color table base.
         * @param max The maximum value to be mapped to the color table top.
         */

        public GridColormap(double min, double max)
        {
            setMinMax(min, max);
        }

        /**
         * Resets the mapping between values and their display colors within this
         * Colormap.
         *
         * @param min The value of the dependent variable corresponding to the base
         *            of the color table.
         * @param max The value of the dependent variable corresponding to the top of the color table.
         */
        public void setMinMax(double min, double max)
        {
            if (max == min)
                throw new IllegalArgumentException("Max value must be bigger than Min value!");

            this.min = min;
            this.max = max;
            if (max < min)
            {
                this.max = min;
                this.min = max;
            }
        }


        /**
         * Gets the color to represent the current value
         *
         * @param value The value to be mapped to a color.
         * @return The Color corresponding to the input value.
         */
        public Color getColor(final double value)
        {
            Color color;
            ColorWeight cwgt;
            int r;
            int g;
            int b;
            int a;

            double hf = max;
            double lf = min;
            double df = hf - lf;

            cwgt = new ColorWeight(value, lf, lf, lf + df * .25, lf + df * .5, 255);
            r = cwgt.getWeight();

            cwgt = new ColorWeight(value, lf + .05 * df, lf + df * .15, lf + df * .25, lf + df * .7, 255);
            g = cwgt.getWeight();

            cwgt = new ColorWeight(value, lf + df * .25, lf + df * .45, lf + df * .55, hf, 255);
            b = cwgt.getWeight();
            a = 255;

            color = new Color(r, g, b, a);
            return color;
        }

        public double getMin()
        {
            return min;
        }

        public double getMax()
        {
            return max;
        }


        /**
         * The ColorWeight class is used in Graphics output - matching a data value with a color value
         * used to convert double valued data into color values for plotting
         */
        private static class ColorWeight
        {
            // returns an integer color weight for a data point (datum)
            // uses 4 points to define the weighting function
            // the color is maxweight between low and high corners
            // and tapers to zero from the corner to the minimum and maximum values
            // return value should fall between 0 and 255 and be used in the
            // r,g,b or a field (e.g. Color c = new Color(r,g,b,a))

            public ColorWeight()
            {
                weight = 0;
            }

            /**
             * This constructor determines the Color Weight using a pass band
             *
             * @param datum      - the value at the data point
             * @param minimum    - the smallest data value which will return a non-zero weight
             * @param lowcorner  - the smallest data value which will return the maximum weight
             * @param highcorner - the largest data value which will return the maximum weight
             * @param maximum    - the largest data value which will return a non-zero weight
             * @param maxweight  - that largest weight returned
             *                   the weight = maxweight if the data point falls between the lowcorner value and the highcorner value
             *                   the weight tapers from maxweight to 0 between the highcorner and maximum values
             *                   the weight tapers from 0 to maxweight between the minimum and the lowcorner values
             */
            public ColorWeight(double datum, double minimum, double lowcorner, double highcorner, double maximum, int maxweight)
            {
                if (datum > highcorner)
                {
                    if (highcorner >= maximum)
                        weight = 0;
                    else
                        weight = (int) (((maximum - datum) / (maximum - highcorner)) * maxweight);
                }
                else if (datum > lowcorner)
                {
                    weight = maxweight;
                }
                else
                {
                    if (lowcorner == minimum)
                        weight = maxweight;
                    else
                        weight = (int) (((datum - minimum) / (lowcorner - minimum)) * maxweight);
                }

                if (weight > 255)
                    weight = 255;
                else if (weight < 0)
                    weight = 0;
            }

            public int getWeight()
            {
                return weight;
            }

            private int weight;
        }
    }
