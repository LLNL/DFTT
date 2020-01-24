package llnl.gnem.core.gui.map.internal;

import java.awt.Color;
import java.awt.image.BufferedImage;

import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.util.PairT;

public interface ClusteredIconManager<S extends StationInfo> extends IconManager<S> {
    public PairT<BufferedImage, Color> getClusteredStationIconInfo(
                    StationInfo stationInfo, boolean selected);
    
  
}
