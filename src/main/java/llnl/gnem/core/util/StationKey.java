package llnl.gnem.core.util;

import java.io.Serializable;
import java.util.Objects;

public class StationKey implements Comparable<StationKey>, Serializable {

    private final String source;
    private final String net;
    private final Integer netJdate;
    private final String sta;
    private static final long serialVersionUID = -1711106006337309198L;

    public StationKey() {
        source = null;
        net = null;
        netJdate = null;
        sta = null;
    }

    public StationKey(StationKey other) {
        this.source = other.source;
        this.net = other.net;
        this.netJdate = other.netJdate;
        this.sta = other.sta;
    }

    public String getPlotLabel() {
        return toString();
    }

    public StationKey(String source, String net, Integer netJdate, String sta) {
        this.source = source;
        this.net = net;
        this.netJdate = netJdate;
        this.sta = sta.trim();
    }

    public StationKey(String source, String net, String sta) {
        this.source = source;
        this.net = net;
        this.netJdate = null;
        this.sta = sta.trim();
    }

    public StationKey(String sta) {
        source = null;
        net = null;
        netJdate = null;
        this.sta = sta.trim();
    }

    public StationKey(String net, String sta) {
        this.source = null;
        this.net = net;
        this.netJdate = null;
        this.sta = sta.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (source != null && !source.equals("-")) {
            sb.append("source = ").append(source);
            sb.append(", ");
        }
        if (net != null && !net.equals("-")) {
            sb.append("net = ").append(net);
            if (netJdate != null && netJdate >= 1900001) {
                sb.append("(").append(netJdate).append(")");
            }
            sb.append(", ");
        }
        sb.append("sta = ");
        sb.append(sta);

        return sb.toString();
    }

    @Override
    public int compareTo(StationKey other) {
        final int EQUAL = 0;

        //this optimization is usually worthwhile, and can
        //always be added
        if (this == other) {
            return EQUAL;
        }

        if (source != null && other.source != null) {
            int cmp = source.compareTo(other.source);
            if (cmp != 0) {
                return cmp;
            }

        }
        if( net!= null && other.net != null){
            int cmp = net.compareTo(other.net);
            if (cmp != 0) {
                return cmp;
            }
        }
        if(netJdate != null && other.netJdate != null){
            int cmp = netJdate.compareTo(other.netJdate);
            if (cmp != 0) {
                return cmp;
            }
        }
        return sta.compareTo(other.sta);

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.source);
        hash = 97 * hash + Objects.hashCode(this.net);
        hash = 97 * hash + Objects.hashCode(this.netJdate);
        hash = 97 * hash + Objects.hashCode(this.sta);
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
        final StationKey other = (StationKey) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.net, other.net)) {
            return false;
        }
        if (!Objects.equals(this.sta, other.sta)) {
            return false;
        }
        if (!Objects.equals(this.netJdate, other.netJdate)) {
            return false;
        }
        return true;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @return the net
     */
    public String getNet() {
        return net;
    }

    /**
     * @return the netJdate
     */
    public Integer getNetJdate() {
        return netJdate;
    }

    /**
     * @return the sta
     */
    public String getSta() {
        return sta;
    }

    boolean isHasNet() {
        return net != null && !net.isEmpty();
    }

    boolean isHasSta() {
        return sta != null && !sta.isEmpty() && !sta.equals("-");
    }
}
