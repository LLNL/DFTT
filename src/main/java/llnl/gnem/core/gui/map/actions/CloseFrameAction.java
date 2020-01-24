package llnl.gnem.core.gui.map.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import llnl.gnem.core.gui.util.Utility;


public class CloseFrameAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private JFrame frame;

    public CloseFrameAction(Object owner, JFrame frame) {
        super("Close Window", Utility.getIcon(owner, "miscIcons/exit32.gif"));
        putValue(SHORT_DESCRIPTION, "Close this window");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	frame.setVisible(false);
    }
}
