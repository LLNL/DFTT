/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.continuous;

import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public interface ContinuousSeismogramDAO {
    ContinuousSeismogram getSeismogram( StreamKey name, Epoch epoch) throws Exception;
}
