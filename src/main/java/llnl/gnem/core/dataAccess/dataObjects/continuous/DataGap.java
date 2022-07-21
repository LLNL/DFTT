/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.core.dataAccess.dataObjects.continuous;


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
