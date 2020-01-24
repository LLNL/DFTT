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
public class RevertAction extends AbstractAction {

    private static RevertAction ourInstance;
    private static final long serialVersionUID = -2888864373988476801L;

    public static RevertAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new RevertAction(owner);
        }
        return ourInstance;
    }

    private RevertAction(Object owner) {
        super("Revert", Utility.getIcon(owner, "miscIcons/undo32.gif"));
        putValue(SHORT_DESCRIPTION, "Revert to Initial Data");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CorrelatedTracesModel.getInstance().revert();
    }
}
