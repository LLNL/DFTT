package llnl.gnem.core.gui.waveform.recsec.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.recsec.MultiStationWaveformDataModel;
import llnl.gnem.core.gui.util.Utility;

/**
 * User: Doug Date: Jan 24, 2009 Time: 4:20:34 PM
 * COPYRIGHT NOTICE Copyright (C) 2008 Doug Dodge.
 */
public class SavePicksAction extends AbstractAction {

    private static SavePicksAction ourInstance = null;

    public synchronized static SavePicksAction getInstance(JComponent owner) {
        if (ourInstance == null) {
            ourInstance = new SavePicksAction(owner);
        }
        return ourInstance;
    }

    private SavePicksAction(JComponent owner) {
        super("Save", Utility.getIcon(owner, "miscIcons/Save16.gif"));
        putValue(SHORT_DESCRIPTION, "Save All Picks.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MultiStationWaveformDataModel dataModel = WaveformViewerFactoryHolder.getInstance().getMSWaveformDataModel();
        dataModel.savePicks();
        this.setEnabled(false);
    }
}