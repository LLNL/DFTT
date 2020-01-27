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
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.FilterComponentFactoryHolder;

/**
 * Created by IntelliJ IDEA.
 * User: Doug
 * Date: Feb 2, 2012
 * Time: 9:25:09 PM
 */
public class OpenCorrFilterDialogAction extends AbstractAction {

    private static OpenCorrFilterDialogAction ourInstance = null;
    private static final long serialVersionUID = 7506223831209067435L;

    public static OpenCorrFilterDialogAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new OpenCorrFilterDialogAction(owner);
        }
        return ourInstance;
    }

    private OpenCorrFilterDialogAction(Object owner) {
        super("Filter", Utility.getIcon(owner, "miscIcons/ShowFilter32.gif"));
        putValue(SHORT_DESCRIPTION, "Open Correlation Trace Filter Dialog.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FilterComponentFactoryHolder.getInstance().getSSFilterGuiContainer().setVisible(true);
    }
}
