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
package llnl.gnem.apps.detection.sdBuilder.waveformViewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.dftt.core.correlation.CorrelationComponent;
import llnl.gnem.dftt.core.gui.util.ProgressDialog;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class ApplyFilterWorker extends SwingWorker<Void, Void> {

    private final ArrayList<CorrelationComponent> components;
    private final StoredFilter filter;
    private final Collection<SeismogramViewer> viewers;

    public ApplyFilterWorker(Collection<CorrelationComponent> components, StoredFilter filter, Collection<SeismogramViewer> viewerList) {
        this.components = new ArrayList<>(components);
        this.filter = filter;
        viewers = new ArrayList<>(viewerList);
        ProgressDialog.getInstance().setTitle("Filtering detection segments");
        ProgressDialog.getInstance().setText("...");
        ProgressDialog.getInstance().setProgressStringPainted(true);
        ProgressDialog.getInstance().setProgressBarIndeterminate(false);
        ProgressDialog.getInstance().setReferenceFrame(ClusterBuilderFrame.getInstance());
        ProgressDialog.getInstance().setValue(0);
        ProgressDialog.getInstance().setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {
        int max = components.size();
        ProgressDialog.getInstance().setMinMax(0, max);
        AtomicLong counter = new AtomicLong();
        counter.getAndSet(0);
        components.parallelStream().forEach(t->applyOneFilter(t,counter));

        return null;
    }
    
    private void applyOneFilter(CorrelationComponent comp, AtomicLong counter){
        comp.applyFilter(filter);
            ProgressDialog.getInstance().setValue((int)counter.getAndIncrement()+1);
    }
    

    @Override
    public void done() {
        ProgressDialog.getInstance().setVisible(false);
        try {
            get();
            CorrelatedTracesModel.getInstance().updateForChangedTrace();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Error applying filters.", e);
            }
        }
    }
}
