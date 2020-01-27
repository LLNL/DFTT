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

import java.io.Serializable;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.Wfdisc;
import llnl.gnem.core.waveform.io.BinaryData;

/*
 * COPYRIGHT NOTICE RBAP Version 1.0 Copyright (C) 2002 Lawrence Livermore
 * National Laboratory.
 */
/**
 * A class that encapsulates essential attributes of a CssSeismogram including
 * the time-series data.
 *
 * @author Doug Dodge
 */
public class CssSeismogram extends BasicSeismogram implements Serializable {

    private final Double calib;
    private final Double calper;
    private static final long serialVersionUID = 2137920519754562968L;

    /**
     * No-arg constructor only used for serialization.
     */
    public CssSeismogram() {
        super();
        calib = null;
        calper = null;
    }

    public CssSeismogram(long wfid, String sta, String chan, float[] data, double samprate, TimeT time) {
        this(wfid, sta, chan, data, samprate, time, null, null);
    }

    public CssSeismogram(Wfdisc metadata, TimeSeries series) {
        this(metadata.getWfid(), metadata.getStaChan(), series.getData(), series.getSamprate(), series.getTime(), metadata.getCalib(), metadata.getCalper());
    }

    public CssSeismogram(Wfdisc metadata, float[] data) {
        this(metadata.getWfid(), metadata.getStaChan(), data, metadata.getSamprate(), new TimeT(metadata.getTime()), metadata.getCalib(), metadata.getCalper());
    }

    public CssSeismogram(Wfdisc metadata, BinaryData data) {
        super(metadata.getWfid(), metadata.getStaChan(), data, metadata.getSamprate(), new TimeT(metadata.getTime()));
        this.calib = metadata.getCalib();
        this.calper = metadata.getCalper();
    }

    public CssSeismogram(long wfid, String net, String sta,
            String chan, String locid, float[] data, double samprate, TimeT time, Double calib, Double calper) {
        this(wfid, new StreamKey(net, sta, chan, locid), data, samprate, time, calib, calper);
    }

    /**
     * Constructor for the CssSeismogram object
     *
     * @param wfid The waveform ID for these data.
     * @param sta The station name
     * @param chan The channel name
     * @param data The data points of the time-series
     * @param samprate The sample rate of the data.
     * @param time The start time of the time-series as an epoch time.
     * @param calib Factor to convert from digital counts to ground motion units
     * @param calper Period at which the calib is valid.
     */
    public CssSeismogram(long wfid, String sta, String chan, float[] data, double samprate, TimeT time, Double calib, Double calper) {
        this(wfid, new StreamKey(sta, chan), data, samprate, time, calib, calper);
    }

    public CssSeismogram(long wfid, StreamKey stachan, float[] data, double samprate, TimeT time, Double calib, Double calper) {
        super(wfid, stachan, data, samprate, time);
        this.calib = calib;
        this.calper = calper;
    }

    /**
     * Copy Constructor for the CssSeismogram object
     *
     * @param s The CssSeismogram to be copied
     */
    public CssSeismogram(CssSeismogram s) {
        super(s);
        this.calib = s.calib;
        this.calper = s.calper;
    }

    public CssSeismogram(BasicSeismogram s, Double calib, Double calper) {
        super(s);
        this.calib = calib;
        this.calper = calper;
    }

    public CssSeismogram(CssSeismogram s, TimeSeries data) {
        super(s, data);
        this.calib = s.calib;
        this.calper = s.calper;
    }

    /**
     * Gets the calib attribute of the CssSeismogram object
     *
     * @return The calib value
     */
    public Double getCalib() {
        return calib;
    }

    /**
     * Gets the calper attribute of the CssSeismogram object
     *
     * @return The calper value
     */
    public Double getCalper() {
        return calper;
    }

    /**
     * Produce a String representation of the CssSeismogram (does not list the
     * data points)
     *
     * @return The String description of the seismogram.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append(", Calib = ");
        s.append(calib);
        s.append(", Calper = ");
        s.append(calper);
        return s.toString();
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
    public int compareTo(CssSeismogram other) {

        return compareTo((TimeSeries) other);
    }

    /* (non-Javadoc)
     * @see llnl.gnem.core.waveform.BasicSeismogram#compareTo(llnl.gnem.core.waveform.TimeSeries)
     * Note this overrides the default comparator so that Maps and Sorts behave as you would
     * expect when using this class.
     */
    @Override
    public int compareTo(TimeSeries other) {
        if (this == other) {
            return 0;
        }

        int rc = super.compareTo(other);
        if (rc == 0) {

            if (other instanceof CssSeismogram) {

                CssSeismogram css = (CssSeismogram) other;

                if (Math.abs(calib - css.calib) > EPSILON) {
                    return calib > css.calib ? 1 : -1;
                }
                if (Math.abs(calper - css.calper) > EPSILON) {
                    return calper > css.calper ? 1 : -1;
                }
            }
        }

        return rc;
    }

    public static CssSeismogram unionOf(CssSeismogram seis1, CssSeismogram seis2) {
        if (!java.util.Objects.equals(seis1.calib, seis2.calib)) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Seismograms being unioned have unequal CALIB values!");
        }
        if (!java.util.Objects.equals(seis1.calper, seis2.calper)) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Seismograms being unioned have unequal CALPER values!");
        }
        BasicSeismogram tmp = BasicSeismogram.unionOf(seis1, seis2);
        return new CssSeismogram(tmp, seis1.calib, seis1.calper);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + java.util.Objects.hashCode(this.calib);
        hash = 29 * hash + java.util.Objects.hashCode(this.calper);
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
        final CssSeismogram other = (CssSeismogram) obj;
        if (!java.util.Objects.equals(this.calib, other.calib)) {
            return false;
        }
        if (!java.util.Objects.equals(this.calper, other.calper)) {
            return false;
        }
        return true;
    }

    @Override
    public CssSeismogram crop(int start, int end) {
        TimeSeries tmp = super.crop(start, end);
        BasicSeismogram bs = new BasicSeismogram(this.getWaveformID(), this.getStreamKey(), tmp);
        return new CssSeismogram(bs, this.getCalib(), this.getCalper());
    }

}
