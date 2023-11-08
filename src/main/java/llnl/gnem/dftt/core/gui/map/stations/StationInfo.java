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
package llnl.gnem.dftt.core.gui.map.stations;

import java.util.Collection;
import java.util.Objects;
import llnl.gnem.dftt.core.geom.GeographicCoordinate;
import llnl.gnem.dftt.core.gui.map.location.LocationInfo;
import llnl.gnem.dftt.core.metadata.site.core.Sensitivity;
import llnl.gnem.dftt.core.seismicData.Station;
import llnl.gnem.dftt.core.util.StationKey;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public abstract class StationInfo extends LocationInfo<Station<GeographicCoordinate>> {

    private final Double beginTime;
    private final Double endTime;
    private final Long networkId;
    private final Long stationId;
    private final Long stationEpochId;
    private final StationKey station;

    @Override
    public String toString() {
        return "StationInfo{" + "networkId=" + networkId + ", stationId=" + stationId + ", station=" + station + '}';
    }

    /**
     * @return the beginTime
     */
    public Double getBeginTime() {
        return beginTime;
    }

    /**
     * @return the endTime
     */
    public Double getEndTime() {
        return endTime;
    }

    /**
     * @return the networkId
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * @return the stationId
     */
    public Long getStationId() {
        return stationId;
    }

    /**
     * @return the stationEpochId
     */
    public Long getStationEpochId() {
        return stationEpochId;
    }

    /**
     * @return the station
     */
    public StationKey getStation() {
        return station;
    }
    public String getSource()
    {
        return station.getAgency();
    }
    
    public String getNetworkCode()
    {
        return station.getNet();
    }
    
    public String getStationCode()
    {
        return station.getSta();
    }
    
    public Integer getNetStartDate()
    {
        return station.getNetJdate();
    }
    
    public String getDescription()
    {
        return getName();
    }
    
    public Double getElevation()
    {
        return super.getLocation().getElev();
    }

    public StationInfo(String stationSource,
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
            Long stationEpochId) {
        super(Station.fromGeo(lat, lon, elev, description));
        station = new StationKey(stationSource, networkCode, netStartDate, stationCode);

        this.beginTime = beginTime;
        this.endTime = endTime;
        this.networkId = networkId;
        this.stationId = stationId;
        this.stationEpochId = stationEpochId;
    }

    public StationInfo(String stationCode, double lat, double lon, Double elev, String description, long stationId) {
        super(Station.fromGeo(lat, lon, elev, description));
        this.beginTime = null;
        this.endTime = null;
        this.networkId = null;
        this.stationId = stationId;
        this.stationEpochId = null;
        this.station = new StationKey(stationCode);
    }

    public StationInfo(StationInfo other) {
        super(other.getLocation());
        station = new StationKey(other.station);
        this.beginTime = other.beginTime;
        this.endTime = other.endTime;
        this.networkId = other.networkId;
        this.stationId = other.stationId;
        this.stationEpochId = other.stationEpochId;
    }

    /**
     * Return whether or not the given station is toggled to disabled
     *
     * @return
     */
    public abstract boolean isDisabled();

    /**
     * Retrieve the list of sensitivities for the given Station
     *
     * @return
     */
    public abstract Collection<Sensitivity> getSensitivities();

    public abstract StationInfo newCopy();

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.beginTime);
        hash = 97 * hash + Objects.hashCode(this.endTime);
        hash = 97 * hash + Objects.hashCode(this.networkId);
        hash = 97 * hash + Objects.hashCode(this.stationId);
        hash = 97 * hash + Objects.hashCode(this.stationEpochId);
        hash = 97 * hash + Objects.hashCode(this.station);
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
        final StationInfo other = (StationInfo) obj;
        if (!Objects.equals(this.beginTime, other.beginTime)) {
            return false;
        }
        if (!Objects.equals(this.endTime, other.endTime)) {
            return false;
        }
        if (!Objects.equals(this.networkId, other.networkId)) {
            return false;
        }
        if (!Objects.equals(this.stationId, other.stationId)) {
            return false;
        }
        if (!Objects.equals(this.stationEpochId, other.stationEpochId)) {
            return false;
        }
        if (!Objects.equals(this.station, other.station)) {
            return false;
        }
        return true;
    }
    
    
}
