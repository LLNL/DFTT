/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

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
    
    public StoredFilter getStreamFilter()
    {
        return streamFilter;
    }

    public void setStreamFilter(StoredFilter streamFilter) {
        this.streamFilter = streamFilter;
    }

    public void setDetectorInfo(String detectorInfo) {
        this.detectorInfo = detectorInfo;
    }

    public void setSecondTemplate(SubspaceTemplate template, DetectorProjection detectorProjection) {
        view.secondTemplateAdded(template,detectorProjection);
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
        view.templateWasUpdated();
    }

    public void clear() {
        template = null;
        if (view != null) {
            view.clear();
        }
    }
}
