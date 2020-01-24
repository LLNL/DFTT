/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.phaseVisibility;

import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;

/**
 *
 * @author dodge1
 */
public class SelectablePhase {
    private boolean selected;
    private final SeismicPhase phase;

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the phase
     */
    public SeismicPhase getPhase() {
        return phase;
    }

    public SelectablePhase(boolean selected, SeismicPhase phase) {
        this.selected = selected;
        this.phase = phase;
    }
    public SelectablePhase(SeismicPhase phase) {
        this.selected = false;
        this.phase = phase;
    }
    
    
}
