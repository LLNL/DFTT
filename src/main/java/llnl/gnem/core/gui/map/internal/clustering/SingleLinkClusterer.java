/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
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
