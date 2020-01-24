/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.continuous.segments;

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

    public double getAverageContiguousSegmentLength() {
        return meanLength.getResult();
    }

    @Override
    public String toString() {
        return String.format("%s has %d contiguous segments of average length %f s and total segment count of %d spanning %s",
                getName().toString(), getSize(), getAverageContiguousSegmentLength(), getTotalSegmentCount(), getEpoch().toString());
    }

    /**
     * Segments are assumed to be added in time order.
     *
     * @param segment
     */
    public void addSegment(Segment segment) {
        if (epochs.isEmpty()) {
            epochs.add(new ContiguousSegmentCollection());
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
    }

    /**
     * @return the totalSegmentCount
     */
    public int getTotalSegmentCount() {
        return totalSegmentCount;
    }
}
