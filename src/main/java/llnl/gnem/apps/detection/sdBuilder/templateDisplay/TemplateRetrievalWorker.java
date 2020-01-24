/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.GetProjectionsAction;
import java.sql.Connection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.database.DetectorDAO;
import llnl.gnem.apps.detection.database.StreamDAO;
import llnl.gnem.apps.detection.database.SubspaceTemplateDAO;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.ProjectionModel;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class TemplateRetrievalWorker extends SwingWorker<Void, Void> {

    private final int detectorid;
    private SubspaceTemplate template;
    private StoredFilter streamFilter;
    private String detectorInfo;

    public TemplateRetrievalWorker(int detectorid) {
        this.detectorid = detectorid;
    }

    @Override
    protected Void doInBackground() throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            Object obj = SubspaceTemplateDAO.getInstance().getEmpiricalTemplate(conn, detectorid);
            template = (SubspaceTemplate) obj;
            streamFilter = StreamDAO.getInstance().getStreamFilter(detectorid);
            detectorInfo = DetectorDAO.getInstance().getDetectorSourceInfo(detectorid);
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }

        return null;
    }

    @Override
    public void done() {
        try {
            get();
            TemplateModel.getInstance().setStreamFilter(streamFilter);
            TemplateModel.getInstance().setDetectorInfo(detectorInfo);
            TemplateModel.getInstance().setTemplate(template, detectorid);
            GetProjectionsAction.getInstance(this).setDetectorid(detectorid);
            if (ProjectionModel.getInstance().getDetectorid() != detectorid) {
                ProjectionModel.getInstance().clear();
            }
        } catch (InterruptedException | ExecutionException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving template for detectorid : " + detectorid, ex);
        }
    }

}
