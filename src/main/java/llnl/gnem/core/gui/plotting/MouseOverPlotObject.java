package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

/**
 * User: dodge1
 * Date: Jan 5, 2004
 * Time: 9:34:44 AM
 * To change this template use Options | File Templates.
 */
public class MouseOverPlotObject {
    public MouseOverPlotObject( PlotObject po )
    {
        this.po = po;
    }

    public PlotObject getPlotObject()
    {
        return po;
    }

    private PlotObject po;
}
