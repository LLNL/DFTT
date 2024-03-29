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
package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DeleteDetectionWorker extends SwingWorker<Void, Void> {

    private static final Logger log = LoggerFactory.getLogger(DeleteDetectionWorker.class);

    private final Detection detection;
    private final DefaultMutableTreeNode node;

    public DeleteDetectionWorker(Detection detection, DefaultMutableTreeNode node) {
        this.detection = detection;
        this.node = node;
    }

    @Override
    protected Void doInBackground() throws Exception {

        DetectionDAOFactory.getInstance().getDetectionDAO().deleteDetection(detection);
        return null;

    }

    @Override
    public void done() {
        try {
            get();
            ConfigDataModel.getInstance().removeNode(node);
            CorrelatedTracesModel.getInstance().detectionWasDeleted(detection.getDetectionid());
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                log.warn("Error removing detector.", e);
            }
        }
    }
}