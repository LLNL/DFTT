/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.continuous.segments;

import java.util.Collection;
import java.util.List;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public interface SegmentCatalogModel {
    
    
    public void addView(SegmentCatalogView view);
    
    public void addChannelSegmentCatalog( ChannelSegmentCatalog catalog);
    
    public ChannelSegmentCatalog getChannelSegmentCatalog( StreamKey key);
    
    public List<StreamKey> getCatalogList();
    
    public void clear();
    
    public void setCatalogs( Collection<ChannelSegmentCatalog> catalogs );

    public void setSelectionWindowTime(double windowTime);

    public double getSelectionWindowDuration();

    public double validateDurationChange(double delta);
    
    void setWindowDuration(double windowDuration);
    
    double getSelectionWindowTime();
    
    
}
