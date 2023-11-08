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
package llnl.gnem.dftt.core.seismicData;

import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: Apr 22, 2008 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class Arrival {

    public Arrival(String sta,
            double time,
            int arid,
            String chan,
            String iphase,
            double deltim,
            String fm,
            double snr,
            String auth) {

        this.sta = sta;
        this.time = time;
        this.arid = arid;
        this.chan = chan;
        this.iphase = iphase;
        this.deltim = deltim;
        this.fm = fm;
        this.snr = snr;
        this.auth = auth;
    }
    private final String sta;
    private final double time;
    private final int arid;
    private final String chan;
    private final String iphase;
    private final double deltim;
    private final String fm;
    private final double snr;
    private final String auth;

    public String getSta() {
        return sta;
    }

    public double getTime() {
        return time;
    }

    public int getArid() {
        return arid;
    }

    public String getFm() {
        return fm;
    }

    public double getSnr() {
        return snr;
    }

    public String getAuth() {
        return auth;
    }

    public String getChan() {
        return chan;
    }

    public String getIphase() {
        return iphase;
    }

    public double getDeltim() {
        return deltim;
    }
}
