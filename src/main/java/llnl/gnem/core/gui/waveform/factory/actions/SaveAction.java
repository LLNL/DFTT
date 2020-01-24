package llnl.gnem.core.gui.waveform.factory.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import llnl.gnem.core.util.ButtonAction;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class SaveAction extends ButtonAction {
    private static SaveAction ourInstance;

    public static SaveAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new SaveAction(owner);
        }
        return ourInstance;
    }

    private SaveAction(Object owner) {
        super("Save", Utility.getIcon(owner, "miscIcons/dbaccept32.gif"));
        putValue(SHORT_DESCRIPTION, "Save Feature to database (Alt-S is shortcut).");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ComponentDataSaver saver = WaveformViewerFactoryHolder.getInstance().getComponentDataSaver();
        saver.saveAll();
    }
}