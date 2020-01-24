/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.plotPrefs;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.UserObjectPreferences;

/**
 *
 * @author dodge1
 */
public class PlotPreferenceModel {

    private final Map<JMultiAxisPlot, String> plotMap;
    private PlotPresentationPrefs plotPrefs;

    private PlotPreferenceModel() {

        
        plotMap = new WeakHashMap<>();
        updateFromDb();

    }

    private void updateFromDb() {

        try {
            plotPrefs = (PlotPresentationPrefs) UserObjectPreferences.getInstance().retrieveObjectFromPrefs(NODE_NAME, PlotPresentationPrefs.class);
        } catch (IOException | ClassNotFoundException | BackingStoreException ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, null, ex);
        }
        if(plotPrefs == null){
            plotPrefs = new PlotPresentationPrefs();
        }
    }

    public void registerPlot(JMultiAxisPlot plot) {
        plotMap.put(plot, "unused");
    }

    public static PlotPreferenceModel getInstance() {
        return PlotPreferenceModelHolder.INSTANCE;
    }

    /**
     * @return the plotPrefs
     */
    public PlotPresentationPrefs getPrefs() {
        return plotPrefs;
    }

    public void setPrefs(PlotPresentationPrefs prefs) throws Exception {
        UserObjectPreferences.getInstance().saveObjectToPrefs(NODE_NAME, prefs);

        for (JMultiAxisPlot plot : plotMap.keySet()) {
            plot.updateForChangedPrefs();
        }
    }
    private static final String NODE_NAME = "PLOT_PREFS";

    private static class PlotPreferenceModelHolder {

        private static final PlotPreferenceModel INSTANCE = new PlotPreferenceModel();
    }
}
