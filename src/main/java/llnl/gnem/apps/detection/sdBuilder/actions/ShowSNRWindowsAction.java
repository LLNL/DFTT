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
package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.dftt.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ShowSNRWindowsAction extends AbstractAction {

    private static ShowSNRWindowsAction ourInstance;
    private static final long serialVersionUID = -9143217544910179470L;
    private boolean showSNRWindow = false;

    public static ShowSNRWindowsAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ShowSNRWindowsAction(owner);
        }
        return ourInstance;
    }

    private ShowSNRWindowsAction(Object owner) {
        super("Show SNR Windows", Utility.getIcon(owner, "miscIcons/showsignalnoise.png"));
        putValue(SHORT_DESCRIPTION, "Show the windows used to calculate SNR for sorting traces.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showSNRWindow = !showSNRWindow;
        ClusterBuilderFrame.getInstance().setSNRWindowVisible(showSNRWindow);
        if (showSNRWindow) {
            putValue(SHORT_DESCRIPTION, "Hide the windows used to calculate SNR for sorting traces.");
        } else {
            putValue(SHORT_DESCRIPTION, "Show the windows used to calculate SNR for sorting traces.");
        }
    }

    public boolean isShowSNRWindow() {
        return showSNRWindow;
    }

}
