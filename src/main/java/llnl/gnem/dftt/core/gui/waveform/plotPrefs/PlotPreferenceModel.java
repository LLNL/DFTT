/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.gui.waveform.plotPrefs;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.UserObjectPreferences;

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
