/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects.continuous;

import java.util.Objects;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class StreamAvailability implements Comparable<StreamAvailability>{
    private final StreamKey key;
    private final Epoch range;
    private final Double sampleRate;
    private final int timeSpans;

    public StreamAvailability(StreamKey key, Epoch range, Double sampleRate, int timeSpans) {
        this.key = key;
        this.range = range;
        this.sampleRate = sampleRate;
        this.timeSpans = timeSpans;
    }

    public StreamKey getKey() {
        return key;
    }

    public Epoch getRange() {
        return range;
    }

    public Double getSampleRate() {
        return sampleRate;
    }

    public int getTimeSpans() {
        return timeSpans;
    }

    @Override
    public String toString() {
        return key.getShortName() + ", " + range + ", Rate=" + sampleRate + ", timeSpans=" + timeSpans ;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.key);
        hash = 67 * hash + Objects.hashCode(this.range);
        hash = 67 * hash + Objects.hashCode(this.sampleRate);
        hash = 67 * hash + this.timeSpans;
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
        final StreamAvailability other = (StreamAvailability) obj;
        if (this.timeSpans != other.timeSpans) {
            return false;
        }
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.range, other.range)) {
            return false;
        }
        if (!Objects.equals(this.sampleRate, other.sampleRate)) {
            return false;
        }
        return true;
    }

    public boolean contains(TimeT atime) {
        return range.ContainsTime(atime);
    }



    @Override
    public int compareTo(StreamAvailability o) {
      return  (int)Math.round(Math.signum(o.timeSpans-timeSpans));
    }
    
    
}
