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
package llnl.gnem.core.waveform.seismogram;

import com.google.common.base.Objects;
import java.io.Serializable;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.io.BinaryData;

/**
 * COPYRIGHT NOTICE GnemUtils Version 1.0 Copyright (C) 2005 Lawrence Livermore
 * National Laboratory. User: dodge1 Date: Mar 23, 2006
 */
public class BasicSeismogram extends TimeSeries implements Serializable {

    private final StreamKey streamKey;
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

    @Override
    public BasicSeismogram crop(int start, int end) {
        TimeSeries tmp = super.crop(start, end);
        return new BasicSeismogram(waveformID, streamKey, tmp);
    }
    
    public static BasicSeismogram unionOf( BasicSeismogram bs1, BasicSeismogram bs2){
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
        StringBuilder s = new StringBuilder("Sta = " + getSta() + ", Chan = " + getChan() + ", ");
        s.append(super.toString());
        return s.toString();
    }

    public String getSta() {
        return streamKey.getSta();
    }
 
    @Override
    public Long getWaveformID() {
        return waveformID;
    }

    public String getChan() {
        return streamKey.getChan();
    }
    
    public BasicSeismogram replaceChan( String newChan ){
        StreamKey newKey = streamKey.replaceChan(newChan);
        return new BasicSeismogram(waveformID, newKey, getData(),  getSamprate(),  getTime());
    }

    public StreamKey getStreamKey() {
        return streamKey;
    }

    /* (non-Javadoc)
     * @see llnl.gnem.core.waveform.TimeSeries#compareTo(llnl.gnem.core.waveform.TimeSeries)
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
