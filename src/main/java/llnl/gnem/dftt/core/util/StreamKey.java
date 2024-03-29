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
import java.util.Objects;

public class StreamKey implements Comparable<StreamKey>, Serializable {

    private final StationKey station;
    private final String chan;
    private final String locationCode;
    private static final long serialVersionUID = 6263203003052015561L;

    public StreamKey() {
        station = null;
        chan = null;
        locationCode = null;
    }

    public String getPlotLabel() {
        return toString();
    }

    public StreamKey(StreamKey other) {
        this.station = new StationKey(other.station);
        this.chan = other.chan;
        this.locationCode = other.locationCode;
    }

    public StreamKey(StationKey station, String chan, String locationCode) {
        this.station = station;
        this.chan = chan != null ? chan.trim() : null;
        this.locationCode = locationCode;
    }

    public StreamKey(String agency, String net, Integer netJdate, String sta, String chan, String locationCode) {
        station = new StationKey(agency, net, netJdate, sta);
        this.chan = chan != null ? chan.trim() : null;
        this.locationCode = locationCode != null ? locationCode.trim() : null;
    }

    public StreamKey(String agency, String net, String sta, String chan, String locationCode) {
        station = new StationKey(agency, net, sta);
        this.chan = chan != null ? chan.trim() : null;
        this.locationCode = locationCode != null ? locationCode.trim() : null;
    }

    public StreamKey(String sta, String chan) {
        station = new StationKey(sta);
        this.chan = chan != null ? chan.trim() : null;
        locationCode = null;
    }

    public StreamKey(String net, String sta, String chan, String locationCode) {
        station = new StationKey(null, net, null, sta);
        this.chan = chan != null ? chan.trim() : null;
        this.locationCode = locationCode != null ? locationCode.trim() : null;
    }
    public StreamKey replaceAgency(String newAgency) {
        return new StreamKey(newAgency, this.getNet(), this.getNetJdate(), this.getSta(), this.getChan(), this.getLocationCode());
    }
    public StreamKey replaceNetwork(String newNet) {
        return new StreamKey(this.getAgency(), newNet, this.getNetJdate(), this.getSta(), this.getChan(), this.getLocationCode());
    }
    public StreamKey replaceNetDate(Integer newDate) {
        return new StreamKey(this.getAgency(), this.getNet(), newDate, this.getSta(), this.getChan(), this.getLocationCode());
    }
    public StreamKey replaceStation(String newSta) {
        return new StreamKey(this.getAgency(), this.getNet(), this.getNetJdate(), newSta, this.getChan(), this.getLocationCode());
    }

    public StreamKey replaceChan(String newChan) {
        return new StreamKey(this.getAgency(), this.getNet(), this.getNetJdate(), this.getSta(), newChan, this.getLocationCode());
    }

    public StreamKey replaceLocid(String newLocid) {
        return new StreamKey(this.getAgency(), this.getNet(), this.getNetJdate(), this.getSta(), this.getChan(), newLocid);
    }

    public StationKey getStationKey() {
        return station;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getAgency() != null) {
            sb.append(getAgency()).append(", ");
        }
        if (getNet() != null) {
            sb.append(getNet());
        }
        if (this.getNetJdate() != null) {
            sb.append("(").append(getNetJdate()).append("), ");
        } else if (sb.length() > 0) {
        sb.append(", ");
        }
        sb.append(getSta());
        sb.append(", ");
        sb.append(chan);
        if (locationCode != null && !locationCode.isEmpty()) {
            sb.append(", ");
            sb.append("locationCode = ");
            sb.append(locationCode);
        }

        return sb.toString();
    }

    @Override
    public int compareTo(StreamKey other) {
        //this optimization is usually worthwhile, and can
        //always be added
        if (this == other) {
            return 0;
        }

        int cmp = station.compareTo(other.station);
        if (cmp != 0) {
            return cmp;
        }

        if (chan != null && other.chan != null) {
            cmp = chan.compareTo(other.chan);
            if (cmp != 0) {
                return cmp;
            }
        }

        if (locationCode != null && other.locationCode != null) {
            cmp = locationCode.compareTo(other.locationCode);
        }

        return cmp;
    }

    /**
     * @return the source
     */
    public String getAgency() {
        return station != null ? station.getAgency() : null;
    }

    /**
     * @return the net
     */
    public String getNet() {
        return station != null ? station.getNet() : null;
    }

    public boolean isHasNet() {
        return station != null ? station.isHasNet() : null;
    }

    /**
     * @return the netJdate
     */
    public Integer getNetJdate() {
        return station != null ? station.getNetJdate() : null;
    }

    /**
     * @return the sta
     */
    public String getSta() {
        return station != null ? station.getSta() : null;
    }

    public boolean isHasSta() {
        return station != null ? station.isHasSta() : false;
    }

    /**
     * @return the chan
     */
    public String getChan() {
        return chan;
    }

    public boolean isHasChan() {
        return chan != null && !chan.isEmpty();
    }

    /**
     * @return the locationCode
     */
    public String getLocationCode() {
        return locationCode;
    }

    public boolean isHasLocid() {
        return locationCode != null && !locationCode.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.station);
        hash = 23 * hash + Objects.hashCode(this.chan);
        hash = 23 * hash + Objects.hashCode(this.locationCode);
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
        final StreamKey other = (StreamKey) obj;
        if (!Objects.equals(this.chan, other.chan)) {
            return false;
        }
        if (!Objects.equals(this.locationCode, other.locationCode)) {
            return false;
        }
        if (!Objects.equals(this.station, other.station)) {
            return false;
        }
        return true;
    }

    public String getShortName() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(station.getAgency() != null ? station.getAgency() : "?");
        sb.append(", ");
        sb.append(station.getNet() != null ? station.getNet() : '?');
        sb.append(", ");
        sb.append(station.getSta() != null ? station.getSta() : '?');
        sb.append(", ");
        sb.append(chan != null ? chan : '?');
        sb.append(", ");
        sb.append(locationCode != null ? locationCode : '?');
        sb.append(')');

        return sb.toString();
    }

}
