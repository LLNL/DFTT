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
package llnl.gnem.dftt.core.gui.map.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.SwingWorker;

import llnl.gnem.dftt.core.gui.map.MapListener;
import llnl.gnem.dftt.core.gui.map.ViewPort;
import llnl.gnem.dftt.core.gui.map.internal.clustering.Cluster;
import llnl.gnem.dftt.core.gui.map.internal.clustering.Clusterer;
import llnl.gnem.dftt.core.gui.map.internal.clustering.ClustererFactory;
import llnl.gnem.dftt.core.util.ApplicationLogger;

public abstract class RenderItemManager<T extends Measurable> implements MapListener {

    private static final Level logLevel = Level.FINE;
    private String measurableTypeName;
    // can't update this to CopyOnWriteList as Java7 does not allow us to sort it using Collections.sort!
    private List<T> allItems = Collections.synchronizedList(new ArrayList<T>());


    /**
     * Resets both the all and viewable item tracking lists
     */
    protected void reset() {
        stopActiveClustering();
        allItems.clear();
    }

    /**
     * Adds the given render item to items that are tracked, and determines if
     * the item should be added to the viewable list. Viewable item is returned
     * if item is viewable, otherwise null.
     *
     */
    protected void addItem(final T item) {
        allItems.add(item);
        if (measurableTypeName == null) {
            measurableTypeName = item.getClass().getSimpleName();
        }
    }

    protected void removeItem(final T item) {
        allItems.remove(item);
    }

    /**
     * Returns the list of all items being tracked
     *
     * @return
     */
    public List<T> getAllItems() {
        return allItems;
    }

    /**
     * Checks the item to determine if it is viewable within the given view port
     *
     * @param viewport
     * @param item
     * @return boolean
     */
    protected boolean evaluateAsViewItem(ViewPort viewport, final Measurable item) {
        return (viewport == null || item.isInside(viewport) || item.intersects(viewport));
    }

    /**
     * Clears out all of the items in user's viewing area.
     */
    protected abstract void clearViewItems();

    /**
     * Called when a tracked item enters the user's viewing area.
     *
     */
    protected abstract void viewItemAdded(T item);

    /**
     * Called when the containing view should be refreshed
     */
    protected abstract void refreshView();

    public void retrievalIsCompleted() {
        stopActiveClustering();
        launchRequest(true);
    }

    @Override
    public void viewChanged() {
        stopActiveClustering();
        launchRequest(true);
    }

    private Thread launchThread;

    public class ViewUpdater extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            launchRequest(false);
            return null;
        }
    }
    private static final int NumMeasurablesPerWorker = 8000;
    private Clusterer clusterer;
    private List<VisibilityWorker> workers = new ArrayList<VisibilityWorker>();

    public class VisibilityWorker extends SwingWorker<Void, Void> {

        private final int startNdx, stopNdx;
        private final ViewPort viewPort;
        private List<Measurable> visibleItems;

        public VisibilityWorker(int start, int stop, ViewPort vp, List<Measurable> visItems) {
            startNdx = start;
            stopNdx = stop;
            viewPort = vp;
            visibleItems = visItems;
            workers.add(this);
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (int i = startNdx; i <= stopNdx; ++i) {
                Measurable item = allItems.get(i);
                if (evaluateAsViewItem(viewPort, item)) {
                    visibleItems.add(item);
                }
            }
            return null;
        }
    }

    private synchronized void launchRequest(boolean useSwingWorker) {
        if (useSwingWorker) {
            ViewUpdater updater = new ViewUpdater();
            updater.execute();
            return;
        }
        launchThread = Thread.currentThread();

        long startTime = System.currentTimeMillis();

        ApplicationLogger.getInstance().log(logLevel, "ViewUpdate(" + measurableTypeName + ") Start=> all(" + allItems.size() + ")");

        // Determine the visible items
        final ViewPort viewPort = ViewportPositionMonitor.getInstance().getCurrentViewport();
        final double combineDistKm = ViewportPositionMonitor.getInstance().getCombineDistKm();
        final int elevationLevel = ViewportPositionMonitor.getInstance().getCurrentElevationLevel();

        List<Measurable> visibleItems = getVisibleItemsParallel(viewPort);

        // Create appropriate Clusterer for visible items and zoom
        final boolean distribute = true;
        clusterer = ClustererFactory.create(visibleItems.size(), combineDistKm, distribute);

        // Create clusters for visible items
        Collection<Cluster> clusters = clusterer.getClusters(visibleItems);

        // Clear out previous viewed items
        clearViewItems();

        // Loop over clusters and add to view
        for (Cluster<T> cluster : clusters) {
            viewItemAdded(cluster.getRepresentative());
        }

        long stopTime = System.currentTimeMillis();

        // Refresh user view
        refreshView();

        ApplicationLogger.getInstance().log(logLevel, "ViewUpdate(" + measurableTypeName + ") Finished!=>" + clusterer + " elevLevel(" + elevationLevel + ") combineDist(" + combineDistKm + ") time(" + (stopTime - startTime) + ")ms all(" + allItems.size() + ") inViewPort(" + visibleItems.size() + ") rendered(" + clusters.size() + ")");

        clusterer = null;
        launchThread = null;
    }

    /**
     * Retrieve the list of currently visible Measurable(s), operating over the
     * allItems list in parallel.
     *
     * @param viewPort
     * @return
     */
    private List<Measurable> getVisibleItemsParallel(ViewPort viewPort) {

        List<Measurable> visibleItems = Collections.synchronizedList(new ArrayList<Measurable>());

        final int total = allItems.size();
        if (total == 0) {
            return visibleItems;
        }

        final int numWorkers = Math.max(2, (total / NumMeasurablesPerWorker));
        final int chunkSize = total / numWorkers;
        int start = 0;
        int stop = start + chunkSize;
        for (int i = 0; i < numWorkers; ++i) {
            if (i == (numWorkers - 1)) {
                stop = total - 1;
            }

            VisibilityWorker worker = new VisibilityWorker(start, stop, viewPort, visibleItems);
            worker.execute();

            start = stop + 1;
            stop = start + chunkSize;
        }

        waitForWorkers();

        return visibleItems;
    }

    /**
     * Wait for all swing workers to finish executing, or if one should
     * exception out, stop all the other workers.
     */
    private void waitForWorkers() {
        final long start = System.currentTimeMillis();
        try {
            for (VisibilityWorker worker : workers) {
                worker.get();
            }
        } catch (InterruptedException e) {
            stopWorkers();
        } catch (ExecutionException e) {
            stopWorkers();
        }
        final long stop = System.currentTimeMillis();
        ApplicationLogger.getInstance().log(logLevel, "Determining visibility(" + measurableTypeName + ") time(" + (stop - start) + ")ms");
    }

    /**
     * Stop all swing workers from executing, and clear the list.
     */
    private void stopWorkers() {
        for (VisibilityWorker worker : workers) {
            worker.cancel(true);
        }
        workers.clear();
    }

    /**
     * Stop the currently active clustering
     */
    private synchronized void stopActiveClustering() {

        stopWorkers();

        if (clusterer != null) {
            clusterer.interrupt();
            clusterer = null;
        }
        if (launchThread != null) {
            launchThread.interrupt();
            launchThread = null;
        }
    }
}
