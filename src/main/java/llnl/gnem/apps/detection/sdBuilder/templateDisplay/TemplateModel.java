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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.DetectorProjection;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class TemplateModel {

    private TemplateView view;
    private EmpiricalTemplate template;
    private StoredFilter streamFilter;
    private int detectorid;
    private String detectorInfo;

    public int getDetectorid() {
        return detectorid;
    }

    /**
     * @return the detectorInfo
     */
    public String getDetectorInfo() {
        return detectorInfo;
    }

    private TemplateModel() {
    }

    public static TemplateModel getInstance() {
        return TemplateModelHolder.INSTANCE;
    }

    public EmpiricalTemplate getCurrentTemplate() {
        return template;
    }

    public StoredFilter getStreamFilter() {
        return streamFilter;
    }

    public void setStreamFilter(StoredFilter streamFilter) {
        this.streamFilter = streamFilter;
    }

    public void setDetectorInfo(String detectorInfo) {
        this.detectorInfo = detectorInfo;
    }

    public void setSecondTemplate(SubspaceTemplate template, DetectorProjection detectorProjection) {
        view.secondTemplateAdded(template, detectorProjection);
    }

    private static class TemplateModelHolder {

        private static final TemplateModel INSTANCE = new TemplateModel();
    }

    public void setView(TemplateView view) {
        this.view = view;
    }

    public void setTemplate(EmpiricalTemplate template, int detectorid) {
        this.template = template;
        this.detectorid = detectorid;
        WriteTemplateAction.getInstance(this).setDetectorid(detectorid);
        if (SwingUtilities.isEventDispatchThread()) {
            view.templateWasUpdated();
        } else {
            Runnable foo = () -> {
                view.templateWasUpdated();
            };
            try {
                SwingUtilities.invokeAndWait(foo);
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(TemplateModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void clear() {
        template = null;
        if (view != null) {
            view.clear();
        }
    }
}
