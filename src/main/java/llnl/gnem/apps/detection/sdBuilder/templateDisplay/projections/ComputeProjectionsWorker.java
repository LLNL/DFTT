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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.Projection;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.database.DetectorDAO;
import llnl.gnem.apps.detection.database.SubspaceTemplateDAO;
import llnl.gnem.core.correlation.RealSequenceCorrelator;
import llnl.gnem.core.correlation.util.CorrelationMax;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class ComputeProjectionsWorker extends SwingWorker<Void, Void> {

    private final int detectorid;
    private SubspaceTemplate currentTemplate;
    private final int runid;
    private ProjectionCollection result;

    public ComputeProjectionsWorker(int detectorid, int runid) {
        this.detectorid = detectorid;
        this.runid = runid;
    }

    @Override
    protected Void doInBackground() throws Exception {

        currentTemplate = SubspaceTemplateDAO.getInstance().getSubspaceTemplate(detectorid);
        ArrayList<Integer> detectorids = DetectorDAO.getInstance().getSubspaceDetectorIDsWithDetections(runid);

        List<DetectorProjection> projections = detectorids.parallelStream()
                .filter(t -> isNewDetector(t))
                .map(t -> produceProjection(t))
                .filter(Objects::nonNull).collect(Collectors.toList());
        result = new ProjectionCollection(detectorid, projections);
        return null;
    }

    private boolean isNewDetector(int adetectorid) {
        return adetectorid != detectorid;
    }

    private DetectorProjection produceProjection(int adetectorid) {
        try {
            SubspaceTemplate thatTemplate = SubspaceTemplateDAO.getInstance().getSubspaceTemplate(adetectorid);
            Projection p = new Projection(currentTemplate, thatTemplate);
            int delay = p.getDecimatedDelay();
            double projection = p.getProjectionValue();
            return new DetectorProjection(adetectorid, delay, projection);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void done() {
        try {
            get();
            ProjectionModel.getInstance().setProjections(result);
        } catch (InterruptedException | ExecutionException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving template for detectorid : " + detectorid, ex);
        }
    }

}
