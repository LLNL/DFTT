/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import llnl.gnem.apps.detection.sdBuilder.templateDisplay.*;
import java.sql.Connection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.database.SubspaceTemplateDAO;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class SecondTemplateRetrievalWorker extends SwingWorker<Void, Void> {

    private SubspaceTemplate template;
    private final DetectorProjection detectorProjection;

    public SecondTemplateRetrievalWorker(DetectorProjection dp) {
        this.detectorProjection = dp;
    }

    @Override
    protected Void doInBackground() throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            Object obj = SubspaceTemplateDAO.getInstance().getEmpiricalTemplate(conn, detectorProjection.getDetectorid());
            template = (SubspaceTemplate) obj;
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
            TemplateModel.getInstance().setSecondTemplate(template, detectorProjection);
        } catch (InterruptedException | ExecutionException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed retrieving template for detectorid : " + detectorProjection.getDetectorid(), ex);
        }
    }

}
