/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;

/**
 *
 * @author dodge1
 */
public class BaseAvailablePhaseManager implements AvailablePhaseManager {

    private static BaseAvailablePhaseManager instance = null;

    public static BaseAvailablePhaseManager getInstance() {
        if (instance == null) {
            instance = new BaseAvailablePhaseManager();
        }
        return instance;
    }

    private final Collection<SeismicPhase> availablePhases;

    private BaseAvailablePhaseManager() {
        availablePhases = new ArrayList<>();
    }

    @Override
    public Collection<SeismicPhase> getAvailablePhases() {
        return new ArrayList<>(availablePhases);
    }

    @Override
    public boolean isAvailablePhase(SeismicPhase phase) {
        return availablePhases.contains(phase);
    }

    @Override
    public void replacePhases(Collection<SeismicPhase> phases) {
        availablePhases.clear();
        availablePhases.addAll(phases);
    }

    @Override
    public boolean isAvailablePhase(String phase) {
        for (SeismicPhase aPhase : availablePhases) {
            if (aPhase.getName().equals(phase)) {
                return true;
            }
        }
        return false;
    }
}
