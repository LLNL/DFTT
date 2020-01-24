/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.continuous.segments;

import llnl.gnem.core.util.Epoch;

/**
 *
 * @author dodge
 */
public final class Segment implements Comparable {

    private final int wfid;
    private final double start;
    private final double end;
    private final double rate;

    public Segment(int wfid, double start, double end, double rate) {
        this.wfid = wfid;
        this.start = start;
        this.end = end;
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Segment{" + "wfid=" + wfid + ", start=" + start + ", end=" + end + ", rate=" + rate + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.start) ^ (Double.doubleToLongBits(this.start) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.end) ^ (Double.doubleToLongBits(this.end) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.rate) ^ (Double.doubleToLongBits(this.rate) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Segment other = (Segment) obj;
        if (Double.doubleToLongBits(this.start) != Double.doubleToLongBits(other.start)) {
            return false;
        }
        if (Double.doubleToLongBits(this.end) != Double.doubleToLongBits(other.end)) {
            return false;
        }
        if (Double.doubleToLongBits(this.rate) != Double.doubleToLongBits(other.rate)) {
            return false;
        }
        return true;
    }

    /**
     * @return the wfid
     */
    public int getWfid() {
        return wfid;
    }

    /**
     * @return the start
     */
    public double getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public double getEnd() {
        return end;
    }

    /**
     * @return the rate
     */
    public double getRate() {
        return rate;
    }
    
    public Epoch getEpoch()
    {
        return new Epoch(start,end);
    }
    
    public double getLength()
    {
        return end - start;
    }

    @Override
    public int compareTo(Object t) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

         if (this == t) {
            return EQUAL;
        }

        Segment other = (Segment) t;
        //primitive numbers follow this form
        if (this.start < other.start) {
            return BEFORE;
        }
        if (this.start > other.start) {
            return AFTER;
        }

        return EQUAL;
    }
}
