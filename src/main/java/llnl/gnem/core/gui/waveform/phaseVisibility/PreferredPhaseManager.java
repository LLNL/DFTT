package llnl.gnem.core.gui.waveform.phaseVisibility;

import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;

/**
 *
 * @author dodge1
 */
public interface PreferredPhaseManager {

    public void addListener(PreferredPhaseListener listener);

    void removePreferredPhase(SeismicPhase phase);

    void addPreferredPhase(SeismicPhase phase);

    void removePreferredPhase(String phase);

    void addPreferredPhase(String phase);

    boolean isPreferred(SeismicPhase phase);

    boolean isPreferred(String phase);

    void savePreferences() throws Exception;
}
