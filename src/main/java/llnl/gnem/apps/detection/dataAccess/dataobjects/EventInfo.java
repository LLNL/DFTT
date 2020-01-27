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
package llnl.gnem.apps.detection.dataAccess.dataobjects;

/**
 *
 * @author dodge1
 */
public class EventInfo {
    private final int eventid;
    private final double minTime;
    private final double maxTime;

    public EventInfo(int eventid, double minTime, double maxTime) {
        this.eventid = eventid;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public int getEventid() {
        return eventid;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxTime() {
        return maxTime;
    }
    
    public double getDuration()
    {
        return maxTime - minTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.eventid;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.minTime) ^ (Double.doubleToLongBits(this.minTime) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.maxTime) ^ (Double.doubleToLongBits(this.maxTime) >>> 32));
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
        final EventInfo other = (EventInfo) obj;
        if (this.eventid != other.eventid) {
            return false;
        }
        if (Double.doubleToLongBits(this.minTime) != Double.doubleToLongBits(other.minTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxTime) != Double.doubleToLongBits(other.maxTime)) {
            return false;
        }
        return true;
    }
    
}
