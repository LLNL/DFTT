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
package llnl.gnem.core.util;

import java.io.Serializable;
import java.util.Objects;

public class StationKey implements Comparable<StationKey>, Serializable {

    private final String agency;
    private final String net;
    private final Integer netJdate;
    private final String sta;
    private static final long serialVersionUID = -1711106006337309198L;

    public StationKey() {
        agency = null;
        net = null;
        netJdate = null;
        sta = null;
    }

    public StationKey(StationKey other) {
        this.agency = other.agency;
        this.net = other.net;
        this.netJdate = other.netJdate;
        this.sta = other.sta;
    }

    public String getPlotLabel() {
        return toString();
    }

    public StationKey(String agency, String net, Integer netJdate, String sta) {
        this.agency = agency;
        this.net = net;
        this.netJdate = netJdate;
        this.sta = sta.trim();
    }

    public StationKey(String agency, String net, String sta) {
        this.agency = agency;
        this.net = net;
        this.netJdate = null;
        this.sta = sta.trim();
    }

    public StationKey(String sta) {
        agency = null;
        net = null;
        netJdate = null;
        this.sta = sta.trim();
    }

    public StationKey(String net, String sta) {
        this.agency = null;
        this.net = net;
        this.netJdate = null;
        this.sta = sta.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (agency != null && !agency.equals("-")) {
            sb.append("source = ").append(agency);
            sb.append(", ");
        }
        if (net != null && !net.equals("-")) {
            sb.append("net = ").append(net);
            if (netJdate != null && netJdate >= 1000000) {
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

        if (agency != null && other.agency != null) {
            int cmp = agency.compareTo(other.agency);
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
        hash = 97 * hash + Objects.hashCode(this.agency);
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
        if (!Objects.equals(this.agency, other.agency)) {
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
     * @return the agency
     */
    public String getAgency() {
        return agency;
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
