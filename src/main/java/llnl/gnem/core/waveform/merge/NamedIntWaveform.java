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
package llnl.gnem.core.waveform.merge;

import edu.iris.dmc.timeseries.model.Segment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import llnl.gnem.core.metadata.Channel;
import llnl.gnem.core.waveform.qc.DataDefect;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: Jan 11, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class NamedIntWaveform extends IntWaveform {

    private final StreamKey key;
    private final double calib;
    private final double calper;
    private final String clip;
    private final String segtype;
    private final String instype;
    private final ArrayList<DataDefect> defects;

    public NamedIntWaveform(long wfid,
            String sta,
            String chan,
            double start,
            double rate,
            int[] data) {
        super(wfid, start, rate, data);
        key = new StreamKey(sta, chan);
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        defects = new ArrayList<>();
    }

    public NamedIntWaveform(long wfid,
            String sta,
            String chan,
            double start,
            double rate,
            int[] data,
            Collection<DataDefect> defects) {
        super(wfid, start, rate, data);
        key = new StreamKey(sta, chan);
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        this.defects = new ArrayList<>(defects);
    }

    public NamedIntWaveform(long wfid,
            String sta,
            String chan,
            double start,
            double rate,
            int[] data,
            double calib,
            double calper,
            String clip,
            String segtype,
            String instype) {
        super(wfid, start, rate, data);
        key = new StreamKey(sta, chan);
        this.calib = calib;
        this.calper = calper;
        this.clip = clip;
        this.segtype = segtype;
        this.instype = instype;
        defects = new ArrayList<>();
    }

    public NamedIntWaveform(IntWaveform waveform, String sta, String chan) {
        super(waveform);
        key = new StreamKey(sta, chan);
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        defects = new ArrayList<>();
    }
    
    private NamedIntWaveform(IntWaveform waveform, StreamKey aKey, List<DataDefect> defects) {
        super(waveform);
        key = aKey;
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        this.defects = new ArrayList<>(defects);
    }

    private NamedIntWaveform(IntWaveform waveform, String sta, String chan, List<DataDefect> defects) {
        super(waveform);
        key = new StreamKey(sta, chan);
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        this.defects = new ArrayList<>(defects);
    }

    public NamedIntWaveform(IntWaveform waveform,
            StreamKey aKey,
            double calib,
            double calper,
            String clip,
            String segtype,
            String instype,
            Collection<DataDefect> defects) {
        super(waveform);
        this.key = aKey;
        this.calib = calib;
        this.calper = calper;
        this.clip = clip;
        this.segtype = segtype;
        this.instype = instype;
        this.defects = defects != null ? new ArrayList<>(defects) : new ArrayList<>();
    }

    public NamedIntWaveform(IntWaveform waveform,
            String sta,
            String chan,
            double calib,
            double calper,
            String clip,
            String segtype,
            String instype,
            Collection<DataDefect> defects) {
        super(waveform);
        key = new StreamKey(sta, chan);
        this.calib = calib;
        this.calper = calper;
        this.clip = clip;
        this.segtype = segtype;
        this.instype = instype;
        this.defects = defects != null ? new ArrayList<>(defects) : new ArrayList<>();
    }

    /*
     * Constructs a NamedIntWaveform from the data contained in an existing IRIS
     * Segment.  Note that the sample data will not be a copy of the Segment's
     * data, but shared between the two objects.  Use this constructor to avoid
     * memory overhead when the Segment is used only as a staging area for
     * getting the data from IRIS' database to an LLNL NamedIntWaveform, and will
     * not be used after NamedIntWaveform construction.  Preferably, the Segment
     * should go out of scope immediately after construction.
     */
    public NamedIntWaveform(long wfid, String sta, String chan, Segment segment) {
        super(wfid, segment);
        key = new StreamKey(sta, chan);
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        defects = new ArrayList<>();
    }
    
    public NamedIntWaveform(StreamKey key, Segment segment){
        super(-1,segment);
        this.key = key;
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        defects = new ArrayList<>();
    }

    public NamedIntWaveform(String sta, String chan, double start, double rate,
            List<int[]> records) {
        super(start, rate, records);
        key = new StreamKey(sta, chan);
        calib = 1.0;
        calper = -1.0;
        clip = "-";
        segtype = "-";
        instype = "-";
        defects = new ArrayList<>();
    }
    
    public StreamKey getKey()
    {
        return key;
    }

    public String getSta() {
        return key.getSta();
    }

    public String getChan() {
        return key.getChan();
    }

    public Channel getChannel() {
        return new Channel(key.getChan());
    }

    public double getCalib() {
        return calib;
    }

    public double getCalper() {
        return calper;
    }

    public String getClip() {
        return clip;
    }

    public String getSegtype() {
        return segtype;
    }

    public String getInstype() {
        return instype;
    }

    @Override
    public String toString() {
        return String.format("Waveform{(%s) wfid = %d, start = %s, rate = %8.3f, length = %d samples}", key,
                getWfid(), (new TimeT(getStart())).toString(), getRate(), getNpts());
    }

    public boolean includes(Epoch epoch) {
        return this.getEpoch().isSuperset(epoch);
    }

    @Override
    public NamedIntWaveform getSubset(Epoch epoch) {
        IntWaveform waveform = super.getSubset(epoch);

        List<DataDefect> tmp = defects.
                stream().
                filter(d -> d.getEpoch().intersects(waveform.getEpoch())).
                collect(Collectors.toList());
        return new NamedIntWaveform(waveform, key, tmp);
    }

    public float[] getDataAsFloatArray() {
        float[] result = new float[this.getNpts()];
        int[] data = this.getData();
        for (int j = 0; j < data.length; ++j) {
            result[j] = data[j];
        }
        return result;
    }
    
    public static NamedIntWaveform combine( NamedIntWaveform wv1, NamedIntWaveform wv2){
        if (!wv1.key.equals(wv2.key)) {
            throw new IllegalArgumentException(String.format("Waveform 1 = %s but other waveform = %s!", wv1.key, wv2.key));
        }
        if ( wv1.calib!=wv2.calib) {
            throw new IllegalArgumentException(String.format("Waveform 1 calib = %f but other waveform  calib = %f!", wv1.calib, wv2.calib));
        }
        if ( wv1.calper!=wv2.calper) {
            throw new IllegalArgumentException(String.format("Waveform 1 calper = %f but other waveform  calper = %f!", wv1.calper, wv2.calper));
        }
        IntWaveform result =  unionOf(wv1,wv2);
        Set<DataDefect> tmp = new HashSet<>(wv1.defects);
        tmp.addAll(wv2.getDefects());
        return new NamedIntWaveform(result,
                wv1.key,
                wv1.calib,
                wv1.calper,
                wv1.clip,
                wv1.segtype,
                wv1.instype,
                tmp);
    }

    public NamedIntWaveform union(NamedIntWaveform other, boolean ignoreMismatch) throws MergeException {
        if (!this.key.equals(other.key)) {
            throw new IllegalArgumentException(String.format("This waveform = %s but other waveform = %s!", key, other.key));
        }

        IntWaveform result = super.union(other, ignoreMismatch);
        Set<DataDefect> tmp = new HashSet<>(this.defects);
        tmp.addAll(other.getDefects());
        return new NamedIntWaveform(result,
                this.key,
                calib,
                calper,
                clip,
                segtype,
                instype,
                tmp);

    }

    public void scaleByCalib() {
        super.scaleBy(calib);
    }

    /**
     * @return the defects
     */
    public ArrayList<DataDefect> getDefects() {
        return new ArrayList<>(defects);
    }

}
