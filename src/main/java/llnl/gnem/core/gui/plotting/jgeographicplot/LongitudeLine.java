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
package llnl.gnem.core.gui.plotting.jgeographicplot;

import llnl.gnem.core.gui.plotting.PaintMode;
import llnl.gnem.core.gui.plotting.PenStyle;
import llnl.gnem.core.gui.plotting.plotobject.Line;

import java.awt.*;

/**
 * A line of longitude that can be rendered on a JBasicPlot.
 */
public final class LongitudeLine extends Line {
    private static final int MIN_POINTS_PER_LINE = 10;
    private static final int MAX_LONGITUDE = 180;
    private static final double MAX_RENDERABLE_LONGITUDE = 179.99;
    private static final int DEFAULT_MIN_LATITUDE = -90;
    private static final double DEFAULT_LATITUDE_RANGE = 180.0;
    private static final PenStyle DEFAULT_PENSTYLE = PenStyle.SOLID;
    private double lon;

    /**
     * Constructor for a LongitudeLine that sets the longitude value and the
     * number of points used in drawing the line.
     *
     * @param lon The longitude of the line.
     * @param npts The number of points to use in drawing this line.
     */
    public LongitudeLine(double lon, int npts) {
        this(lon, npts, DEFAULT_MIN_LATITUDE, DEFAULT_LATITUDE_RANGE, DEFAULT_PENSTYLE);
    }

    private LongitudeLine(double lon,
            int npts,
            double startLatitude,
            double latitudeRange,
            PenStyle penStyle) {
        super(new Color(128, 128, 128, 128), PaintMode.COPY, PenStyle.SOLID, 1);
        this.lon = lon;
        if (npts < MIN_POINTS_PER_LINE) {
            npts = MIN_POINTS_PER_LINE;
        }

        if (lon >= MAX_LONGITUDE) {
            lon = MAX_RENDERABLE_LONGITUDE;
        }

        if (lon < -MAX_LONGITUDE) {
            lon = -MAX_RENDERABLE_LONGITUDE;
        }

        float[] x = new float[npts];
        float[] y = new float[npts];
        double dlat = latitudeRange / (npts - 1);

        for (int j = 0; j < npts; ++j) {
            x[j] = (float) (startLatitude + j * dlat);
            y[j] = (float) lon;
        }
        fillXarray(x);
        fillYarray(y);
    }

    public double getLon() {
        return lon;
    }
}
