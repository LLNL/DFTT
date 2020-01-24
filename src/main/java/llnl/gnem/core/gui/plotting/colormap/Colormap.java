package llnl.gnem.core.gui.plotting.colormap;

import java.awt.*;

/**
 * The interface for classes used by the Pcolor class to map from values of the
 * dependent variable to the display color.
 */
public interface Colormap {

    /**
     * Gets the color to represent the current value
     *
     * @param value The value to be mapped to a color.
     * @return The Color corresponding to the input value.
     */
    public Color getColor( final double value );

    /**
     * Resets the mapping between values and their display colors within this
     * Colormap.
     *
     * @param min The value of the dependent variable corresponding to the base
     *            of the color table.
     * @param max The value of the dependent variable corresponding to the top of the color table.
     */
    public void setMinMax( double min, double max );

    public double getMin();

    public double getMax();
}
