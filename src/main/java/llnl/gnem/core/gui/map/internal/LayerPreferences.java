package llnl.gnem.core.gui.map.internal;

import java.util.prefs.Preferences;

public class LayerPreferences {

    private static final LayerPreferences instance = new LayerPreferences();
    private final Preferences prefs;

    public static LayerPreferences getInstance() {
        return instance;
    }

    private LayerPreferences() {
        prefs = Preferences.userNodeForPackage(this.getClass());
    }

    public void defaultLayerEnabled(String layerName, boolean value) {
        prefs.putBoolean(layerName, value);
    }

    public boolean getLayerEnabled(String layerName) {
        return prefs.getBoolean(layerName, false);
    }

    public void setLayerEnabled(String layerName, boolean value) {
        prefs.putBoolean(layerName, value);
    }

    public boolean containsLayer(String name) {
        return !prefs.get(name, "xxx").equals("xxx");
    }
}
