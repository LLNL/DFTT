package llnl.gnem.core.gui.plotting.transforms;

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
