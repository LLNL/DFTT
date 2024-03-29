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
package llnl.gnem.dftt.core.gui.plotting.transforms;

/**
 * A class that describes a coordinate of a point. Currently, this class assumes that
 * a point can be represented by two values. The Coordinate object holds both the
 * coordinates in "World" values and in "Pixel" values necessary to render on the plot.
 * Coordinates are passed as arguments to methods of the CoordinateTransform objects to populate
 * one set of internal values from the other internal set.
 */
public class Coordinate {
    private double x;
    private double y;
    private double worldC1;
    private double worldC2;

    @Override
    public String toString()
    {
        return String.format("x = %f, y = %f, worldc1 = %f, worldc2 = %f",x,y,worldC1,worldC2);
    }
    /**
     * Constructs a Coordinate given its two pixel values from the plot. After construction
     * the world values are still unitialized.
     *
     * @param x The x-pixel value
     * @param y The y-pixel value
     */
    public Coordinate( double x, double y )
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Coordinate object given both the pixel values of the Coordinate
     * and the World values of the Coordinate. No checking for internal consistency is done.
     *
     * @param x       The x-pixel value
     * @param y       The y-pixel value
     * @param worldC1 The first "World" coordinate ( X for (X,) -- Lat for (Lat, Lon) ...)
     * @param worldC2 The second "World" coordinate ( Y for (X,) -- Lon for (Lat, Lon) ...)
     */
    public Coordinate( double x, double y, double worldC1, double worldC2 )
    {
        this.x = x;
        this.y = y;
        this.worldC1 = worldC1;
        this.worldC2 = worldC2;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setX( double v )
    {
        x = v;
    }

    public void setY( double v )
    {
        y = v;
    }

    public double getWorldC1()
    {
        return worldC1;
    }

    public double getWorldC2()
    {
        return worldC2;
    }

    public void setWorldC1( double v )
    {
        worldC1 = v;
    }

    public void setWorldC2( double v )
    {
        worldC2 = v;
    }

}
