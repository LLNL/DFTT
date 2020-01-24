package llnl.gnem.core.gui.plotting;

import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;

/**
 * User: dodge1
 * Date: Jun 16, 2005
 * Time: 4:43:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PickSelectionStateChange {
    private VPickLine vpl;
    private boolean isSelected;

    public PickSelectionStateChange( VPickLine vpl, boolean isSelected )
    {
        this.vpl = vpl;
        this.isSelected = isSelected;
    }

    public VPickLine getVpl()
    {
        return vpl;
    }

    public boolean isSelected()
    {
        return isSelected;
    }
}
