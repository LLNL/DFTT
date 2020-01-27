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
import llnl.gnem.core.gui.filter.FilterGuiContainer;
import llnl.gnem.core.gui.util.Utility;

/**
 * User: Doug
 * Date: Feb 8, 2012
 * Time: 11:25:45 AM
  */
public class UnapplyFilterAction extends AbstractAction {
   private FilterGuiContainer owner;

    public UnapplyFilterAction(FilterGuiContainer owner)
    {
        super( "Un-Filter trace", Utility.getIcon(owner,"miscIcons/undo16.gif") );
        putValue(SHORT_DESCRIPTION, "Un-Apply current filter");
        putValue(MNEMONIC_KEY, KeyEvent.VK_U );
        this.owner = owner;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        owner.getGui().unapplyFilter();
    }

}
