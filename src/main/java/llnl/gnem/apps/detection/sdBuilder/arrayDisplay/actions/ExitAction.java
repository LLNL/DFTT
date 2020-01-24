package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayFrame;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class ExitAction extends AbstractAction {

    private static ExitAction ourInstance;

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
        ArrayDisplayFrame.getInstance().setVisible(false);
    }
}
