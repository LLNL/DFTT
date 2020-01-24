package llnl.gnem.core.gui.map.internal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.seismicData.AbstractEventInfo;
import llnl.gnem.core.gui.plotting.plotobject.Symbol;
import llnl.gnem.core.util.PairT;

/**
 *
 * @author dodge1
 */
public interface IconManager<S extends StationInfo> {
    PairT<BufferedImage, Color> getEarthquakeIconInfo(AbstractEventInfo info, boolean selected);

    PairT<BufferedImage, Color> getStationIconInfo(S station, boolean selected);

    Symbol getStationSymbol(S info);

    Color getStationSymbolColor(S info, boolean selected);
}
