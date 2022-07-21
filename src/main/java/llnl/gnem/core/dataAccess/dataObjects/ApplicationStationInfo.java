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
package llnl.gnem.core.dataAccess.dataObjects;

import java.util.ArrayList;
import java.util.Collection;

import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.metadata.site.core.Sensitivity;

public class ApplicationStationInfo extends StationInfo {

    private static final String SENSITIVITY_HEADER = "Sensitivity: ";
    private static final String DISTRIBUTION_HEADER = "Distribution: ";
    private static final String PUBLICATION_HEADER = "Publication: ";

    private final boolean waveformStation;
    private final String locationCode;
    private final Long arrayId;
    private final Long arrayElementId;
    private final Collection<SensitivityInfo> sensitivities;

    private boolean hasTrackLines;

    public ApplicationStationInfo(String sta,
            double lat,
            double lon,
            double elev,
            String staname,
            long siteid,
            boolean waveformStation,
            Long arrayId,
            Long arrayElementId,
            Collection<SensitivityInfo> sensitivities) {
        super(sta, lat, lon, elev, staname, siteid);
        this.waveformStation = waveformStation;
        this.sensitivities = new ArrayList<>(sensitivities);
        sensitivities.add(new SensitivityInfo(SensitivityInfoTypes.UNSENSITIVE.getType(), "YES", "YES"));
        this.hasTrackLines = true;
        this.arrayId = arrayId;
        this.arrayElementId = arrayElementId;
        locationCode = null;
    }

    public ApplicationStationInfo(StationEpoch se) {
        super(se.getStationSource(),
                se.getNetworkCode(),
                se.getNetStartDate(),
                se.getStationCode(),
                se.getDescription(),
                se.getLat(),
                se.getLon(),
                se.getElev(),
                se.getBeginTime(),
                se.getEndTime(),
                se.getNetworkId(),
                se.getStationId(),
                null);
        waveformStation = true;
        this.sensitivities = new ArrayList<>();
        sensitivities.add(new SensitivityInfo(SensitivityInfoTypes.UNSENSITIVE.getType(), "YES", "YES"));
        this.hasTrackLines = true;
        this.arrayId = se.getArrayId();
        this.arrayElementId = se.getArrayElementId();
        locationCode = se.getLocationCode();
    }

    public ApplicationStationInfo(String stationSource,
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
            Long arrayElementId,
            boolean waveformStation,
            Collection<SensitivityInfo> sensitivities,
            boolean hasTrackLines) {
        super(stationSource,
                networkCode,
                netStartDate,
                stationCode,
                description,
                lat,
                lon,
                elev,
                beginTime,
                endTime,
                networkId,
                stationId,
                stationEpochId);
        this.waveformStation = waveformStation;
        this.sensitivities = new ArrayList<>(sensitivities);
        this.hasTrackLines = hasTrackLines;
        this.arrayId = arrayId;
        this.arrayElementId = arrayElementId;
        locationCode = null;
    }
    
 
    public ApplicationStationInfo(String stationSource,
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
            long stationEpochId,
            Long arrayId,
            Long arrayElementId,
            boolean waveformStation,
            Collection<SensitivityInfo> sensitivities,
            boolean hasTrackLines) {
        super(stationSource,
                networkCode,
                netStartDate,
                stationCode,
                description,
                lat,
                lon,
                elev,
                beginTime,
                endTime,
                networkId,
                stationId,
                stationEpochId);
        this.waveformStation = waveformStation;
        this.sensitivities = new ArrayList<>(sensitivities);
        this.hasTrackLines = hasTrackLines;
        this.arrayId = arrayId;
        this.arrayElementId = arrayElementId;
        this.locationCode = locationCode;
    }   
    
    
    
    public ApplicationStationInfo(String stationSource,
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
        super(stationSource,
                networkCode,
                netStartDate,
                stationCode,
                description,
                lat,
                lon,
                elev,
                beginTime,
                endTime,
                networkId,
                stationId,
                stationEpochId);
        this.waveformStation = true;
        this.sensitivities = new ArrayList<>();
        this.hasTrackLines = true;
        this.arrayId = arrayId;
        this.arrayElementId = arrayElementId;
        locationCode = null;
    }

    void addSensitivityInfo(SensitivityInfo sensitivity) {
        this.sensitivities.add(sensitivity);
    }

    void finalizeSensitivity() {
        if (sensitivities.isEmpty()) {
            sensitivities.add(SensitivityInfo.newDefault());
        }
    }

    public void setHasTrackLines(boolean value) {
        this.hasTrackLines = value;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public String getMapAnnotation() {
        String title = String.format("%s", getStation().toString());
        String infoString = String.format("%s <br>Lat = %8.3f, Lon = %9.3f<br>Elev = %6.3f m", getDescription(),
                getLat(), getLon(), getElevation());
        String sensString = "<br>" + SENSITIVITY_HEADER + getSensitivityString();
        String distString = "<br>" + DISTRIBUTION_HEADER + getDistributionString();
        String pubString = "<br>" + PUBLICATION_HEADER + getPublicationString();
        return "<p><b>" + title + "</b></p>" + infoString + sensString + distString + pubString;
    }

    public String getDistributionString() {
        StringBuilder sb = new StringBuilder();
        for (SensitivityInfo sens : sensitivities) {
            sb.append(sens.getDistribution());
            if (sensitivities.size() > 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    public String getPublicationString() {
        StringBuilder sb = new StringBuilder();
        for (SensitivityInfo sens : sensitivities) {
            sb.append(sens.getPublication());
            if (sensitivities.size() > 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    public String getSensitivityString() {
        StringBuilder sb = new StringBuilder();
        for (SensitivityInfo sens : sensitivities) {
            sb.append(sens.getSensitivityType());
            if (sensitivities.size() > 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    public boolean isWaveformStation() {
        return waveformStation;
    }

    public boolean hasTrackLines() {
        return hasTrackLines;
    }

    public boolean hasArrivals() {
        return false;
    }

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

    @Override
    public String toString() {
        return "ApplicationStationInfo{" + super.toString() +  ", arrayId=" + arrayId + ", arrayElementId=" + arrayElementId + '}';
    }
    
    

    @Override
    public StationInfo newCopy() {
        return new ApplicationStationInfo(this.getSource(),
                this.getNetworkCode(),
                this.getNetStartDate(),
                this.getStationCode(),
                locationCode,
                this.getDescription(),
                this.getLat(),
                this.getLon(),
                this.getElevation(),
                this.getBeginTime(),
                this.getEndTime(),
                this.getNetworkId(),
                this.getStationId(),
                this.getStationEpochId(),
                this.arrayId,
                this.arrayElementId,
                waveformStation,
                sensitivities,
                hasTrackLines);
    }

    @Override
    public Collection<Sensitivity> getSensitivities() {
        return new ArrayList<>();
    }

    public Collection<SensitivityInfo> getSensitivity() {
        return new ArrayList<>(sensitivities);
    }

    public SensitivityInfoTypes highestSensitivity() {
        SensitivityInfoTypes highestLevel = SensitivityInfoTypes.UNKNOWN;
        for (SensitivityInfo sensitivity : sensitivities) {
            SensitivityInfoTypes currentLevel = SensitivityInfoTypes.getSensitivity(sensitivity.getSensitivityType());
            if (currentLevel.getLevel() > highestLevel.getLevel()) {
                highestLevel = currentLevel;
}
            if (highestLevel == SensitivityInfoTypes.SENSITIVE) {
                break;
            }
        }
        return highestLevel == SensitivityInfoTypes.UNKNOWN ? SensitivityInfoTypes.SENSITIVE : highestLevel;
    }

    public boolean isStationSensitive() {
        return !(highestSensitivity() == SensitivityInfoTypes.UNSENSITIVE);
    }

    public String getLocationCode() {
        return locationCode;
    }
    
    
}
