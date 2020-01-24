package llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackFrame;
import llnl.gnem.core.gui.util.Utility;

public class UnzoomAllAction extends AbstractAction {

    private static UnzoomAllAction ourInstance;
    private static final long serialVersionUID = -6123527364951155647L;

    public static UnzoomAllAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new UnzoomAllAction(owner);
        }
        return ourInstance;
    }

    private UnzoomAllAction(Object owner) {
        super("Unzoom-All", Utility.getIcon(owner, "miscIcons/unzoomall32.gif"));
        putValue(SHORT_DESCRIPTION, "Un-zoom all axes to original view.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MultiStationStackFrame.getInstance().unzoomAll();
        MultiStationStackFrame.getInstance().returnFocusToPlot();
    }
}