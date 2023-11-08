/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.dftt.core.gui.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.dftt.core.dataAccess.DAOFactory;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 * Created by dodge1
 * Date: Feb 12, 2012
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class FilterRetrievalWorker extends SwingWorker<Void, StoredFilter> {

    private final Collection<StoredFilter> allFilters;
    private final Collection<StoredFilter> userFilters;

    public FilterRetrievalWorker() {

        allFilters = new ArrayList<>();
        userFilters = new ArrayList<>();
    }

    @Override
    protected Void doInBackground() throws Exception {
        allFilters.addAll(DAOFactory.getInstance().getFilterDAO().getAllFilters());
        Collection<StoredFilter> tmp = DAOFactory.getInstance().getFilterDAO().getUserFilters();
        if(tmp.isEmpty()){
            DAOFactory.getInstance().getFilterDAO().createDefaultUserFilters();
            tmp = DAOFactory.getInstance().getFilterDAO().getUserFilters();
        }
        userFilters.addAll(tmp);
        return null;
    }

    @Override
    protected void process(List<StoredFilter> events) {
    }

    @Override
    public void done() {
        try {
            get();
            FilterModel.getInstance().updateModel(allFilters,userFilters);
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving filter data.", e);
            }
        }
    }
}
