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
package llnl.gnem.core.waveform;


import llnl.gnem.core.util.StreamKey;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class Wfdisc {

    private final double calib;
    private final double calper;
    private final String datatype;
    private final String dfile;
    private final String dir;
    private final double endtime;
    private final int foff;
    private final int nsamp;
    private final double samprate;
    private final StreamKey stachan;
    private final double time;
    private final long wfid;

    public Wfdisc(String sta,
            String chan,
            double time,
            long wfid,
            double endtime,
            int nsamp,
            double samprate,
            double calib,
            double calper,
            String datatype,
            String dir,
            String dfile,
            int foff) {
        this(new StreamKey(sta, chan), time, wfid, endtime, nsamp, samprate, calib, calper, datatype, dir, dfile, foff);
    }

    public Wfdisc(StreamKey stachan, double time, long wfid, double endtime, int nsamp, double samprate, double calib, double calper, String datatype, String dir, String dfile, int foff) {
        this.stachan = stachan;
        this.time = time;
        this.wfid = wfid;
        this.endtime = endtime;
        this.nsamp = nsamp;
        this.samprate = samprate;
        this.calib = calib;
        this.calper = calper;
        this.datatype = datatype;
        this.dir = dir;
        this.dfile = dfile;
        this.foff = foff;
    }

    public double getCalib() {
        return calib;
    }

    public double getCalper() {
        return calper;
    }

    public String getChan() {
        return stachan.getChan();
    }

    public String getDatatype() {
        return datatype;
    }

    public String getDfile() {
        return dfile;
    }

    public String getDir() {
        return dir;
    }

    public double getEndtime() {
        return endtime;
    }

    public int getFoff() {
        return foff;
    }

    public int getNsamp() {
        return nsamp;
    }

    public String getPath() {
        String dir = getDir();
        return dir + '/' + getDfile();
    }

    public double getSamprate() {
        return samprate;
    }

    public String getSta() {
        return stachan.getSta();
    }

    public StreamKey getStaChan() {
        return stachan;
    }

    public double getTime() {
        return time;
    }

    public long getWfid() {
        return wfid;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(" Sta: ");
        b.append(stachan.getSta());
        b.append(" chan: ");
        b.append(stachan.getChan());
        b.append(" time: ");
        b.append(time);
        b.append(" wfid: ");
        b.append(wfid);
        b.append(" endtime: ");
        b.append(endtime);
        b.append(" nsamp: ");
        b.append(nsamp);
        b.append(" samprate: ");
        b.append(samprate);
        b.append(" calib: ");
        b.append(calib);
        b.append(" calper: ");
        b.append(calper);
        b.append(" datatype: ");
        b.append(datatype);
        b.append(" dir: ");
        b.append(getDir());
        b.append(" dfile: ");
        b.append(getDfile());
        b.append(" foff: ");
        b.append(foff);
        return b.toString();
    }
}
