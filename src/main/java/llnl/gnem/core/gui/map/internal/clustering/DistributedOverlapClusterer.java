package llnl.gnem.core.gui.map.internal.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import llnl.gnem.core.gui.map.internal.Measurable;


public class DistributedOverlapClusterer implements Clusterer {

    private static final int NumMeasurablesPerWorker = 3000;
    private static final int MIN_WORKERS = 2;
    private static final int MAX_WORKERS = 5;

	private final double combineDist;
	private int numWorkers = 1;
    private List<ClustererWorker> workers = new ArrayList<ClustererWorker>();

    public DistributedOverlapClusterer(double dist) {
    	this.combineDist = dist;
    }

	@Override
	public Collection<Cluster> getClusters(List<Measurable> items) {
		startWorkers(items);
		return waitForWorkers();
	}

	@Override
	public void interrupt() {
		stopWorkers();
	}

    private class ClustererWorker extends SwingWorker<Collection<Cluster>, Void> {

    	private List<Measurable> items;

    	public ClustererWorker(List<Measurable> items) {
    		this.items = items;
    		workers.add(this);
    	}

        @Override
        protected Collection<Cluster> doInBackground() throws Exception {
    		Clusterer clusterer = new SingleLinkClusterer(combineDist);
        	return clusterer.getClusters(items);
        }
    }

    private void startWorkers(List<Measurable> items) {
    	final int total = items.size();
        if (total == 0) {
            return;
        }

        numWorkers = Math.min(Math.max(MIN_WORKERS, (total / NumMeasurablesPerWorker)), MAX_WORKERS);
        final int chunkSize = total / numWorkers;
        int start = 0;
        int stop = start + chunkSize;
        for (int i = 0; i < numWorkers; ++i) {
            if (i == (numWorkers - 1)) {
                stop = total - 1;
            }

            ClustererWorker worker = new ClustererWorker(items.subList(start, stop));
            worker.execute();

            start = stop + 1;
            stop = start + chunkSize;
        }
    }

    private void stopWorkers() {
        for (ClustererWorker worker : workers) {
            worker.cancel(true);
        }
        workers.clear();
    }

    private Collection<Cluster> waitForWorkers() {
    	Collection<Cluster> clusters = null;
		try {
			for (ClustererWorker worker : workers) {
				Collection<Cluster> subset = worker.get();
				if (clusters==null)
					clusters = subset;
				else
					clusters.addAll(subset);
			}
		}
		catch (InterruptedException e) {
			stopWorkers();
		}
		catch (ExecutionException e) {
			stopWorkers();
		}
		return clusters;
    }

    @Override
	public String toString() {
		return this.getClass().getSimpleName()+": numClusterers("+numWorkers+")";
	}

}
