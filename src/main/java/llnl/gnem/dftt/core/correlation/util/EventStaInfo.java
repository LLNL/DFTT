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
package llnl.gnem.dftt.core.correlation.util;

import java.io.Serializable;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class EventStaInfo implements Serializable {

    private final long evid;
    private final long wfid;
    private final double delta;
    private final double olat;
    private final double olon;
    private static final long serialVersionUID = 17989434118061147L;

    public EventStaInfo(long evid, long wfid, double delta, double olat, double olon) {
        this.evid = evid;
        this.wfid = wfid;
        this.delta = delta;
        this.olat = olat;
        this.olon = olon;
    }

    @Override
    public String toString() {
        return "EventStaInfo{" + "evid=" + evid + ", wfid=" + wfid + ", delta=" + delta + '}';
    }

    public long getEvid() {
        return evid;
    }

    public long getWfid() {
        return wfid;
    }

    public double getDelta() {
        return delta;
    }

    public double getOlat() {
        return olat;
    }

    public double getOlon() {
        return olon;
    }

}
