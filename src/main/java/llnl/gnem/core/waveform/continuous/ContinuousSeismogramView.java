/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.continuous;

import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public interface ContinuousSeismogramView {

    public void clear();

    public void seismogramWasAdded(StreamKey identifier);

    public void replaceContents();
    
}
