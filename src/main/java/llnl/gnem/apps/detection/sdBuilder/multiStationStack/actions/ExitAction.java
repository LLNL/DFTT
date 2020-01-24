package llnl.gnem.apps.detection.sdBuilder.multiStationStack.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.multiStationStack.MultiStationStackFrame;
import llnl.gnem.core.gui.util.Utility;

public class ExitAction extends AbstractAction {

    private static ExitAction ourInstance;
    private static final long serialVersionUID = -6871232834784442918L;

    public static ExitAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ExitAction(owner);
        }
        return ourInstance;
    }

    private ExitAction(Object owner) {
        super("Exit", Utility.getIcon(owner, "miscIcons/exit32.gif"));
        putValue(SHORT_DESCRIPTION, "Click to exit dialog.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MultiStationStackFrame.getInstance().setVisible(false);
    }
}
