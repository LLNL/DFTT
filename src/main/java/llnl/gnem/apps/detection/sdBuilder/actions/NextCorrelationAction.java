package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class NextCorrelationAction extends AbstractAction {

    private static NextCorrelationAction ourInstance;

    public static NextCorrelationAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new NextCorrelationAction(owner);
        }
        return ourInstance;
    }

    private NextCorrelationAction(Object owner) {
        super("Next", Utility.getIcon(owner, "miscIcons/next32.gif"));
        putValue(SHORT_DESCRIPTION, "Get Next Group");
        putValue(MNEMONIC_KEY, KeyEvent.VK_N);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CorrelatedTracesModel.getInstance().nextGroup();
    }
}
