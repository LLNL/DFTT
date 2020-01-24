package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayFrame;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class UnzoomAllAction extends AbstractAction {

    private static UnzoomAllAction ourInstance;

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
        ArrayDisplayFrame.getInstance().unzoomAll();
    }
}