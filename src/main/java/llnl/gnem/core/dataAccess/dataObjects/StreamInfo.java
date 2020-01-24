/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

import java.io.Serializable;
import java.util.Objects;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class StreamInfo implements Serializable {

    private final long streamId;
    private final long stationId;
    private final StreamKey streamKey;
    private final String band;
    private final String instrumentCode;
    private final String orientation;
    private final String description;
    private static final long serialVersionUID = 7788917611246280977L;

    public StreamInfo(long streamId,
            long stationId,
            StreamKey streamKey,
            String band,
            String instrumentCode,
            String orientation,
            String description) {
        this.streamId = streamId;
        this.stationId = stationId;
        this.streamKey = streamKey;
        this.band = band;
        this.instrumentCode = instrumentCode;
        this.orientation = orientation;
        this.description = description;
    }

    public StreamInfo(StreamInfo other) {
        this.streamId = other.streamId;
        this.stationId = other.stationId;
        this.streamKey = other.streamKey;
        this.band = other.band;
        this.instrumentCode = other.instrumentCode;
        this.orientation = other.orientation;
        this.description = other.description;
    }

    /**
     * @return the streamId
     */
    public long getStreamId() {
        return streamId;
    }

    /**
     * @return the stationId
     */
    public long getStationId() {
        return stationId;
    }

    /**
     * @return the streamKey
     */
    public StreamKey getStreamKey() {
        return streamKey;
    }

    /**
     * @return the band
     */
    public String getBand() {
        return band;
    }

    /**
     * @return the instrumentCode
     */
    public String getInstrumentCode() {
        return instrumentCode;
    }

    /**
     * @return the orientation
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int)this.streamId;
        hash = 89 * hash + (int)this.stationId;
        hash = 89 * hash + Objects.hashCode(this.streamKey);
        hash = 89 * hash + Objects.hashCode(this.band);
        hash = 89 * hash + Objects.hashCode(this.instrumentCode);
        hash = 89 * hash + Objects.hashCode(this.orientation);
        hash = 89 * hash + Objects.hashCode(this.description);
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
        final StreamInfo other = (StreamInfo) obj;
        if (this.streamId != other.streamId) {
            return false;
        }
        if (this.stationId != other.stationId) {
            return false;
        }
        if (!Objects.equals(this.band, other.band)) {
            return false;
        }
        if (!Objects.equals(this.instrumentCode, other.instrumentCode)) {
            return false;
        }
        if (!Objects.equals(this.orientation, other.orientation)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.streamKey, other.streamKey)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StreamInfo{" + "streamId=" + streamId + ", stationId=" + stationId + ", streamKey=" + streamKey + ", band=" + band + ", instrumentCode=" + instrumentCode + ", orientation=" + orientation + ", description=" + description + '}';
    }

}
