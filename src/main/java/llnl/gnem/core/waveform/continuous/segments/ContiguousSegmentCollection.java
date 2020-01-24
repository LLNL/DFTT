/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.continuous.segments;

import java.util.ArrayList;
import llnl.gnem.core.util.Epoch;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author dodge
 */
public class ContiguousSegmentCollection {

    private final ArrayList<Segment> segments;
    private final StandardDeviation intervalDeviation;
    private final Mean intervalMean;
    private final Mean lengthMean;

    public ContiguousSegmentCollection() {
        segments = new ArrayList<>();
        intervalDeviation = new StandardDeviation();
        intervalMean = new Mean();
        lengthMean = new Mean();
    }

    @Override
    public String toString() {
        return String.format("%d segments spanning (%s) with mean delta = %f and std delta = %f and average length = %f s.",
                segments.size(), getEpoch(), getAverageDelta(), getSampleIntervalDeviation(), getAverageLength());
    }

    public ArrayList<Segment> getSegments() {
        return new ArrayList<>(segments);
    }

    public int getSize() {
        return segments.size();
    }

    public int getEndWfid() {
        if (segments.isEmpty()) {
            return -1;
        } else {
            return segments.get(segments.size() - 1).getWfid();
        }
    }

    public int getStartWfid() {
        if (segments.isEmpty()) {
            return -1;
        } else {
            return segments.get(0).getWfid();
        }
    }

    public void add(Segment segment) {
        segments.add(segment);
        double interval = 1.0 / segment.getRate();
        intervalDeviation.increment(interval);
        intervalMean.increment(interval);
        lengthMean.increment(segment.getLength());
    }

    boolean canAccept(Segment segment) {
        if (segments.isEmpty()) {
            return true;
        } else {
            Segment last = segments.get(segments.size() - 1);
            double sigmaInterval = intervalDeviation.getResult();
            double meanInterval = intervalMean.getResult();
            double lastTime = last.getEnd() + meanInterval + sigmaInterval;
            return segment.getStart() <= lastTime;
        }
    }

    public Epoch getEpoch() {
        return new Epoch(getStart(), getEnd());
    }

    public double getStart() {
        if (!segments.isEmpty()) {
            return segments.get(0).getStart();
        } else {
            return Double.MAX_VALUE;
        }
    }

    public double getEnd() {
        if (!segments.isEmpty()) {
            return segments.get(segments.size() - 1).getEnd();
        } else {
            return -Double.MAX_VALUE;
        }
    }

    public double getAverageDelta() {
        return intervalMean.getResult();
    }

    public double getSampleIntervalDeviation() {
        return intervalDeviation.getResult();
    }

    public double getAverageLength() {
        return lengthMean.getResult();
    }

}
