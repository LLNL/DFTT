package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.database.ConfigurationDAO;
import llnl.gnem.apps.detection.util.Configuration;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GetConfigurationsWorker extends SwingWorker<Void, Void> {

    private final Collection<Configuration> result;

    public GetConfigurationsWorker()
    {
        result = new ArrayList<>();
    }

    @Override
    protected Void doInBackground() throws Exception {
       
        result.addAll(ConfigurationDAO.getInstance().getAllConfigurations());
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().setConfigurationData(result);
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving configurations.", e);
            }
        }
    }
}