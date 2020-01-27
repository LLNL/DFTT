/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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