package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import llnl.gnem.core.dataAccess.dataObjects.SeismicPhase;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.UserObjectPreferences;

/**
 *
 * @author dodge1
 */
public class BasePreferredPhaseManager implements PreferredPhaseManager {

    private static BasePreferredPhaseManager instance = null;

    public static BasePreferredPhaseManager getInstance() {
        if (instance == null) {
            instance = new BasePreferredPhaseManager();
        }
        return instance;
    }

    private PreferredPhases preferredPhases;
    private static final String NODE_NAME = "PREFERRED_PHASES";
    private final Collection<PreferredPhaseListener> listeners;

    private BasePreferredPhaseManager() {
        try {
            preferredPhases = (PreferredPhases) UserObjectPreferences.getInstance().retrieveObjectFromPrefs(NODE_NAME, PreferredPhases.class);
        } catch (IOException | ClassNotFoundException | BackingStoreException ex) {
            preferredPhases = new PreferredPhases();
        }
        if (preferredPhases == null) {
            preferredPhases = new PreferredPhases();
        }
        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(PreferredPhaseListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePreferredPhase(SeismicPhase phase) {
        preferredPhases.removePhase(phase);
        notifyListeners();
    }

    @Override
    public void addPreferredPhase(SeismicPhase phase) {
        preferredPhases.addPhase(phase);
        notifyListeners();
    }

    @Override
    public boolean isPreferred(SeismicPhase phase) {
        return preferredPhases.contains(phase);
    }

    @Override
    public void savePreferences() {
        try {
            UserObjectPreferences.getInstance().saveObjectToPrefs(NODE_NAME, preferredPhases);
        } catch (IOException | BackingStoreException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed saving preferred Phases preferences!", ex);
        }
    }

    private void notifyListeners() {
        for (PreferredPhaseListener listener : listeners) {
            listener.updatePhases();
        }
    }

    @Override
    public boolean isPreferred(String phase) {
        return preferredPhases.contains(phase);
    }

    @Override
    public void removePreferredPhase(String phase) {
        preferredPhases.removePhase(phase);
        notifyListeners();
    }

    @Override
    public void addPreferredPhase(String phase) {
        preferredPhases.addPhase(phase);
        notifyListeners();
    }
}
