package llnl.gnem.apps.detection.sdBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.gui.filter.FilterModel;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 * Created by dodge1
 * Date: Feb 12, 2012
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class FilterRetrievalWorker extends SwingWorker<Void, Void> {

    private final Collection<StoredFilter> filters;

    public FilterRetrievalWorker() {

        filters = new ArrayList<>();
    }

    @Override
    protected Void doInBackground() throws Exception {
        filters.addAll(DAOFactory.getInstance().getFilterDAO().getAllFilters());
        return null;
    }

    @Override
    public void done() {
        try {
            get();
            FilterModel.getInstance().updateModel(filters);
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving filter data.", e);
            }
        }
    }
}
