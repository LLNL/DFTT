package llnl.gnem.core.gui.map.events;

import llnl.gnem.core.seismicData.AbstractEventInfo;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import llnl.gnem.core.gui.map.location.LocationColumn;
import llnl.gnem.core.gui.map.location.LocationModel;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.gui.util.CancelListener;
import llnl.gnem.core.gui.util.CancellableProgressDialog;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 *
 * @param <T>
 */
public abstract class EventModel<T extends AbstractEventInfo> extends LocationModel<T> implements CancelListener {

    protected final CopyOnWriteArrayList<EventRetrievalWorker> workers;

    public EventModel() {
        workers = new CopyOnWriteArrayList<>();
        CancellableProgressDialog.getInstance(EventModel.class).addCancelListener(this);
    }

    public boolean hasEvent(long evid) {
        for (T ei : getLocations()) {
            if (ei.getEvid() == evid) {
                return true;
            }
        }
        return false;
    }

    public void retrieveEvents() {
        if (!retrievalInProgress()) {
            clear();
            cancel();
            doRetrieval();
        }
    }

    public boolean retrievalInProgress() {
        return workers.size() > 0;
    }

    public void setFinished(EventRetrievalWorker worker) {
        workers.remove(worker);
        //workers.clear();  // fix: there may be several works running, only remove from list the one that has finished
        retrievalIsCompleted();
    }

    @Override
    public void cancel() {
        for (EventRetrievalWorker worker : workers) {
            worker.cancel(true);
        }
        workers.clear();
    }

    protected void addEvents(List<T> events) {
        addLocations(events);
    }

    protected abstract void doRetrieval();

    @Override
    public void createColumns() {
        addColumn(new LocationColumn<T>("EVID", Long.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getEvid();
            }
        });
        addColumn(new LocationColumn<T>("LAT", Double.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getLat();
            }
        });
        addColumn(new LocationColumn<T>("LON", Double.class, false, -1) {
            @Override
            public Object getValue(T data) {
                return data.getLon();
            }
        });
        addColumn(new LocationColumn<T>("TIME", TimeT.class, false, 230) {
            @Override
            public Object getValue(T data) {
                return data.getTime();
            }
        });
    }
}