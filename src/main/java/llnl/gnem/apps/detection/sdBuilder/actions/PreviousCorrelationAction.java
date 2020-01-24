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
public class PreviousCorrelationAction extends AbstractAction {

    private static PreviousCorrelationAction ourInstance;

    public static PreviousCorrelationAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new PreviousCorrelationAction(owner);
        }
        return ourInstance;
    }

    private PreviousCorrelationAction(Object owner) {
        super("Previous", Utility.getIcon(owner, "miscIcons/previous32.gif"));
        putValue(SHORT_DESCRIPTION, "Get Previous Group");
        putValue(MNEMONIC_KEY, KeyEvent.VK_P);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CorrelatedTracesModel.getInstance().previousGroup();
    }
}
