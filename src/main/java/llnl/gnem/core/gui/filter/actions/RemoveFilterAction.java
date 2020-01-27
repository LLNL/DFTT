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
package llnl.gnem.core.gui.filter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.Utility;
import llnl.gnem.core.gui.waveform.factory.FilterComponentFactoryHolder;

/**
 * User: Doug
 * Date: Feb 8, 2012
 * Time: 11:21:35 AM
 */
public class RemoveFilterAction extends AbstractAction {
    private static RemoveFilterAction ourInstance = null;

    public synchronized static RemoveFilterAction getInstance(Object owner)
    {
        if( ourInstance == null )
            ourInstance = new RemoveFilterAction(owner);
        return ourInstance;
    }

    private RemoveFilterAction(Object owner)
    {
        super( "Remove Selected", Utility.getIcon(owner,"miscIcons/ignore16.gif") );
        putValue(SHORT_DESCRIPTION, "Remove selected filter from list.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_R );
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try {
            FilterComponentFactoryHolder.getInstance().getSSFilterGuiContainer().getGui().removeSelectedFilter();
        }
        catch (Exception e1) {
            ExceptionDialog.displayError(e1);
        }
    }

}