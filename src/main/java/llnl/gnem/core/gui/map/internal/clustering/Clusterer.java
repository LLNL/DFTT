package llnl.gnem.core.gui.map.internal.clustering;

import java.util.Collection;
import java.util.List;

import llnl.gnem.core.gui.map.internal.Measurable;

public interface Clusterer {

	/**
	 * Generates a group of clusters based on the given list of measurable items.
	 * Nothing resides in local memory.
	 *
	 * @param items
	 * @return
	 */
    public Collection<Cluster> getClusters(List<Measurable> items);

    /**
     * Interrupt any current execution of clustering
     */
    public void interrupt();
}
