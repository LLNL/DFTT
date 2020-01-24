/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.continuous.segments;

import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public interface SegmentCatalogView {
    
    public void catalogsWereLoaded();
    
    public void catalogWasLoaded(StreamKey key);

    public void catalogsCleared();

    public void windowDurationWasChanged();

    public void selectionWindowWasMoved();
    
}
