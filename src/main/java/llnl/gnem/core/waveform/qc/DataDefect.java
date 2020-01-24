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
public interface DataDefect {

    enum DefectType {
        GAP, SPIKE, DROPOUT
    };

    Epoch getEpoch();

    DefectType getDefectType();
}
