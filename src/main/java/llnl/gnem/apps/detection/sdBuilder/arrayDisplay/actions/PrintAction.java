package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayFrame;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class PrintAction extends AbstractAction {

    private static PrintAction ourInstance;

    public static PrintAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new PrintAction(owner);
        }
        return ourInstance;
    }

    private PrintAction(Object owner) {
        super("Print", Utility.getIcon(owner, "miscIcons/print32.gif"));
        putValue(SHORT_DESCRIPTION, "Print Plot");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayDisplayFrame.getInstance().printPlot();
    }
}