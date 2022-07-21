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
package llnl.gnem.apps.detection.core.dataObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.waveform.qc.DataDefect;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.merge.NamedIntWaveform;
import llnl.gnem.core.waveform.seismogram.BasicSeismogram;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class WaveformSegment extends BasicSeismogram {

    private static final long serialVersionUID = 2759974382277736377L;

    private static CompatibilityStrictness strictness = CompatibilityStrictness.STRICT;
    private final Set<DataDefect> defects;

    /**
     * @return the defects
     */
    public Set<DataDefect> getDefects() {
        return new HashSet<>(defects);
    }

    public void appendToDefectCollection(Collection<? extends DataDefect> additionalDefects) {
        defects.addAll(additionalDefects);
    }

    /**
     * @return the strictness
     */
    public static CompatibilityStrictness getStrictness() {
        return strictness;
    }

    /**
     * @param aStrictness the strictness to set
     */
    public static void setStrictness(CompatibilityStrictness aStrictness) {
        strictness = aStrictness;
    }

    public WaveformSegment(NamedIntWaveform waveform, double commonRate) {
        super(null,
                waveform.getKey(),
                waveform.getDataAsFloatArray(),
                waveform.getRate(),
                new TimeT(waveform.getStart()));
        defects = new HashSet<>(waveform.getDefects());
    }

    boolean hasDefects() {
        return !defects.isEmpty();
    }

    public static enum CompatibilityStrictness {
        STRICT, ONLY_MATCH_CODES
    }

    public WaveformSegment(String sta,
            String chan,
            double start,
            double rate,
            float[] inData) {
        super(null, sta, chan, inData, rate, new TimeT(start));
        defects = new HashSet<>();
    }

    public WaveformSegment(String sta,
            String chan,
            double start,
            double rate,
            float[] inData,
            List<DataDefect> defects) {
        super(null, sta, chan, inData, rate, new TimeT(start));
        this.defects = new HashSet<>(defects);
    }


    public WaveformSegment(StreamKey key,
            double start,
            double rate,
            float[] inData,
            List<DataDefect> defects) {
        super(null, key, inData, rate, new TimeT(start));
        this.defects = new HashSet<>(defects);
    }
    
    
    public WaveformSegment(BasicSeismogram seis) {
        super(seis);
        defects = new HashSet<>();
    }

    public WaveformSegment(BasicSeismogram seis, Collection<DataDefect> defects) {
        super(seis);
        this.defects = new HashSet<>(defects);
    }

    public TimeT getStartTime() {
        return new TimeT(this.getTimeAsDouble());
    }

    public String getStation() {
        return this.getSta();
    }

    public String getChannel() {
        return this.getChan();
    }

    public WaveformSegment getSubset(Epoch epoch) {
        int blockSize = (int) Math.round(epoch.duration() * getSamprate()) + 1;
        return getSubset(epoch, blockSize);
    }

    public WaveformSegment getSubset(Epoch epoch, int blockSize) {
        Epoch thisEpoch = getEpoch();
        if (!thisEpoch.isSuperset(epoch)) {
            // This may be a rounding error of less than 1/2 sample in which case it is benign.
            if (epoch.getStart() < thisEpoch.getStart()) {
                double preStartSeconds = thisEpoch.getStart() - epoch.getStart();
                double preStartSamples = preStartSeconds * getSamprate();
                if (preStartSamples > .5) {
                    String msg = String.format("Requested epoch starts %f samples before waveform start time!", preStartSamples);
                    throw new IllegalStateException(msg);
                }
            } else if (epoch.getEnd() > thisEpoch.getEnd()) {
                double postEndSeconds = epoch.getEnd() - thisEpoch.getEnd();
                double postEndSamples = postEndSeconds * getSamprate();
                if (postEndSamples > .5) {
                    String msg = String.format("Requested epoch ends %f samples after waveform end time!", postEndSamples);
                    throw new IllegalStateException(msg);
                }
            }
        }
        List<DataDefect> tmp = this.defects.stream().filter(d -> d.getEpoch().intersects(epoch)).collect(Collectors.toList());

        int startOffset = (int) Math.round((epoch.getTime().getEpochTime() - getTimeAsDouble()) * getSamprate());
        int endOffset = (int) Math.round((epoch.getEndtime().getEpochTime() - getTimeAsDouble()) * getSamprate());
        int npts = endOffset - startOffset + 1;
        if (npts > blockSize) {
            npts = blockSize;
            ApplicationLogger.getInstance().log(Level.FINEST, "Calculated npts was too large and had to be reduced to block size!");
        }
        float[] data2 = new float[blockSize];
        System.arraycopy(getData(), startOffset, data2, 0, npts);
        return new WaveformSegment(this.getStreamKey(), epoch.getTime().getEpochTime(), getSamprate(), data2, tmp);
    }

    public boolean includes(Epoch epoch) {
        Epoch thisEpoch = new Epoch(getTimeAsDouble(), getEndtimeAsDouble());
        return thisEpoch.isSuperset(epoch);
    }

    public WaveformSegment append(WaveformSegment appendee) {
        verifyCompatibility(appendee);
        Collection<DataDefect> tmpDefects = new ArrayList<>(defects);
        tmpDefects.addAll(appendee.defects);
        BasicSeismogram tmp = BasicSeismogram.unionOf(this, appendee);
        List<DataDefect> allDefects = tmpDefects.stream().filter(d -> d.getEpoch().intersects(tmp.getEpoch())).collect(Collectors.toList());
        return new WaveformSegment(tmp, allDefects);
    }

    public WaveformSegment trimFrontAndAppend(WaveformSegment appendee, double blockStart) {
        WaveformSegment tmp = append(appendee);
        Epoch newEpoch = new Epoch(blockStart, tmp.getEndtimeAsDouble());
        return tmp.getSubset(newEpoch);
    }

    private void verifyCompatibility(WaveformSegment that) {
        if (!this.getStreamKey().equals(that.getStreamKey())) {
            throw new IllegalStateException(String.format("Attempt to append segment for different sta-chan ( %s vs %s )", this.getStreamKey(), that.getStreamKey()));
        }
    }

    @Override
    public WaveformSegment crop(int start, int end) {
        BasicSeismogram tmp = super.crop(start, end);
        List<DataDefect> allDefects = defects.stream().filter(d -> d.getEpoch().intersects(tmp.getEpoch())).collect(Collectors.toList());
        return new WaveformSegment(tmp, allDefects);
    }

    public WaveformSegment trimToLength(int minNpts) {
        return crop(0, minNpts - 1);
    }

}
