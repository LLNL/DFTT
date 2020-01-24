package llnl.gnem.core.gui.waveform.multiStation.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.multiStation.SingleComponentRetrievalWorker;
import llnl.gnem.core.gui.util.Utility;


/**
 * Created by dodge1
 * Date: Feb 12, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */


@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class LoadStationWaveformAction extends AbstractAction {

    private static final long serialVersionUID = 8054704459447973842L;

    private double delta = 90.0;
    private long evid = -1;

    public LoadStationWaveformAction(Object owner) {
        super("Waveforms", Utility.getIcon(owner, "miscIcons/recsec32.gif"));
        putValue(SHORT_DESCRIPTION, "Display Multi-station Waveforms.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_W);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	if( isOkToRetrieveWaveforms()){
          
            WaveformViewerFactoryHolder.getInstance().getMSWaveformDataModel().clear();
            new SingleComponentRetrievalWorker(evid,delta).execute();
            WaveformViewerFactoryHolder.getInstance().getMSWaveformDataModel().setActive(true);
        }
    }

    public boolean isOkToRetrieveWaveforms() {
        return evid > 0;
    }

    public void setEvid(long evid) {
        this.evid = evid;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }


}