/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.configuration;

import llnl.gnem.apps.detection.sdBuilder.actions.CreateTemplateAction;

/**
 *
 * @author vieceli1
 */
public class DetectorCreationEnabler {

    private boolean requireCorrelation;
    private boolean waveformsAvailable;
    private boolean hasBeenCorrelated;
    
    private DetectorCreationEnabler() {
        requireCorrelation = ParameterModel.getInstance().isRequireCorrelation();
        waveformsAvailable = false;
        hasBeenCorrelated = false;
    }
    
    public static DetectorCreationEnabler getInstance() {
        return DetectorCreationEnablerHolder.INSTANCE;
    }

    public void correlationRequired(boolean requireCorrelation) {
        this.requireCorrelation = requireCorrelation;
        CreateTemplateAction.getInstance(this).setEnabled(isOkToCreate());
    }

    public void setWaveformsAvailable(boolean available) {
        waveformsAvailable = available;
        CreateTemplateAction.getInstance(this).setEnabled(isOkToCreate());
    }

    public void hasBeenCorrelated(boolean correlated) {
        hasBeenCorrelated = correlated;
        CreateTemplateAction.getInstance(this).setEnabled(isOkToCreate());
    }
    
    private static class DetectorCreationEnablerHolder {

        private static final DetectorCreationEnabler INSTANCE = new DetectorCreationEnabler();
    }
    
    public boolean isOkToCreate(){
        return waveformsAvailable && (!requireCorrelation || hasBeenCorrelated );
        
    }
    
}
