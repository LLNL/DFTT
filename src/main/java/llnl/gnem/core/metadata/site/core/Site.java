package llnl.gnem.core.metadata.site.core;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.TimeT;
import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: May 22, 2009 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class Site extends StationInfo {

    private final String stacode;
    private final int ondate;
    private final int offdate;
    private final String net;
    private final String auth;
    private final DataSource source;
    private final String unmappedSta;
    private static final int FOREVER = 2286324;

    public Site(String sta,
            String stacode,
            double lat,
            double lon,
            double elev) {
        super(sta, lat, lon, elev, "-", -1);
        this.stacode = stacode;
        ondate = -1;
        offdate = FOREVER;
        net = "-";
        auth = "-";
        source = DataSource.Unknown;
        unmappedSta = sta;
    }

    public Site(String sta,
            String stacode,
            double lat,
            double lon,
            double elev,
            int ondate,
            int offdate) {
        super(sta, lat, lon, elev, "-", -1);
        this.stacode = stacode;
        this.ondate = ondate;
        this.offdate = offdate;
        net = "-";
        auth = "-";
        source = DataSource.Unknown;
        unmappedSta = sta;
    }

    public Site(String sta,
            String stacode,
            double lat,
            double lon,
            double elev,
            int ondate,
            int offdate,
            int siteid) {
        super(sta, lat, lon, elev, "-", siteid);
        this.stacode = stacode;
        this.ondate = ondate;
        this.offdate = offdate;
        net = "-";
        auth = "-";
        source = DataSource.Unknown;
        unmappedSta = sta;
    }

    public Site(String sta,
            String stacode,
            double lat,
            double lon,
            double elev,
            int ondate,
            int offdate,
            String net,
            String auth,
            DataSource source,
            String unmappedSta) {
        super(sta, lat, lon, elev, "-", -1);
        this.stacode = stacode;
        this.ondate = ondate;
        this.offdate = offdate;
        this.net = net;
        this.auth = auth;
        this.source = source;
        this.unmappedSta = unmappedSta;
    }

    public Site(final Site site) {
        super(site.getSta(), site.getLat(), site.getLon(), site.getElevation(), site.getDescription(), site.getStationId());
        this.stacode = site.stacode;
        this.ondate = site.ondate;
        this.offdate = site.offdate;
        this.net = site.net;
        this.auth = site.auth;
        this.source = site.source;
        this.unmappedSta = site.unmappedSta;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %8.5f %9.5f %6.3f %d %d, Net: %s, Auth: %s, Source: %s (Un-mapped sta code: %s)",
                getSta(), stacode, getLat(), getLon(), getElevation(), ondate, offdate, net, auth, source.toString(), unmappedSta);
    }

    public String getStacode() {
        return stacode;
    }

    public int getOndate() {
        return ondate;
    }

    public int getOffdate() {
        return offdate;
    }

    public String getNet() {
        return net;
    }

    public String getAuth() {
        return auth;
    }

    public DataSource getDataSource() {
        return DataSource.Unknown;
    }

    public String getUnmappedSta() {
        return unmappedSta;
    }


    public String getSta() {
        String tmp = super.getStationCode();
        if (tmp == null || tmp.trim().isEmpty()) {
            return getUnmappedSta();
        } else {
            return tmp;
        }
    }

    public boolean isEquivalent(final Site aSite) {
        return getLat() == aSite.getLat() && getLon() == aSite.getLon() && getElevation() == aSite.getElevation() && ondate == aSite.ondate && offdate == aSite.offdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Site site = (Site) o;

        if (Double.compare(site.getElevation(), getElevation()) != 0) {
            return false;
        }
        if (Double.compare(site.getLat(), getLat()) != 0) {
            return false;
        }
        if (Double.compare(site.getLon(), getLon()) != 0) {
            return false;
        }
        if (offdate != site.offdate) {
            return false;
        }
        if (ondate != site.ondate) {
            return false;
        }
        if (auth != null ? !auth.equals(site.auth) : site.auth != null) {
            return false;
        }
        if (net != null ? !net.equals(site.net) : site.net != null) {
            return false;
        }
        if (source != site.source) {
            return false;
        }
        if (getSta() != null ? !getSta().equals(site.getSta()) : site.getSta() != null) {
            return false;
        }
        if (stacode != null ? !stacode.equals(site.stacode) : site.stacode != null) {
            return false;
        }
        return !(unmappedSta != null ? !unmappedSta.equals(site.unmappedSta) : site.unmappedSta != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (getSta() != null ? getSta().hashCode() : 0);
        result = 31 * result + (stacode != null ? stacode.hashCode() : 0);
        temp = getLat() != 0.0d ? Double.doubleToLongBits(getLat()) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = getLon() != 0.0d ? Double.doubleToLongBits(getLon()) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = getElevation() != 0.0d ? Double.doubleToLongBits(getElevation()) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + ondate;
        result = 31 * result + offdate;
        result = 31 * result + (net != null ? net.hashCode() : 0);
        result = 31 * result + (auth != null ? auth.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (unmappedSta != null ? unmappedSta.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public StationInfo newCopy() {
        return new Site(getSta(), "-", getLat(), getLon(), getElevation());
    }

    @Override
    public String getMapAnnotation() {
        return "";
    }

    @Override
    public Collection<Sensitivity> getSensitivities() {
        return new ArrayList<>();
    }

    public Epoch getEpoch() {
        TimeT begin = new TimeT(getOndate() / 1000, getOndate() % 1000, 0, 0, 0, 0);
        int temp = offdate + 1;
        TimeT end = new TimeT(temp / 1000, temp % 1000, 0, 0, 0, 0);
        end = end.subtract(.001);
        return new Epoch(begin, end);
    }
}
