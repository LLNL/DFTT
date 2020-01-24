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
