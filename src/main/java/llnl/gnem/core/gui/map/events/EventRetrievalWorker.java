package llnl.gnem.core.gui.map.events;

import llnl.gnem.core.seismicData.AbstractEventInfo;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import llnl.gnem.core.gui.util.CancellableProgressDialog;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author addair1
 * @param <T>
 */
public abstract class EventRetrievalWorker<T extends AbstractEventInfo> extends SwingWorker<Void, T> {
    private final EventModel owner;
    private final JFrame refFrame;

    public EventRetrievalWorker(EventModel owner) {
        this.owner = owner;
        this.refFrame = null;
        showDialog();
    }
    
    public EventRetrievalWorker(EventModel owner, JFrame refFrame) {
        this.owner = owner;
        this.refFrame = refFrame;
        showDialog();
    }

    private void showDialog() {
        CancellableProgressDialog.getInstance(EventModel.class).setTitle("Retrieving Event Data");
        CancellableProgressDialog.getInstance(EventModel.class).setText("Executing query...");
        CancellableProgressDialog.getInstance(EventModel.class).setProgressStringPainted(true);
        CancellableProgressDialog.getInstance(EventModel.class).setProgressBarIndeterminate(true);
        CancellableProgressDialog.getInstance(EventModel.class).setVisible(true);
        CancellableProgressDialog.getInstance(EventModel.class).setReferenceFrame(refFrame);
    }

    @Override
    protected void process(List<T> events) {
        owner.addEvents(events);
    }

    @Override
    public void done() {
        CancellableProgressDialog.getInstance(EventModel.class).setVisible(false);
        owner.setFinished(this);
        try {
            get();
           
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error retrieving event data.", e);
            }
        }
         afterGet();
    }

    public abstract void afterGet();
}
