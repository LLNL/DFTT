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
package llnl.gnem.dftt.core.gui.filter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.dftt.core.gui.filter.FilterAdditionWorker;
import llnl.gnem.dftt.core.gui.filter.FilterGui;
import llnl.gnem.dftt.core.gui.filter.FilterModel;
import llnl.gnem.dftt.core.gui.util.Utility;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 * User: Doug Date: Feb 8, 2009 Time: 11:19:02 AM
 */
public class AddCurrentAction extends AbstractAction {

    private static AddCurrentAction ourInstance = null;
    private static final long serialVersionUID = -758004814166881039L;
    private FilterGui associatedGui = null;

    public synchronized static AddCurrentAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new AddCurrentAction(owner);
        }
        return ourInstance;
    }

    private AddCurrentAction(Object owner) {
        super("Add Current", Utility.getIcon(owner, "miscIcons/add-16.gif"));
        putValue(SHORT_DESCRIPTION, "Add current specifications to the filter list.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        StoredFilter filter = associatedGui.getUserDefinedFilter();
        if (!FilterModel.getInstance().addFilterFromPool(filter)) { // We did not have this filter in the allFilters pool
            new FilterAdditionWorker(filter).execute(); // So add it to database
        }

    }

    public void setFilterGui(FilterGui gui) {
        associatedGui = gui;
    }
}
