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
package llnl.gnem.core.gui.filter;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.gui.waveform.factory.FilterComponentFactoryHolder;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 * Created by dodge1
 * Date: Feb 12, 2012
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class FilterAdditionWorker extends SwingWorker<Void, Void> {

    private final StoredFilter filterToAdd;
    private StoredFilter addedFilter = null;

    public FilterAdditionWorker(StoredFilter filter) {

        filterToAdd = filter;



    }

    @Override
    protected Void doInBackground() throws Exception {

        addedFilter = DAOFactory.getInstance().getFilterDAO().maybeAddFilter(filterToAdd);
        return null;
    }

    @Override
    public void done() {
        try {
            get();
            if (addedFilter != null) {
                FilterModel.getInstance().addNewFilter(addedFilter);
            }
            else{
                FilterModel.getInstance().addFilterFromPool(filterToAdd);
            }
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error adding filter.", e);
            }
        }
    }
}
