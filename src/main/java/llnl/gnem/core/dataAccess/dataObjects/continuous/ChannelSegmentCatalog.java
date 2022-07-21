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
package llnl.gnem.core.dataAccess.dataObjects.continuous;


import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author dodge
 */
public class ChannelSegmentCatalog {

    private final StreamKey name;
    private  double sampleRate;
    private final Mean meanLength;
    private final ArrayList<ContiguousSegmentCollection> epochs;
    private int totalSegmentCount;

    public ChannelSegmentCatalog(StreamKey name) {
        this.name = name;
        epochs = new ArrayList<>();
        meanLength = new Mean();
        totalSegmentCount = 0;
    }

    public double getStart() {
        if (!epochs.isEmpty()) {
            return epochs.get(0).getStart();
        } else {
            return Double.MAX_VALUE;
        }
    }

    public double getEnd() {
        if (!epochs.isEmpty()) {
            return epochs.get(epochs.size() - 1).getEnd();
        } else {
            return -Double.MAX_VALUE;
        }
    }
    
    public Epoch getEpoch()
    {
        return new Epoch(getStart(), getEnd());
    }

    public ArrayList<ContiguousSegmentCollection> getContiguousSegments() {
        return new ArrayList<>(epochs);
    }

    public int getSize() {
        return epochs.size();
    }
    
    public Collection<DataGap> getGaps()
    {
        Collection<DataGap> result = new ArrayList<>();
        for( int j = 1; j < epochs.size(); ++j ){
            ContiguousSegmentCollection last = epochs.get(j-1);
            ContiguousSegmentCollection current = epochs.get(j);
            result.add(new DataGap(last.getEndWfid(), current.getStartWfid(), last.getEnd(), current.getStart()));
        }
        return result;
    }

    /**
     * @return the name
     */
    public StreamKey getName() {
        return name;
    }


    @Override
    public String toString() {
        return String.format("%s (sample rate = %f) has %d contiguous segments of total segment count of %d spanning %s",
                getName().toString(), getSampleRate(), getSize(),  getTotalSegmentCount(), getEpoch().toString());
    }

    /**
     * Segments are assumed to be added in time order.
     *
     * @param segment
     */
    public void addSegment(Segment segment) {
        if (epochs.isEmpty()) {
            epochs.add(new ContiguousSegmentCollection());
            totalSegmentCount = 1;
        }
        int idx = epochs.size() - 1;
        ContiguousSegmentCollection csc = epochs.get(idx);
        if (csc.canAccept(segment)) {
            csc.add(segment);
        } else {
            meanLength.increment(csc.getEpoch().duration());
            totalSegmentCount += csc.getSize();
            ContiguousSegmentCollection newCsc = new ContiguousSegmentCollection();
            newCsc.add(segment);
            epochs.add(newCsc);
        }
        sampleRate = Math.round(segment.getRate());
    }

    /**
     * @return the totalSegmentCount
     */
    public int getTotalSegmentCount() {
        return totalSegmentCount;
    }

    public double getSampleRate() {
        return sampleRate;
    }
    
}
