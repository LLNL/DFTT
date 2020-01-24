package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
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
        ClusterBuilderFrame.getInstance().magnifyTraces();
    }
}
