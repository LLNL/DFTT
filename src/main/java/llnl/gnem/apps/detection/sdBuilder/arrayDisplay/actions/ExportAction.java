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
public class ExportAction extends AbstractAction {

    private static ExportAction ourInstance;

    public static ExportAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ExportAction(owner);
        }
        return ourInstance;
    }

    private ExportAction(Object owner) {
        super("Export", Utility.getIcon(owner, "miscIcons/export32.gif"));
        putValue(SHORT_DESCRIPTION, "Export Plot");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayDisplayFrame.getInstance().exportPlot();
    }
}