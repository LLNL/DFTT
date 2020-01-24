/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.map.internal.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import llnl.gnem.core.gui.map.internal.Measurable;

/**
 *
 * @author dodge1
 */
public class SingleLinkClusterer implements Clusterer {

	private boolean stopped = false;
	private final double level;
    private List<Cluster> clusters = new ArrayList<Cluster>();

    public SingleLinkClusterer(double threshold) {
        level = threshold;
    }

    @Override
    public Collection<Cluster> getClusters(List<Measurable> items) {
    	stopped = false;

    	Collection<Cluster> clusters = new ArrayList<Cluster>();
    	Iterator<Measurable> iter = items.iterator();
    	while (!stopped && iter.hasNext()) {
    		Measurable item = iter.next();
    		maybeAddToCluster(clusters, item);
    	}
    	return clusters;
    }

    private Cluster maybeAddToCluster(Collection<Cluster> clusters, Measurable item) {
        for (Cluster cluster : clusters) {
            if (cluster.distanceFrom(item) <= level) {
                cluster.addItem(item);
                return cluster;
            }
        }
        Cluster cluster = new Cluster(item);
        clusters.add(cluster);
        return cluster;
    }

	@Override
	public void interrupt() {
		stopped = true;
		clusters.clear();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
