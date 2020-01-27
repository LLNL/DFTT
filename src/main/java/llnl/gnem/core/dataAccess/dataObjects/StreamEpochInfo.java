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

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class StreamEpochInfo implements Serializable {

    private final long streamEpochId;
    private final StreamInfo streamInfo;
    private final double beginTime;
    private final double endTime;
    private final Double depth;
    private final Double azimuth;
    private final Double dip;
    private final Double samprate;
    private static final long serialVersionUID = -4383978784428503033L;

    public StreamEpochInfo(long streamEpochId,
            StreamInfo streamInfo,
            double beginTime,
            double endTime,
            Double depth,
            Double azimuth,
            Double dip,
            Double samprate) {
        this.streamEpochId = streamEpochId;
        this.streamInfo = streamInfo;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.depth = depth;
        this.azimuth = azimuth;
        this.dip = dip;
        this.samprate = samprate;
    }

    public StreamEpochInfo(StreamEpochInfo other) {
        if (other == null) {
            streamEpochId = -1;
            streamInfo = null;
            beginTime = -999999999.0;
            endTime = 99999999999.0;
            depth = -999.0;
            azimuth = -999.0;
            dip = -999.0;
            samprate = -999.0;
        } else {
            this.streamEpochId = other.streamEpochId;
            this.streamInfo = other.streamInfo != null ? new StreamInfo(other.streamInfo) : null;
            this.beginTime = other.beginTime;
            this.endTime = other.endTime;
            this.depth = other.depth;
            this.azimuth = other.azimuth;
            this.dip = other.dip;
            this.samprate = other.samprate;
        }
    }

    /**
     * @return the streamEpochId
     */
    public long getStreamEpochId() {
        return streamEpochId;
    }

    /**
     * @return the streamInfo
     */
    public StreamInfo getStreamInfo() {
        return streamInfo;
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
     * @return the depth
     */
    public Double getDepth() {
        return depth;
    }

    /**
     * @return the azimuth
     */
    public Double getAzimuth() {
        return azimuth;
    }

    /**
     * @return the dip
     */
    public Double getDip() {
        return dip;
    }

    /**
     * @return the samprate
     */
    public Double getSamprate() {
        return samprate;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (int) this.streamEpochId;
        hash = 13 * hash + Objects.hashCode(this.streamInfo);
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.beginTime) ^ (Double.doubleToLongBits(this.beginTime) >>> 32));
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.endTime) ^ (Double.doubleToLongBits(this.endTime) >>> 32));
        hash = 13 * hash + Objects.hashCode(this.depth);
        hash = 13 * hash + Objects.hashCode(this.azimuth);
        hash = 13 * hash + Objects.hashCode(this.dip);
        hash = 13 * hash + Objects.hashCode(this.samprate);
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
        final StreamEpochInfo other = (StreamEpochInfo) obj;
        if (this.streamEpochId != other.streamEpochId) {
            return false;
        }
        if (Double.doubleToLongBits(this.beginTime) != Double.doubleToLongBits(other.beginTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.endTime) != Double.doubleToLongBits(other.endTime)) {
            return false;
        }
        if (!Objects.equals(this.streamInfo, other.streamInfo)) {
            return false;
        }
        if (!Objects.equals(this.depth, other.depth)) {
            return false;
        }
        if (!Objects.equals(this.azimuth, other.azimuth)) {
            return false;
        }
        if (!Objects.equals(this.dip, other.dip)) {
            return false;
        }
        if (!Objects.equals(this.samprate, other.samprate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StreamEpochInfo{" + "streamEpochId=" + streamEpochId + ", streamInfo=" + streamInfo + ", beginTime=" + beginTime + ", endTime=" + endTime + ", depth=" + depth + ", azimuth=" + azimuth + ", dip=" + dip + ", samprate=" + samprate + '}';
    }

}
