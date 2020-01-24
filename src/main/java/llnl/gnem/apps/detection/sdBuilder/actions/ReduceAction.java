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
public class ReduceAction extends AbstractAction {

    private static ReduceAction ourInstance;

    public static ReduceAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ReduceAction(owner);
        }
        return ourInstance;
    }

    private ReduceAction(Object owner) {
        super("Reduce", Utility.getIcon(owner, "miscIcons/pagedown32.gif"));
        putValue(SHORT_DESCRIPTION, "Decrease Magnification");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClusterBuilderFrame.getInstance().reduceTraces();
    }
}