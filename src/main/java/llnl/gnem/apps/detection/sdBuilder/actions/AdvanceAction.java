package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class AdvanceAction extends AbstractAction {

    private static AdvanceAction ourInstance;
    private static final long serialVersionUID = -5100373455387557893L;

    public static AdvanceAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new AdvanceAction(owner);
        }
        return ourInstance;
    }

    private AdvanceAction(Object owner) {
        super("Next", Utility.getIcon(owner, "miscIcons/pagedown32.gif"));
        putValue(SHORT_DESCRIPTION, "Next Block of Detections");
        putValue(MNEMONIC_KEY, KeyEvent.VK_N);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClusterBuilderFrame.getInstance().loadDetectionWaveforms();
    }
}
