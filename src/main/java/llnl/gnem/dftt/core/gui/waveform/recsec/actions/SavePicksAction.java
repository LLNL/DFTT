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
package llnl.gnem.dftt.core.gui.waveform.recsec.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import llnl.gnem.dftt.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.dftt.core.gui.waveform.recsec.MultiStationWaveformDataModel;
import llnl.gnem.dftt.core.gui.util.Utility;

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