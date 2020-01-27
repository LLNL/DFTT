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
package llnl.gnem.core.gui.plotting.histogram;


import llnl.gnem.core.gui.plotting.plotobject.JPolygon;

import java.awt.geom.Point2D;


/**
 * Class to accumulate data for a single bin of a Histogram plot and to produce the JPolygon
 * for that bin.
 * Created by: dodge1
 * Date: Jan 6, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class HistogramBin {

    private float minValue;
    private float maxValue;
    private int nValues;

    /**
     * Gets the number of values stored in this HistogramBin. This will be the height displayed
     * in the Histogram plot.
     *
     * @return The number of values in this bin.
     */
    public int getNumberOfValues()
    {
        return nValues;
    }

    /**
     * Constructs a HistogramBin for the range minValue(inclusive) to maxValue(exclusive).
     * The number of values in the bin is set to 0.
     *
     * @param minValue The minimum (inclusive) value in this bin.
     * @param maxValue The maximum (exclusive) value in this bin.
     */
    public HistogramBin( float minValue, float maxValue )
    {
        super();
        nValues = 0;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    JPolygon getBar()
    {
        Point2D[] vertex = new Point2D.Float[4];
        vertex[0] = new Point2D.Float( minValue, 0.0f );
        vertex[1] = new Point2D.Float( minValue, (float) nValues );
        vertex[2] = new Point2D.Float( maxValue, (float) nValues );
        vertex[3] = new Point2D.Float( maxValue, 0.0f );
        return new JPolygon( vertex );
    }

    void incrementCount()
    {
        ++nValues;
    }

    boolean containsValue( float value )
    {
        return value >= minValue && value < maxValue;
    }

    @Override
    public String toString()
    {
        return "ObservationBin from " + minValue + " to " + maxValue + " has " + nValues + " values.";
    }
}
