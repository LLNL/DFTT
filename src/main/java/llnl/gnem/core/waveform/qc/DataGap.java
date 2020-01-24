/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.qc;

import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public class DataGap implements DataDefect{

    private final Epoch epoch;

    public DataGap(Epoch gapEpoch) {
        this.epoch = gapEpoch;
    }

    @Override
    public Epoch getEpoch() {
        return epoch;
    }

    @Override
    public DefectType getDefectType() {
        return DataDefect.DefectType.GAP;
    }
    
}
