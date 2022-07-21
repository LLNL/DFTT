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

import llnl.gnem.apps.detection.util.TimeStamp;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.BasicSeismogram;

public class DecimatedStreamSegment {

    private final ArrayList< float[]> waveforms;
    private TimeStamp startTime;
    private final int nsamples;
    private final int nch;
    private final double decimatedSampleInterval;
    private final ArrayList< StreamKey> chanids;

    public DecimatedStreamSegment(int nch, int nsamples, double decimatedSampleInterval, ArrayList< StreamKey> chanids) {
        this.nch = nch;
        this.nsamples = nsamples;
        waveforms = new ArrayList<>();
        for (int ich = 0; ich < nch; ich++) {
            waveforms.add(new float[nsamples]);
        }
        this.decimatedSampleInterval = decimatedSampleInterval;
        startTime = new TimeStamp(0.0);
        this.chanids = new ArrayList<>(chanids);
    }

    // constructor for a DecimatedStreamSegment that combines two DecimatedStreamSegments
    public DecimatedStreamSegment(DecimatedStreamSegment seg0, DecimatedStreamSegment seg1) {

        if (seg0.nch != seg1.nch) {
            throw new IllegalStateException("Decimated segments to be merged do not have the same number of channels");
        }
        if (seg0.decimatedSampleInterval != seg1.decimatedSampleInterval) {
            throw new IllegalStateException("Decimated segments to be merged do not have identical sampling intervals");
        }

        nch = seg0.nch;
        nsamples = seg0.nsamples + seg1.nsamples;
        decimatedSampleInterval = seg0.decimatedSampleInterval;
        startTime = new TimeStamp(seg0.startTime);
        this.chanids = new ArrayList<>(seg0.chanids);

        waveforms = new ArrayList<>();

        for (int ich = 0; ich < nch; ich++) {

            float[] w0 = seg0.waveforms.get(ich);
            float[] w1 = seg1.waveforms.get(ich);
            float[] tmp = new float[nsamples];

            System.arraycopy(w0, 0, tmp, 0, seg0.nsamples);
            System.arraycopy(w1, 0, tmp, seg0.nsamples, seg1.nsamples);
            waveforms.add(tmp);
        }

    }

    // copy method intended for BAA2011 version of PreProcessor
    //   contents of this DecimatedStreamSegment1 are copied from another without reallocation of waveform arrays
    public void copy(DecimatedStreamSegment DSS) {

        // check compatibility
        if (this.nch != DSS.nch) {
            throw new IllegalArgumentException("Mismatch between number of channels in DecimatedStreamSegment instances");
        }
        if (this.nsamples != DSS.nsamples) {
            throw new IllegalArgumentException("Mismatch between number of samples in DecimatedStreamSegment instances");
        }
        if (this.decimatedSampleInterval != DSS.decimatedSampleInterval) {
            throw new IllegalArgumentException("Mismatch between sample intervals in DecimatedStreamSegment instances");
        }

        this.startTime = new TimeStamp(DSS.startTime);
        // copy waveforms
        for (int ich = 0; ich < this.nch; ich++) {
            float[] w = waveforms.get(ich);
            float[] DSSw = DSS.waveforms.get(ich);
            System.arraycopy(DSSw, 0, w, 0, nsamples);
        }

    }

    public String getSta(int channel) {
        return chanids.get(channel).getSta();
    }

    public String getChan(int channel) {
        return chanids.get(channel).getChan();
    }

    public float[] getChannelData(int channel) {
        return waveforms.get(channel);
    }

    public float[] getChannelData(StreamKey key) {
        for (int j = 0; j < chanids.size(); ++j) {
            if (chanids.get(j).equals(key)) {
                return getChannelData(j);
            }
        }
        throw new IllegalArgumentException(String.format("Channel key (%s) not found!", key));
    }

    public TimeStamp getStartTime() {
        return new TimeStamp(startTime);
    }

    public void setStartTime(TimeStamp T) {
        startTime = new TimeStamp(T);
    }

    public int getNumChannels() {
        return nch;
    }

    public int size() {
        return nsamples;
    }

    public double getDecimatedSampleInterval() {
        return decimatedSampleInterval;
    }

    public Collection<BasicSeismogram> getSeismograms() {
        Collection<BasicSeismogram> result = new ArrayList<>();
        int nchan = getNumChannels();
        for (int j = 0; j < nchan; ++j) {
            float[] data = getChannelData(j);
            double rate = 1.0 / getDecimatedSampleInterval();

            float[] tmpData = new float[data.length];
            System.arraycopy(data, 0, tmpData, 0, data.length);
            result.add(new BasicSeismogram(null, getSta(j),
                    getChan(j), tmpData, rate, new TimeT(getStartTime().epochAsDouble())));
        }
        return result;
    }

}
