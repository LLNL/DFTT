
package llnl.gnem.core.gui.waveform;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import llnl.gnem.core.waveform.components.BaseSingleComponent;

/**
 * User: Doug Date: Feb 2, 2012 Time: 9:25:09 PM
 */
public abstract class SingleComponentOperationAction extends AbstractAction {

    protected BaseSingleComponent component;


    protected SingleComponentOperationAction(String name, ImageIcon icon)
    {
        super(name, icon);
    }

    public void setComponent(BaseSingleComponent component) {
        this.component = component;
    }
}
