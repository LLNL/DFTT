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
package llnl.gnem.apps.detection.core.framework.detectors.bulletin;

import java.io.Serializable;
import llnl.gnem.dftt.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class BulletinRecord  implements Serializable{
    private final int evid;
    private final double lat;
    private final double lon;
    private final double time;
    private final double depth;
    private final double mw;
    private final double expectedPTime;
    private static final long serialVersionUID = 1L;

    public BulletinRecord(int evid, double lat, double lon, double time, double depth, double mw, double expectedPTime) {
        this.evid = evid;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.depth = depth;
        this.mw = mw;
        this.expectedPTime = expectedPTime;
    }

    @Override
    public String toString() {
        return "BulletinRecord{" + "evid=" + evid + ", mw=" + mw + ", expectedPTime=" + new TimeT(expectedPTime) + '}';
    }
    
    

    /**
     * @return the evid
     */
    public int getEvid() {
        return evid;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @return the depth
     */
    public double getDepth() {
        return depth;
    }

    /**
     * @return the mw
     */
    public double getMw() {
        return mw;
    }

    /**
     * @return the expectedPTime
     */
    public double getExpectedPTime() {
        return expectedPTime;
    }
    
}
