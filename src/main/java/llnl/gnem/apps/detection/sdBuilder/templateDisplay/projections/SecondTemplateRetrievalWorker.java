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

import llnl.gnem.apps.detection.sdBuilder.templateDisplay.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;

import llnl.gnem.dftt.core.util.ApplicationLogger;

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
 
            Object obj = DetectionDAOFactory.getInstance().getSubspaceTemplateDAO().getEmpiricalTemplate(detectorProjection.getDetectorid());
            template = (SubspaceTemplate) obj;
      
        

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
