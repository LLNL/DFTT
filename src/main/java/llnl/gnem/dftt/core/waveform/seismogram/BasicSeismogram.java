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
package llnl.gnem.dftt.core.waveform.seismogram;

import java.io.Serializable;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.io.BinaryData;

/**
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2005 Lawrence Livermore
 * National Laboratory. User: dodge1 Date: Mar 23, 2006
 */
public class BasicSeismogram extends TimeSeries implements Serializable {

    private StreamKey streamKey;
    private final Long waveformID;
    private static final long serialVersionUID = 4210755492799539547L;

    /**
     * No-arg constructor only used for serialization.
     */
    public BasicSeismogram() {
        super();
        streamKey = new StreamKey();
        waveformID = -1L;
    }

    public String createName() {
        StringBuilder sb = new StringBuilder();
        StreamKey key = getStreamKey();
        if (key.getNet() != null) {
            sb.append(key.getNet()).append(".");
        }
        sb.append(key.getSta()).append(".");
        sb.append(key.getChan()).append(".");
        sb.append(key.getLocationCode()).append(".");
        sb.append(getTimeAsDouble());
        return sb.toString();
    }

    public BasicSeismogram(Long wfid, String sta, String chan, float[] data, double samprate, TimeT time) {
        this(wfid, new StreamKey(sta, chan), data, samprate, time);
    }

    public BasicSeismogram(Long wfid, StreamKey streamKey, float[] data, double samprate, TimeT time) {
        super(data, samprate, time);
        this.waveformID = wfid;
        this.streamKey = streamKey;
    }

    public BasicSeismogram(Long wfid, StreamKey streamKey, BinaryData data, double samprate, TimeT time) {
        super(data, samprate, time);
        this.waveformID = wfid;
        this.streamKey = streamKey;
    }

    public BasicSeismogram(BasicSeismogram s) {
        super(s);
        this.streamKey = s.getStreamKey();
        waveformID = s.waveformID;
    }

    public BasicSeismogram(BasicSeismogram s, TimeSeries data) {
        super(data);
        this.streamKey = s.getStreamKey();
        waveformID = s.waveformID;
    }

    public BasicSeismogram(Long wfid, StreamKey stachan, TimeSeries series) {
        super(series);
        this.waveformID = wfid;
        this.streamKey = stachan;
    }

    public int[] getIntData() {
        int[] result = new int[getLength()];
        float[] tmp = getData();
        for (int j = 0; j < result.length; ++j) {
            result[j] = Math.round(tmp[j]);
        }
        return result;
    }

    public void setAgencyCode(String code) {
        if (streamKey == null) {
            streamKey = new StreamKey(code, null, null, null, null, null);
        } else {
            streamKey = streamKey.replaceAgency(code);
        }
    }

    public void setNetworkCode(String code) {
       if (streamKey == null) {
            streamKey = new StreamKey(null, code, null, null, null, null);
        } else {
            streamKey = streamKey.replaceNetwork(code);
        }
    }

    public void setNetStartDate(Integer date) {
       if (streamKey == null) {
            streamKey = new StreamKey(null, null, date, null, null, null);
        } else {
            streamKey = streamKey.replaceNetDate(date);
        }
    }

    public void setStationCode(String code) {
       if (streamKey == null) {
            streamKey = new StreamKey(null, null, null, code, null, null);
        } else {
            streamKey = streamKey.replaceStation(code);
        }
    }

    public void setChannelCode(String code) {
       if (streamKey == null) {
            streamKey = new StreamKey(null, null, null, null, code, null);
        } else {
            streamKey = streamKey.replaceChan(code);
        }
    }

    public void setLocationCode(String code) {
       if (streamKey == null) {
            streamKey = new StreamKey(null, null, null, null, null, code);
        } else {
            streamKey = streamKey.replaceLocid(code);
        }
    }

    @Override
    public BasicSeismogram crop(int start, int end) {
        TimeSeries tmp = super.crop(start, end);
        return new BasicSeismogram(waveformID, streamKey, tmp);
    }

    public static BasicSeismogram unionOf(BasicSeismogram bs1, BasicSeismogram bs2) {
        TimeSeries tmp = TimeSeries.unionOf(bs1, bs2);
        return new BasicSeismogram(null, bs1.getStreamKey(), tmp);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + java.util.Objects.hashCode(this.streamKey);
        hash = 43 * hash + java.util.Objects.hashCode(this.waveformID);
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
        final BasicSeismogram other = (BasicSeismogram) obj;
        if (!java.util.Objects.equals(this.streamKey, other.streamKey)) {
            return false;
        }
        if (!java.util.Objects.equals(this.waveformID, other.waveformID)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(streamKey.toString() + ", ");
        s.append(super.toString());
        return s.toString();
    }

    public String getAgency() {
        return streamKey != null ? streamKey.getAgency() : null;
    }

    public String getSta() {
        return streamKey != null ? streamKey.getSta() : null;
    }

    @Override
    public Long getWaveformID() {
        return waveformID;
    }

    public String getChan() {
        return streamKey != null ? streamKey.getChan() : null;
    }

    public BasicSeismogram replaceChan(String newChan) {
        StreamKey newKey = streamKey.replaceChan(newChan);
        return new BasicSeismogram(waveformID, newKey, getData(), getSamprate(), getTime());
    }

    public StreamKey getStreamKey() {
        return streamKey;
    }

    /* (non-Javadoc)
     * @see llnl.gnem.dftt.core.waveform.TimeSeries#compareTo(llnl.gnem.dftt.core.waveform.TimeSeries)
     * Note this overrides the default comparator so that Maps and Sorts behave as you would
     * expect when using this class.
     */
    @Override
    public int compareTo(TimeSeries other) {
        int rc = super.compareTo(other);
        if (rc == 0) {
            if (other instanceof BasicSeismogram) {
                BasicSeismogram basic = (BasicSeismogram) other;
                rc = streamKey.compareTo(basic.getStreamKey());
                if (rc == 0) {
                    if (waveformID > basic.waveformID) {
                        return 1;
                    } else if (waveformID < basic.waveformID) {
                        return -1;
                    }
                }
            }
        }
        return rc;
    }

    /**
     * Specific compare for this class. Note TimeSeries has a comparator
     * defined, so it {@link compareTo(TimeSeries)} will be called when using
     * the default comparator for Collections, Sets, Maps.
     *
     * Note currently not using the local TreeMap of variables to affect
     * sorting.
     *
     * @param other
     * @return
     */
    public int compareTo(BasicSeismogram other) {
        return compareTo((TimeSeries) other);
    }

}
