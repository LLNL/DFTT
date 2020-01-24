package llnl.gnem.core.gui.plotting;

/**
 * User: dodge1
 * Date: Jun 10, 2005
 * Time: 5:09:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PanInfo {
    private PanStyle panStyle;
    private boolean isComplete;

    public PanInfo( PanStyle style, boolean isComplete )
    {
        panStyle = style;
        this.isComplete = isComplete;
    }

    public PanStyle getPanStyle()
    {
        return panStyle;
    }

    public boolean isComplete()
    {
        return isComplete;
    }
}
