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
package llnl.gnem.dftt.core.dataAccess.dataObjects;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class StationEpoch {

    private final String stationSource;
    private final String networkCode;
    private final int netStartDate;
    private final String stationCode;
    private final String description;
    private final double lat;
    private final double lon;
    private final Double elev;
    private final double beginTime;
    private final double endTime;
    private final long networkId;
    private final long stationId;
    private final String locationCode;
    private final Long arrayId;
    private final Long arrayElementId;

    /**
     * @return the arrayId
     */
    public Long getArrayId() {
        return arrayId;
    }

    /**
     * @return the arrayElementId
     */
    public Long getArrayElementId() {
        return arrayElementId;
    }
    
    public boolean isSingleStation() {
        return arrayId == null;
    }

    public StationEpoch(String stationSource,
            String networkCode,
            int netStartDate,
            String stationCode,
            String description,
            double lat,
            double lon,
            Double elev,
            double beginTime,
            double endTime,
            long networkId,
            long stationId,
            Long stationEpochId,
            Long arrayId,
            Long arrayElementId) {
        this.stationSource = stationSource;
        this.networkCode = networkCode;
        this.netStartDate = netStartDate;
        this.stationCode = stationCode;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.elev = elev;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.networkId = networkId;
        this.stationId = stationId;
        this.arrayId = arrayId;
        this.arrayElementId = arrayElementId;
        locationCode = null;
    }
     
    public StationEpoch(String stationSource, 
            String networkCode, 
            int netStartDate, 
            String stationCode,
            String locationCode,
            String description, 
            double lat, 
            double lon, 
            Double elev, 
            double beginTime, 
            double endTime, 
            long networkId, 
            long stationId, 
            Long arrayId,
            Long arrayElementId) {
        this.stationSource = stationSource;
        this.networkCode = networkCode;
        this.netStartDate = netStartDate;
        this.stationCode = stationCode;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.elev = elev;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.networkId = networkId;
        this.stationId = stationId;
        this.arrayId = arrayId;
        this.arrayElementId = arrayElementId;
        this.locationCode = locationCode;
    }

    /**
     * @return the stationSource
     */
    public String getStationSource() {
        return stationSource;
    }

    /**
     * @return the networkCode
     */
    public String getNetworkCode() {
        return networkCode;
    }

    /**
     * @return the netStartDate
     */
    public int getNetStartDate() {
        return netStartDate;
    }

   /**
     * @return the stationCode
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @return the elev
     */
    public Double getElev() {
        return elev;
    }

    /**
     * @return the beginTime
     */
    public double getBeginTime() {
        return beginTime;
    }

    /**
     * @return the endTime
     */
    public double getEndTime() {
        return endTime;
    }

    /**
     * @return the networkId
     */
    public long getNetworkId() {
        return networkId;
    }

    /**
     * @return the stationId
     */
    public long getStationId() {
        return stationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    @Override
    public String toString() {
        return "StationEpoch{" + "stationSource=" + stationSource + ", networkCode=" + networkCode + ", netStartDate=" + netStartDate + ", stationCode=" + stationCode + ", description=" + description + ", lat=" + lat + ", lon=" + lon + ", elev=" + elev + ", beginTime=" + beginTime + ", endTime=" + endTime + ", networkId=" + networkId + ", stationId=" + stationId + ", locationCode=" + locationCode + ", arrayId=" + arrayId + ", arrayElementId=" + arrayElementId + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.stationSource);
        hash = 71 * hash + Objects.hashCode(this.networkCode);
        hash = 71 * hash + this.netStartDate;
        hash = 71 * hash + Objects.hashCode(this.stationCode);
        hash = 71 * hash + Objects.hashCode(this.description);
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 71 * hash + Objects.hashCode(this.elev);
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.beginTime) ^ (Double.doubleToLongBits(this.beginTime) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.endTime) ^ (Double.doubleToLongBits(this.endTime) >>> 32));
        hash = 71 * hash + (int) (this.networkId ^ (this.networkId >>> 32));
        hash = 71 * hash + (int) (this.stationId ^ (this.stationId >>> 32));
        hash = 71 * hash + Objects.hashCode(this.locationCode);
        hash = 71 * hash + Objects.hashCode(this.arrayId);
        hash = 71 * hash + Objects.hashCode(this.arrayElementId);
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
        final StationEpoch other = (StationEpoch) obj;
        if (this.netStartDate != other.netStartDate) {
            return false;
        }
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        if (Double.doubleToLongBits(this.beginTime) != Double.doubleToLongBits(other.beginTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.endTime) != Double.doubleToLongBits(other.endTime)) {
            return false;
        }
        if (this.networkId != other.networkId) {
            return false;
        }
        if (this.stationId != other.stationId) {
            return false;
        }
        if (!Objects.equals(this.stationSource, other.stationSource)) {
            return false;
        }
        if (!Objects.equals(this.networkCode, other.networkCode)) {
            return false;
        }
        if (!Objects.equals(this.stationCode, other.stationCode)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.locationCode, other.locationCode)) {
            return false;
        }
        if (!Objects.equals(this.elev, other.elev)) {
            return false;
        }
        if (!Objects.equals(this.arrayId, other.arrayId)) {
            return false;
        }
        if (!Objects.equals(this.arrayElementId, other.arrayElementId)) {
            return false;
        }
        return true;
    }

    }
