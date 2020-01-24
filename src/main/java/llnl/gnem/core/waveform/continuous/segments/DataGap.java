/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.continuous.segments;

import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge
 */
public class DataGap {
    private final int wfid1;
    private final int wfid2;
    private final double start;
    private final double end;

    public DataGap(int wfid1, int wfid2, double endTime1, double time2) {
        this.wfid1 = wfid1;
        this.wfid2 = wfid2;
        this.start = endTime1;
        this.end = time2;
    }
    
    @Override
    public String toString()
    {
        return String.format("Gap of duration %5.4f s extending from %s to %s WFID1 = %d, WFID2 = %d",
                end - start,new TimeT(start), new TimeT(end), wfid1, wfid2);
    }

    /**
     * @return the wfid1
     */
    public int getWfid1() {
        return wfid1;
    }

    /**
     * @return the wfid2
     */
    public int getWfid2() {
        return wfid2;
    }

    /**
     * @return the start
     */
    public double getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public double getEnd() {
        return end;
    }
    
    public Epoch getEpoch()
    {
        return new Epoch(start, end);
    }
}
