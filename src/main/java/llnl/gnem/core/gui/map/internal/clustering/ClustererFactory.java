package llnl.gnem.core.gui.map.internal.clustering;


public class ClustererFactory {

    private static final int MIN_TO_CLUSTER = 5000;

    /**
     * Create the Measurable item Clusterer
     *
     * @param numVisibleItems
     * @param elevationLevel
     * @param zoomLevel
     * @param distribute
     * @return
     */
    public static Clusterer create(final int numVisibleItems, final double combineDistKm, boolean distribute) {

    	// If we are fully zoomed in, or we do not meet them minimum number of visible items
    	// then return a Clusterer which creates a Cluster per Measurable
    	if (numVisibleItems <= MIN_TO_CLUSTER) {
    		return new NeverCombineClusterer();
    	}
    	else if (! distribute) {
    		// Create the Single Link Clusterer
    		return new SingleLinkClusterer(combineDistKm);
    	}

    	// Seems to be a fair amount of visible items, distribute the job
    	return new DistributedOverlapClusterer(combineDistKm);
	}
}
