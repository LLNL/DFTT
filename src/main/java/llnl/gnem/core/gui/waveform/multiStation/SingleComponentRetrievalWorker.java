package llnl.gnem.core.gui.waveform.multiStation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.gui.util.ProgressDialog;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SingleComponentRetrievalWorker extends SwingWorker<Void, BaseSingleComponent> {

    private final long evid;
    private final Collection<BaseSingleComponent> components;
    private final double delta;

    public SingleComponentRetrievalWorker(long evid, double delta) {
        WaveformViewerFactoryHolder.getInstance().getThreeComponentModel().doBeforeRetrieval();

        this.evid = evid;
        this.delta = delta;

        components = new ArrayList<>();
        ProgressDialog.getInstance().setTitle("Retrieving Component Data");
        ProgressDialog.getInstance().setText("Executing query...");
        ProgressDialog.getInstance().setProgressStringPainted(false);
        ProgressDialog.getInstance().setProgressBarIndeterminate(true);
        ProgressDialog.getInstance().setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {
        components.addAll(DAOFactory.getInstance().getSeismogramDAO().getComponentData(evid,delta,ProgressDialog.getInstance()));
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            WaveformViewerFactoryHolder.getInstance().getMSWaveformDataModel().setComponents(components);
            WaveformViewerFactoryHolder.getInstance().getThreeComponentModel().retrievalIsComplete();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving component data.", e);
            }
        } finally {
            ProgressDialog.getInstance().setVisible(false);
            WaveformViewerFactoryHolder.getInstance().getThreeComponentModel().finishRetrieval();
        }
    }
}
