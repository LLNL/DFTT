package llnl.gnem.core.gui.map.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import llnl.gnem.core.gui.util.Utility;


public class ClosePopupAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private JPopupMenu menu;

    public ClosePopupAction(Object owner, JPopupMenu menu) {
        super("Close Menu", Utility.getIcon(owner, "miscIcons/exit32.gif"));
        putValue(SHORT_DESCRIPTION, "Close popup-menu");
        this.menu = menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	menu.setVisible(false);
    }
}
