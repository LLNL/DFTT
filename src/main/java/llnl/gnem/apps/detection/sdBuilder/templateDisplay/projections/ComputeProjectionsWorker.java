/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this currentTemplate file, choose Tools | Templates
 * and open the currentTemplate in the editor.
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
