package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayFrame;
import llnl.gnem.core.gui.util.Utility;

public class MagnifyAction extends AbstractAction {

    private static MagnifyAction ourInstance;

    public static MagnifyAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new MagnifyAction(owner);
        }
        return ourInstance;
    }

    private MagnifyAction(Object owner) {
        super("Magnify", Utility.getIcon(owner, "miscIcons/pageup32.gif"));
        putValue(SHORT_DESCRIPTION, "Increase Magnification");
        putValue(MNEMONIC_KEY, KeyEvent.VK_M);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayDisplayFrame.getInstance().magnifyTraces();
    }
}
