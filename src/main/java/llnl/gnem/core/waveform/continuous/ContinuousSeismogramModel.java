/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.continuous;

import java.util.Collection;
import java.util.List;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge
 */
public interface ContinuousSeismogramModel {
    
    public void addView(ContinuousSeismogramView view);

    public void addSeismogram(ContinuousSeismogram seismogram);

    public ContinuousSeismogram getContinuousSeismogram(StreamKey key);

    public List<StreamKey> getSeismogramList();

    public void clear();

    public void setSeismograms(Collection<ContinuousSeismogram> seismograms);
}
