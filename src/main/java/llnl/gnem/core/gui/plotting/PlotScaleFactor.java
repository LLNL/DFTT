package llnl.gnem.core.gui.plotting;

/**
 * User: dodge1
 * Date: Jun 10, 2005
 * Time: 9:10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlotScaleFactor {
    private double factor;
    public PlotScaleFactor( double v )
    {
        factor = v;
    }

    public double getFactor()
    {
        return factor;
    }
}
