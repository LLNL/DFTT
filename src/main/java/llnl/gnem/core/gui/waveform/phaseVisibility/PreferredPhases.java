/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;

/**
 *
 * @author dodge1
 */
public class PreferredPhases implements Serializable {

    private final Collection<SeismicPhase> phases;
    static final long serialVersionUID = 5663822896377567970L;

    public PreferredPhases() {
        phases = new ArrayList<>();
    }

    public void clear() {
        phases.clear();
    }

    public void addPhase(SeismicPhase phase) {
        phases.remove(phase);
        phases.add(phase);
    }

    public Collection<SeismicPhase> getPhases() {
        return new ArrayList<>(phases);
    }

    void removePhase(SeismicPhase phase) {
        phases.remove(phase);
    }

    void removePhase(String phase) {
        Iterator<SeismicPhase> it = phases.iterator();
        while(it.hasNext()){
            if(it.next().getName().equals(phase)){
                it.remove();
                return;
            }
        }
    }

    boolean contains(SeismicPhase phase) {
        return phases.contains(phase);
    }

    boolean contains(String phase) {
        for (SeismicPhase aPhase : phases) {
            if (aPhase.getName().equals(phase)) {
                return true;
            }
        }
        return false;
    }

    void addPhase(String phase) {
        for(SeismicPhase aPhase :BaseAvailablePhaseManager.getInstance().getAvailablePhases()){
            if(aPhase.getName().equals(phase)){
                phases.add(aPhase);
                return;
            }
        }
    }
}
