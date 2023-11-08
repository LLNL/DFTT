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
package llnl.gnem.dftt.core.gui.waveform.factory.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import llnl.gnem.dftt.core.util.ButtonAction;
import llnl.gnem.dftt.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.dftt.core.gui.util.Utility;

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