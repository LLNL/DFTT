/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class StationEpoch {

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
    
    public boolean isSingleStation()
    {
        return arrayId == null;
    }

     
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
    private final long stationEpochId;
    private final Long arrayId;
    private final Long arrayElementId;

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
            long stationEpochId,
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
        this.stationEpochId = stationEpochId;
        this.arrayId = arrayId;
        this.arrayElementId=arrayElementId;
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

    /**
     * @return the stationEpochId
     */
    public long getStationEpochId() {
        return stationEpochId;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.stationSource);
        hash = 89 * hash + Objects.hashCode(this.networkCode);
        hash = 89 * hash + this.netStartDate;
        hash = 89 * hash + Objects.hashCode(this.stationCode);
        hash = 89 * hash + Objects.hashCode(this.description);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 89 * hash + Objects.hashCode(this.elev);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.beginTime) ^ (Double.doubleToLongBits(this.beginTime) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.endTime) ^ (Double.doubleToLongBits(this.endTime) >>> 32));
        hash = 89 * hash + (int)this.networkId;
        hash = 89 * hash + (int)this.stationId;
        hash = 89 * hash + (int)this.stationEpochId;
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
        if (this.stationEpochId != other.stationEpochId) {
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
        if (!Objects.equals(this.elev, other.elev)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StationEpoch{" + "stationSource=" + stationSource + ", networkCode=" + networkCode +
                ", netStartDate=" + netStartDate + ", stationCode=" + stationCode + ", description=" +
                description + ", lat=" + lat + ", lon=" + lon + ", elev=" + elev + ", beginTime=" + beginTime + ", endTime=" + endTime +
                ", networkId=" + networkId + ", stationId=" + stationId + ", stationEpochId=" + stationEpochId  + '}';
    }
    
}
