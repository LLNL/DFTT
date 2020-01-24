package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;

/**
 *
 * @author dodge1
 */
public class UsablePhaseManager {
    private final AvailablePhaseManager available;
    private final PreferredPhaseManager preferred;

    public UsablePhaseManager(AvailablePhaseManager allowable, PreferredPhaseManager preferred) {
        this.available = allowable;
        this.preferred = preferred;
    }

    public boolean isAllowable(SeismicPhase phase) {
        return available.isAvailablePhase(phase) && preferred.isPreferred(phase);
    }

    public boolean isAllowable(String phase) {
        return available.isAvailablePhase(phase) && preferred.isPreferred(phase);
    }

    public PreferredPhaseManager getPreferredPhaseManager() {
        return preferred;
    }

    public Collection<SeismicPhase> getUsablePhases() {
        Collection<SeismicPhase> result = new ArrayList<>();
        Collection<SeismicPhase> candidates = available.getAvailablePhases();
        for (SeismicPhase candidate : candidates) {
            if (preferred.isPreferred(candidate)) {
                result.add(candidate);
            }
        }

        return result;
    }
}
