/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.seismicData;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@Immutable
@ThreadSafe
public class Origerr {

    private final int originId;
    private final double sxx;
    private final double syy;
    private final double szz;
    private final double stt;
    private final double sxy;
    private final double sxz;
    private final double syz;
    private final double stx;
    private final double sty;
    private final double stz;
    private final double sdobs;
    private final double smajax;
    private final double sminax;
    private final double strike;
    private final double sdepth;
    private final double stime;
    private final double conf;

    public Origerr(int orid,
            double sxx,
            double syy,
            double szz,
            double stt,
            double sxy,
            double sxz,
            double syz,
            double stx,
            double sty,
            double stz,
            double sdobs,
            double smajax,
            double sminax,
            double strike,
            double sdepth,
            double stime,
            double conf) {
        this.originId = orid;
        this.sxx = sxx;
        this.syy = syy;
        this.szz = szz;
        this.stt = stt;
        this.sxy = sxy;
        this.sxz = sxz;
        this.syz = syz;
        this.stx = stx;
        this.sty = sty;
        this.stz = stz;
        this.sdobs = sdobs;
        this.smajax = smajax;
        this.sminax = sminax;
        this.strike = strike;
        this.sdepth = sdepth;
        this.stime = stime;
        this.conf = conf;
    }

    @Override
    public String toString() {
        return "OrigerrInfo{" + "orid=" + originId + ", sxx=" + sxx + ", syy=" + syy + ", szz=" + szz + ", sdobs=" + sdobs + ", smajax=" + smajax + ", sminax=" + sminax + ", strike=" + strike + '}';
    }


    public String getLabelText() {
         return String.format("<html><h3><font color=blue>Orid: %d, Sdobs: %7.4f, Smajax: %6.2f, Sminax: %6.2f, Strike: %6.2f, Sdepth: %6.2f, Stime: %f</font></h3></html>",
                originId, sdobs,smajax,sminax,strike,sdepth,stime);
    }


    public int getOrid() {
        return originId;
    }

    public double getSxx() {
        return sxx;
    }

    public double getSyy() {
        return syy;
    }

    public double getSzz() {
        return szz;
    }

    public double getStt() {
        return stt;
    }

    public double getSxy() {
        return sxy;
    }

    public double getSxz() {
        return sxz;
    }

    public double getSyz() {
        return syz;
    }

    public double getStx() {
        return stx;
    }

    public double getSty() {
        return sty;
    }

    public double getStz() {
        return stz;
    }

    public double getSdobs() {
        return sdobs;
    }

    public double getSmajax() {
        return smajax;
    }

    public double getSminax() {
        return sminax;
    }

    public double getStrike() {
        return strike;
    }

    public double getSdepth() {
        return sdepth;
    }

    public double getStime() {
        return stime;
    }

    public double getConf() {
        return conf;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.originId;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.sxx) ^ (Double.doubleToLongBits(this.sxx) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.syy) ^ (Double.doubleToLongBits(this.syy) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.szz) ^ (Double.doubleToLongBits(this.szz) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.stt) ^ (Double.doubleToLongBits(this.stt) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.sxy) ^ (Double.doubleToLongBits(this.sxy) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.sxz) ^ (Double.doubleToLongBits(this.sxz) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.syz) ^ (Double.doubleToLongBits(this.syz) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.stx) ^ (Double.doubleToLongBits(this.stx) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.sty) ^ (Double.doubleToLongBits(this.sty) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.stz) ^ (Double.doubleToLongBits(this.stz) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.sdobs) ^ (Double.doubleToLongBits(this.sdobs) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.smajax) ^ (Double.doubleToLongBits(this.smajax) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.sminax) ^ (Double.doubleToLongBits(this.sminax) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.strike) ^ (Double.doubleToLongBits(this.strike) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.sdepth) ^ (Double.doubleToLongBits(this.sdepth) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.stime) ^ (Double.doubleToLongBits(this.stime) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.conf) ^ (Double.doubleToLongBits(this.conf) >>> 32));
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
        final Origerr other = (Origerr) obj;
        if (this.originId != other.originId) {
            return false;
        }
        if (Double.doubleToLongBits(this.sxx) != Double.doubleToLongBits(other.sxx)) {
            return false;
        }
        if (Double.doubleToLongBits(this.syy) != Double.doubleToLongBits(other.syy)) {
            return false;
        }
        if (Double.doubleToLongBits(this.szz) != Double.doubleToLongBits(other.szz)) {
            return false;
        }
        if (Double.doubleToLongBits(this.stt) != Double.doubleToLongBits(other.stt)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sxy) != Double.doubleToLongBits(other.sxy)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sxz) != Double.doubleToLongBits(other.sxz)) {
            return false;
        }
        if (Double.doubleToLongBits(this.syz) != Double.doubleToLongBits(other.syz)) {
            return false;
        }
        if (Double.doubleToLongBits(this.stx) != Double.doubleToLongBits(other.stx)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sty) != Double.doubleToLongBits(other.sty)) {
            return false;
        }
        if (Double.doubleToLongBits(this.stz) != Double.doubleToLongBits(other.stz)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sdobs) != Double.doubleToLongBits(other.sdobs)) {
            return false;
        }
        if (Double.doubleToLongBits(this.smajax) != Double.doubleToLongBits(other.smajax)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sminax) != Double.doubleToLongBits(other.sminax)) {
            return false;
        }
        if (Double.doubleToLongBits(this.strike) != Double.doubleToLongBits(other.strike)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sdepth) != Double.doubleToLongBits(other.sdepth)) {
            return false;
        }
        if (Double.doubleToLongBits(this.stime) != Double.doubleToLongBits(other.stime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.conf) != Double.doubleToLongBits(other.conf)) {
            return false;
        }
        return true;
    }
    
}
