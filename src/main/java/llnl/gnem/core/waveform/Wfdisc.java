/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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

import java.util.Objects;
import llnl.gnem.core.util.StreamKey;


public class Wfdisc {

    private final StreamKey stachan;
    private final double time;
    private final long wfid;
    private final long chanid;
    private final int jdate;
    private final double endtime;
    private final int nsamp;
    private final double samprate;
    private final double calib;
    private final double calper;
    private final String instype;
    private final String segtype;
    private final String datatype;
    private final String clip;
    private final String dir;
    private final String dfile;
    private final int foff;
    private final int commid;

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
        this(new StreamKey(null, sta, chan, null), time, wfid, endtime, nsamp, samprate, calib, calper, datatype, dir, dfile, foff);
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
        chanid = -1;
        jdate = -1;
        instype = "-";
        segtype = "-";
        clip = "-";
        commid = -1;
    }

    public Wfdisc(String sta, String chan, double time, long wfid, long chanid, int jdate,
            double endtime, int nsamp, double samprate, double calib, double calper,
            String instype, String segtype, String datatype, String clip, String dir, String dfile, int foff, int commid) {
        this.stachan = new StreamKey(null, sta, chan, null);
        this.time = time;
        this.wfid = wfid;
        this.chanid = chanid;
        this.jdate = jdate;
        this.endtime = endtime;
        this.nsamp = nsamp;
        this.samprate = samprate;
        this.calib = calib;
        this.calper = calper;
        this.instype = instype;
        this.segtype = segtype;
        this.datatype = datatype;
        this.clip = clip;
        this.dir = dir;
        this.dfile = dfile;
        this.foff = foff;
        this.commid = commid;
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

    public StreamKey getStachan() {
        return stachan;
    }

    public long getChanid() {
        return chanid;
    }

    public int getJdate() {
        return jdate;
    }

    public String getInstype() {
        return instype;
    }

    public String getSegtype() {
        return segtype;
    }

    public String getClip() {
        return clip;
    }

    public int getCommid() {
        return commid;
    }

    @Override
    public String toString() {
        return "Wfdisc{" + "stachan=" + stachan + ", time=" + time + ", wfid=" + wfid + ", chanid=" + chanid + ", jdate=" + jdate + ", endtime=" + endtime + ", nsamp=" + nsamp + ", samprate=" + samprate + ", calib=" + calib + ", calper=" + calper + ", instype=" + instype + ", segtype=" + segtype + ", datatype=" + datatype + ", clip=" + clip + ", dir=" + dir + ", dfile=" + dfile + ", foff=" + foff + ", commid=" + commid + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.stachan);
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 83 * hash + (int) (this.wfid ^ (this.wfid >>> 32));
        hash = 83 * hash + (int) (this.chanid ^ (this.chanid >>> 32));
        hash = 83 * hash + this.jdate;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.endtime) ^ (Double.doubleToLongBits(this.endtime) >>> 32));
        hash = 83 * hash + this.nsamp;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.samprate) ^ (Double.doubleToLongBits(this.samprate) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.calib) ^ (Double.doubleToLongBits(this.calib) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.calper) ^ (Double.doubleToLongBits(this.calper) >>> 32));
        hash = 83 * hash + Objects.hashCode(this.instype);
        hash = 83 * hash + Objects.hashCode(this.segtype);
        hash = 83 * hash + Objects.hashCode(this.datatype);
        hash = 83 * hash + Objects.hashCode(this.clip);
        hash = 83 * hash + Objects.hashCode(this.dir);
        hash = 83 * hash + Objects.hashCode(this.dfile);
        hash = 83 * hash + (int) (this.foff ^ (this.foff >>> 32));
        hash = 83 * hash + this.commid;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Wfdisc other = (Wfdisc) obj;
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (this.wfid != other.wfid) {
            return false;
        }
        if (this.chanid != other.chanid) {
            return false;
        }
        if (this.jdate != other.jdate) {
            return false;
        }
        if (Double.doubleToLongBits(this.endtime) != Double.doubleToLongBits(other.endtime)) {
            return false;
        }
        if (this.nsamp != other.nsamp) {
            return false;
        }
        if (Double.doubleToLongBits(this.samprate) != Double.doubleToLongBits(other.samprate)) {
            return false;
        }
        if (Double.doubleToLongBits(this.calib) != Double.doubleToLongBits(other.calib)) {
            return false;
        }
        if (Double.doubleToLongBits(this.calper) != Double.doubleToLongBits(other.calper)) {
            return false;
        }
        if (this.foff != other.foff) {
            return false;
        }
        if (this.commid != other.commid) {
            return false;
        }
        if (!Objects.equals(this.instype, other.instype)) {
            return false;
        }
        if (!Objects.equals(this.segtype, other.segtype)) {
            return false;
        }
        if (!Objects.equals(this.datatype, other.datatype)) {
            return false;
        }
        if (!Objects.equals(this.clip, other.clip)) {
            return false;
        }
        if (!Objects.equals(this.dir, other.dir)) {
            return false;
        }
        if (!Objects.equals(this.dfile, other.dfile)) {
            return false;
        }
        return Objects.equals(this.stachan, other.stachan);
    }

}
