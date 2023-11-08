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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.GetProjectionsAction;
import java.sql.Connection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;

import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.ProjectionModel;
import llnl.gnem.dftt.core.database.ConnectionManager;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

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
            Object obj = DetectionDAOFactory.getInstance().getSubspaceTemplateDAO().getEmpiricalTemplate(detectorid);
            template = (SubspaceTemplate) obj;
            streamFilter = DetectionDAOFactory.getInstance().getStreamDAO().getStreamFilter(detectorid);
            detectorInfo = DetectionDAOFactory.getInstance().getDetectorDAO().getDetectorSourceInfo(detectorid);
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
