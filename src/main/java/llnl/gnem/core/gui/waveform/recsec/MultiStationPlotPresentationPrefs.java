package llnl.gnem.core.gui.waveform.recsec;

import java.awt.Color;
import llnl.gnem.core.dataAccess.dataObjects.ApplicationStationInfo;

import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.gui.waveform.plotPrefs.PlotPresentationPrefs;

public class MultiStationPlotPresentationPrefs extends PlotPresentationPrefs {

    private static final long serialVersionUID = -7669758834987828569L;

    /**
     * Return the preferred trace color for the given station. Default
     * implementation is to return simply the preferred trace color
     *
     * @param info
     * @return
     */
    public Color getTraceColor(StationInfo info) {
        if (info instanceof ApplicationStationInfo) {
            ApplicationStationInfo asi = (ApplicationStationInfo) info;
            return asi.getArrayId() != null ? Color.black : Color.blue;
        } else {
            return super.getTraceColor();
        }
    }
}
