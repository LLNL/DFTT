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
package llnl.gnem.dftt.core.util;

import java.io.Serializable;
import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: Oct 25, 2007 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class BandInfo implements Serializable, Comparable {

    private final int bandid;
    private final double lowpass;
    private final double highpass;
    private final double centerfreq;
    static final long serialVersionUID = -2499157452426028964L;

    public boolean passBandsMatch(BandInfo other) {
        return lowpass == other.lowpass && highpass == other.highpass;
    }

    public static enum CenterFreqType { COMPUTED, SPECIFIED}
    public BandInfo(int bandid,
            double lowpass,
            double highpass) {
        this.bandid = bandid;
        this.lowpass = lowpass;
        this.highpass = highpass;
        centerfreq = -1;
    }

    public BandInfo(int bandid,
            double lowpass,
            double highpass,
            double centerfreq) {
        this.bandid = bandid;
        this.lowpass = lowpass;
        this.highpass = highpass;
        this.centerfreq = centerfreq;
    }

    public int getBandid() {
        return bandid;
    }

    public double getLowpass() {
        return lowpass;
    }

    public double getHighpass() {
        return highpass;
    }
    
    /**
     * Returns the log10 of the center frequency
     * @return 
     */
    public double getLogCenterFreq()
    {
        return Math.log10(this.getCenterFreq());
    }

    @Override
    public String toString() {
        return String.format("Band %2d (%5.3f Hz to %5.3f Hz with centerFreq = %5.3f)",
                bandid, lowpass, highpass, getCenterFreq());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BandInfo other = (BandInfo) obj;
        if (this.bandid != other.bandid) {
            return false;
        }
        if (Double.doubleToLongBits(this.lowpass) != Double.doubleToLongBits(other.lowpass)) {
            return false;
        }
        if (Double.doubleToLongBits(this.highpass) != Double.doubleToLongBits(other.highpass)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.bandid;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.lowpass) ^ (Double.doubleToLongBits(this.lowpass) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.highpass) ^ (Double.doubleToLongBits(this.highpass) >>> 32));
        return hash;
    }

    public double getCenterFreq() {
        return centerfreq <= 0 ? (lowpass + highpass) / 2 : centerfreq;
    }

    @Override
    public int compareTo(Object o) {
        BandInfo other = (BandInfo) o;
        double centerFreq = this.getCenterFreq();
        double otherCenterFreq = other.getCenterFreq();
        if (centerFreq < otherCenterFreq) {
            return -1;
        } else if (centerFreq == otherCenterFreq) {
            return 0;
        } else {
            return 1;
        }
    }
}
