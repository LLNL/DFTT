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
package llnl.gnem.dftt.core.gui.map.internal.clustering;


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
