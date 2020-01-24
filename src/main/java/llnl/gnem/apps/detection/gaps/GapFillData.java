/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.gaps;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */

@ThreadSafe
public class GapFillData {
    
    private final PrePostParamPair prePostStats;
    private final int gapStart;
    private final int gapEnd;
    
    public GapFillData( PrePostParamPair prePostStats,int gapStart,int gapEnd)
    {
        this.prePostStats = prePostStats;
        this.gapStart = gapStart;
        this.gapEnd = gapEnd;
    }

    public PrePostParamPair getPrePostStats() {
        return prePostStats;
    }

    public int getGapStart() {
        return gapStart;
    }

    public int getGapEnd() {
        return gapEnd;
    }
}
