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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.TimeT;
import edu.iris.dmc.timeseries.model.Segment;
import org.apache.commons.lang.ArrayUtils;



public class IntWaveform {

    private final long wfid;
    private final double start;
    private final double rate;
    private final int[] data;
    private final boolean empty;
    private static final double MAX_ALLOWABLE_DIFFERENCE = 0.005;

    public IntWaveform(long wfid,
            double start,
            double rate,
            int[] data) {
        this.wfid = wfid;
        this.start = start;
        this.rate = rate;
        this.data = Arrays.copyOf(data, data.length);
        empty = false;
    }

    public IntWaveform(long wfid, double rate) {
        this.wfid = wfid;
        this.rate = rate;
        start = -999999999;
        data = new int[0];
        empty = true;
    }

    public IntWaveform(IntWaveform other) {
        this.wfid = other.wfid;
        this.start = other.start;
        this.rate = other.rate;
        this.data = Arrays.copyOf(other.data, other.data.length);
        this.empty = other.empty;
    }

    /*
     * Constructs a IntWaveform from the data contained in an existing IRIS
     * Segment.  Note that the sample data will not be a copy of the Segment's
     * data, but shared between the two objects.  Use this constructor to avoid
     * memory overhead when the Segment is used only as a staging area for
     * getting the data from IRIS' database to an LLNL Waveform, and will
     * not be used after IntWaveform construction.  Preferably, the Segment should
     * go out of scope immediately after construction.
     */
    public IntWaveform(long wfid, Segment segment) {
        this.wfid = wfid;
        start = new TimeT(segment.getStartTime()).getEpochTime();
        rate = segment.getSamplerate();
        Integer[] dataAsIntObj = segment.getIntData().toArray(new Integer[0]);
        data = ArrayUtils.toPrimitive(dataAsIntObj, 0);
        empty = false;
    }

    public IntWaveform(double start, double rate, List<int[]> records) {
        this.wfid = -1;
        this.start = start;
        this.rate = rate;

        int sampleCount = 0;
        for (int[] record : records) sampleCount += record.length;
        data = new int[sampleCount];

        int j = 0;
        for (int[] record : records) {
            for (int sample : record) data[j++] = sample;
        }

        empty = false;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void write(String filename, double reftime) throws FileNotFoundException {
        try (PrintStream stream = new PrintStream(filename)) {
            for (int j = 0; j < data.length; ++j) {
                double t = (start - reftime) + j / rate;
                stream.printf("%f %d\n", t, data[j]);
            }
        }
    }

    public long getWfid() {
        return wfid;
    }

    public double getStart() {
        return start;
    }

    public double getRate() {
        return rate;
    }

    public int[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public String toString() {
        return String.format("Waveform{wfid = %d, start = %s, rate = %8.3f, length = %d samples}", wfid, (new TimeT(start)).toString(), rate, data.length);
    }

    public boolean rateIsComparable(final IntWaveform other) {
        double fileOneDelta = 1/rate;
        double fileTwoDelta = 1/other.rate;
        double percentDiff = 100 * Math.abs((fileOneDelta - fileTwoDelta) / fileOneDelta);
        return percentDiff < MAX_ALLOWABLE_DIFFERENCE;
    }

    public static boolean samplesAreComparable(int s1, int s2) {
        return s1 == s2 || s1 == 0 || s2 == 0;
    }

    public double getEnd() {
        if (data.length < 1) {
            return start;
        } else {
            return start + (data.length - 1) / rate;
        }
    }

    public Epoch getEpoch()
    {
        return new Epoch(start, getEnd());
    }

    public boolean isSubset(IntWaveform other) {
        return start >= other.start && getEnd() <= other.getEnd();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (this.wfid ^ (this.wfid >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.start) ^ (Double.doubleToLongBits(this.start) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.rate) ^ (Double.doubleToLongBits(this.rate) >>> 32));
        hash = 59 * hash + Arrays.hashCode(this.data);
        hash = 59 * hash + (this.empty ? 1 : 0);
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
        final IntWaveform other = (IntWaveform) obj;
        if (this.wfid != other.wfid) {
            return false;
        }
        if (Double.doubleToLongBits(this.start) != Double.doubleToLongBits(other.start)) {
            return false;
        }
        if (Double.doubleToLongBits(this.rate) != Double.doubleToLongBits(other.rate)) {
            return false;
        }
        if (this.empty != other.empty) {
            return false;
        }
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }


    public IntWaveform getNewStartCopy(double newStart) {
        if (newStart < start) {
            int numNewPoints = (int) Math.round((start - newStart) * rate);
            if (numNewPoints >= 1) {
                int[] newData = new int[data.length + numNewPoints];
                Arrays.fill(newData, 0);
                System.arraycopy(data, 0, newData, numNewPoints, data.length);
                double adjustedTime = start - numNewPoints / rate;
                return new IntWaveform(wfid, adjustedTime, rate, newData);
            } else {
                return this;
            }
        } else if (newStart == start) {
            return this;
        } else {
            int numToRemove = (int) Math.round((newStart - start) * rate);
            if (numToRemove >= 1) {
                int[] newData = new int[data.length - numToRemove];
                System.arraycopy(data, numToRemove, newData, numToRemove - numToRemove, data.length - numToRemove);
                double adjustedTime = start + numToRemove / rate;
                return new IntWaveform(wfid, adjustedTime, rate, newData);
            } else {
                return this;
            }
        }
    }

    public IntWaveform getNewEndCopy(double requestedEnd) {
        double thisEnd = getEnd();
        if (requestedEnd > thisEnd) {
            int numNewPoints = (int) Math.round((requestedEnd - thisEnd) * rate);
            int[] newData = new int[data.length + numNewPoints];
            Arrays.fill(newData, 0);
            System.arraycopy(data, 0, newData, 0, data.length);
            return new IntWaveform(wfid, start, rate, newData);
        } else if (requestedEnd < thisEnd) {
            int numToRemove = (int) Math.round((thisEnd - requestedEnd) * rate);
            int[] newData = new int[data.length - numToRemove];
            System.arraycopy(data, 0, newData, 0, newData.length);
            return new IntWaveform(wfid, start, rate, newData);
        }
        return this;
    }
    
    
    
    public static IntWaveform unionOf(IntWaveform ts1In, IntWaveform ts2In){
        if( !ts1In.rateIsComparable(ts2In)){
            throw new IllegalStateException(String.format("Rate mismatch! rate 1 = %f, rate 2 = %f", ts1In.getRate(), ts2In.getRate()));
        }
        double s1 = ts1In.getStart();
        double s2 = ts2In.getStart();
        IntWaveform ts1 = ts1In;
        IntWaveform ts2 = ts2In;
        if( s2 < s1 ){
            ts1 = ts2In;
            ts2 = ts1In;
        }
        int offset = (int) Math.round((ts2.getStart() - ts1.getStart()) * ts1.getRate());
        int npts = Math.max(offset + ts2.getNpts(), ts1.getNpts());
        int[] newData = new int[npts];
        System.arraycopy(ts1.getData(), 0, newData, 0, ts1.getNpts());
        
        System.arraycopy(ts2.getData(), 0, newData, offset, ts2.getNpts());
        return new IntWaveform(-1, ts1.getStart(), ts1.getRate(), newData); 
    }
    

    public IntWaveform union(IntWaveform other, boolean ignoreMismatch) throws MergeException {

        if (other.isEmpty()) {
            if (this.isEmpty()) {
                return new IntWaveform(-1, -999);  // Empty IntWaveform

            } else {
                return this;
            }
        } else if (this.isEmpty()) {
            return other;
        }
        else {
            double minStart = Math.min(start, other.start);
            double maxEnd = Math.max(getEnd(), other.getEnd());
            int npts = (int) Math.round((maxEnd - minStart) * rate) + 1;
            try {
                int[] shifts = {0, -1, 1}; // Ideally only the zero-shift is required, but in case of one-off error, try forward and backward shifts
                if (start == minStart) {
                    for (int shift : shifts) {
                        int mpts = npts + shift;
                        int[] newData = new int[mpts];
                        int destOffset = (int) Math.round((other.start - minStart) * other.rate) + shift;
                        if (copyToResultArray(destOffset, other.data, data, newData, ignoreMismatch)) {
                            return new IntWaveform(wfid, minStart, rate, newData);
                        }
                    }
                } else {
                    for (int shift : shifts) {
                        int mpts = npts + shift;
                        int[] newData = new int[mpts];
                        int destOffset = (int) Math.round((start - minStart) * rate) + shift;
                        if (copyToResultArray(destOffset, data, other.data, newData, ignoreMismatch)) {
                            return new IntWaveform(wfid, minStart, rate, newData);
                        }
                    }
                }
            } catch (Exception e) {
                throw new MergeException(e.getMessage());
            }

            throw new MergeException("Could not merge segments using shifts of (-1, 0, 1)");
        }
    }

    public IntWaveform trim() {
        int startIndex = getTrimmedStartIndex();
        int end = getTrimmedEndIndex();
        if (end > startIndex) {
            if (startIndex == 0 && end == data.length - 1) {
                return this;
            } else {
                double newStart = start + startIndex / rate;
                int[] newData = new int[end - startIndex + 1];
                System.arraycopy(data, startIndex, newData, 0, newData.length);
                return new IntWaveform(wfid, newStart, rate, newData);
            }
        } else {
            return new IntWaveform(wfid, rate);    // Empty IntWaveform
        }
    }

    protected int getTrimmedEndIndex() {
        int j;
        j = data.length - 1;
        while (j >= 0) {
            if (data[j] != 0) {
                break;
            }
            --j;
        }
        return j;
    }

    protected int getTrimmedStartIndex() {
        int j = 0;

        while (j < data.length) {
            if (data[j] != 0) {
                break;
            }
            ++j;
        }
        return j;
    }

    protected static boolean copyToResultArray(int destOffset, int[] data1, int[] data2, int[] newData, boolean ignoreMismatch) {
        try {
            // First copy samples from the earlier-starting trace to the destination trace. Nothing to merge here.
            int maxIdx = Math.min(destOffset, data2.length);
            System.arraycopy(data2, 0, newData, 0, maxIdx);

            // Now merge the overlapped portions...
            int maxMergeIndexBound = Math.min(destOffset + data1.length, data2.length);
            for (int j = destOffset; j < maxMergeIndexBound; ++j) {
                newData[j] = mergeSamples(data2[j], data1[j - destOffset], ignoreMismatch);
            }


            // Copy remaining samples from the input arrays (whichever has samples after their common samples)
            if (maxMergeIndexBound < data2.length) {
                System.arraycopy(data2, maxMergeIndexBound, newData, maxMergeIndexBound, data2.length - maxMergeIndexBound);
            } else if (maxMergeIndexBound < data1.length + destOffset) {
                for (int j = maxMergeIndexBound; j < data1.length + destOffset; ++j) {
                    int idx = j - destOffset;
                    if (idx >= 0 && idx < data1.length && j < newData.length) {
                        newData[j] = data1[idx];
                    }
                }
            }
            return true;
        } catch (MergeException e) {
            return false;
        }

    }

    private static int mergeSamples(int value1, int value2, boolean ignoreMismatch) throws MergeException {
        if (value1 == value2) {
            return value1;
        } else if (value1 == 0) {
            return value2;
        } else if (value2 == 0) {
            return value1;
        } else {
            if (ignoreMismatch) {
                return (value1 + value2) / 2;
            } else {
                throw new MergeException(String.format("Overlapped data have different values (%d vs %d)!", value1, value2));
            }
        }
    }

    public int getNpts() {
        return data.length;
    }

    public int getJdate() {
        return new TimeT(start).getJdate();
    }

    public IntWaveform getSubset(Epoch epoch) {
        Epoch thisEpoch = new Epoch(start, getEnd());
        if (!thisEpoch.isSuperset(epoch)) {
            throw new IllegalArgumentException("Requested epoch is not subset of this waveform epoch!");
        }

        int startOffset = (int) Math.round((epoch.getTime().getEpochTime() - start) * rate);
        int endOffset = (int) Math.round((epoch.getEndtime().getEpochTime() - start) * rate);
        int npts = endOffset - startOffset;
        int[] data2 = new int[npts];
        System.arraycopy(data, startOffset, data2, 0, npts);
        double actualTime = startOffset / rate + start;
        return new IntWaveform(wfid, actualTime, rate, data2);
    }

    public void removeMean() {
        double mean = 0;
        for (int v : data) {
            mean += v;
        }
        mean /= data.length;
        for (int j = 0; j < data.length; ++j) {
            data[j] = (int) (data[j] - mean);
        }
    }

    void scaleBy(double value) {
        for (int j = 0; j < data.length; ++j) {
            data[j] = (int)(data[j] * value);
        }
    }
}
