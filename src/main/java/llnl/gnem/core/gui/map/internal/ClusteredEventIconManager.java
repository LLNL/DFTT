/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.map.internal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import llnl.gnem.core.seismicData.AbstractEventInfo;

import llnl.gnem.core.util.PairT;
/**
 *
 * @author thomas12
 */
public interface ClusteredEventIconManager<E extends AbstractEventInfo> extends IconManager {
    public PairT<BufferedImage, Color> getClusteredEventIconInfo(
                    AbstractEventInfo eventInfo, boolean selected);
    
  
}
