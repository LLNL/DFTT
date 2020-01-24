package llnl.gnem.core.seismicData;

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
