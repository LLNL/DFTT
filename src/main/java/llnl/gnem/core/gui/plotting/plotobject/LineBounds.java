package llnl.gnem.core.gui.plotting.plotobject;

/**
 * This is a class used by the Line class when determining appropriate bounds
 * for a plot. It makes sense in the context of Cartesian plots, but is not used when
 * drawing geographic plots.
 */
public class LineBounds {
    public double xmin;
    public double xmax;
    public double ymin;
    public double ymax;
}
