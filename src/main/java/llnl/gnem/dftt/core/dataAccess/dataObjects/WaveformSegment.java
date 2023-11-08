/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.dftt.core.dataAccess.dataObjects;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class WaveformSegment {

    private final long waveformId;
    private final long adslChanelId;
    private final long eventId;
    private final double beginTime;
    private final double endTime;
    private final int nsamp;
    private final double samprate;
    private final String dataType;
    private final long foff;
    private final String dir;
    private final String dfile;
    private final Double calib;
    private final Double calper;
    private final int[] data;

    public WaveformSegment(long waveformId,
            long adslChanelId,
            long eventId,
            double beginTime,
            double endTime,
            int nsamp,
            double samprate,
            String dataType,
            long foff,
            String dir,
            String dfile,
            Double calib, 
            Double calper,
            int[] data) {
        this.waveformId = waveformId;
        this.adslChanelId = adslChanelId;
        this.eventId = eventId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.nsamp = nsamp;
        this.samprate = samprate;
        this.dataType = dataType;
        this.foff = foff;
        this.dir = dir;
        this.dfile = dfile;
        this.calib = calib;
        this.calper = calper;
        this.data = data.clone();
    }

    public long getWaveformId() {
        return waveformId;
    }

    public long getAdslChanelId() {
        return adslChanelId;
    }

    public long getEventId() {
        return eventId;
    }

    public double getBeginTime() {
        return beginTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public int getNsamp() {
        return nsamp;
    }

    public double getSamprate() {
        return samprate;
    }

    public String getDataType() {
        return dataType;
    }

    public long getFoff() {
        return foff;
    }

    public String getDir() {
        return dir;
    }

    public String getDfile() {
        return dfile;
    }

    public Double getCalib() {
        return calib;
    }

    public Double getCalper() {
        return calper;
    }

    public int[] getData() {
        return data.clone();
    }

    @Override
    public String toString() {
        return "WaveformSegment{" + "waveformId=" + waveformId + ", adslChanelId=" + adslChanelId + ", eventId=" + eventId + ", beginTime=" + beginTime + ", endTime=" + endTime + ", nsamp=" + nsamp + ", samprate=" + samprate + ", dataType=" + dataType + ", foff=" + foff + ", dir=" + dir + ", dfile=" + dfile + ", calib=" + calib + ", calper=" + calper + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.waveformId ^ (this.waveformId >>> 32));
        hash = 29 * hash + (int) (this.adslChanelId ^ (this.adslChanelId >>> 32));
        hash = 29 * hash + (int) (this.eventId ^ (this.eventId >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.beginTime) ^ (Double.doubleToLongBits(this.beginTime) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.endTime) ^ (Double.doubleToLongBits(this.endTime) >>> 32));
        hash = 29 * hash + this.nsamp;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.samprate) ^ (Double.doubleToLongBits(this.samprate) >>> 32));
        hash = 29 * hash + Objects.hashCode(this.dataType);
        hash = 29 * hash + (int) (this.foff ^ (this.foff >>> 32));
        hash = 29 * hash + Objects.hashCode(this.dir);
        hash = 29 * hash + Objects.hashCode(this.dfile);
        hash = 29 * hash + Objects.hashCode(this.calib);
        hash = 29 * hash + Objects.hashCode(this.calper);
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
        final WaveformSegment other = (WaveformSegment) obj;
        if (this.waveformId != other.waveformId) {
            return false;
        }
        if (this.adslChanelId != other.adslChanelId) {
            return false;
        }
        if (this.eventId != other.eventId) {
            return false;
        }
        if (Double.doubleToLongBits(this.beginTime) != Double.doubleToLongBits(other.beginTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.endTime) != Double.doubleToLongBits(other.endTime)) {
            return false;
        }
        if (this.nsamp != other.nsamp) {
            return false;
        }
        if (Double.doubleToLongBits(this.samprate) != Double.doubleToLongBits(other.samprate)) {
            return false;
        }
        if (this.foff != other.foff) {
            return false;
        }
        if (!Objects.equals(this.dataType, other.dataType)) {
            return false;
        }
        if (!Objects.equals(this.dir, other.dir)) {
            return false;
        }
        if (!Objects.equals(this.dfile, other.dfile)) {
            return false;
        }
        if (!Objects.equals(this.calib, other.calib)) {
            return false;
        }
        if (!Objects.equals(this.calper, other.calper)) {
            return false;
        }
        return true;
    }

}
