package llnl.gnem.core.gui.map.internal.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import llnl.gnem.core.gui.map.internal.Measurable;

public class NeverCombineClusterer implements Clusterer {

	private boolean running;

	@Override
	public Collection<Cluster> getClusters(List<Measurable> items) {
		running = true;

		List<Cluster> clusters = new ArrayList<Cluster>();
		Iterator<Measurable> iter = items.iterator();
		while (running && iter.hasNext()) {
			Measurable item = iter.next();
			clusters.add(new Cluster(item));
		}
		return clusters;
	}

	@Override
	public void interrupt() {
		running = false;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
