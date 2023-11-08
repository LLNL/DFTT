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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.dftt.core.dataAccess.dataObjects;


import java.util.Objects;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class WaveformSegmentProblem {

    private final long segmentId;
    private final long waveformId;
    private final double beginTime;
    private final double endTime;
    private final int problemId;
    private final String problemDescription;
    private final String auth;

    public WaveformSegmentProblem(long segid,
            long wfid,
            double time,
            double endtime,
            int probid,
            String problemDescription,
            String auth) {
        this.segmentId = segid;
        this.waveformId = wfid;
        this.beginTime = time;
        this.endTime = endtime;
        this.problemId = probid;
        this.problemDescription = problemDescription;
        this.auth = auth;
    }

    public WaveformSegmentProblem(WaveformSegmentProblem old) {
        segmentId = old.segmentId;
        waveformId = old.waveformId;
        beginTime = old.beginTime;
        endTime = old.endTime;
        problemId = old.problemId;
        problemDescription = old.problemDescription;
        auth = old.auth;
    }

    public long getSegmentId() {
        return segmentId;
    }

    public long getWaveformId() {
        return waveformId;
    }

    public double getBeginTime() {
        return beginTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public int getProblemId() {
        return problemId;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public String getAuth() {
        return auth;
    }

    @Override
    public String toString() {
        return "WaveformSegmentProblem{" + "segmentId=" + segmentId + ", waveformId=" + waveformId + ", beginTime=" + beginTime + ", endTime=" + endTime + ", problemId=" + problemId + ", problemDescription=" + problemDescription + ", auth=" + auth + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.segmentId ^ (this.segmentId >>> 32));
        hash = 79 * hash + (int) (this.waveformId ^ (this.waveformId >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.beginTime) ^ (Double.doubleToLongBits(this.beginTime) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.endTime) ^ (Double.doubleToLongBits(this.endTime) >>> 32));
        hash = 79 * hash + this.problemId;
        hash = 79 * hash + Objects.hashCode(this.problemDescription);
        hash = 79 * hash + Objects.hashCode(this.auth);
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
        final WaveformSegmentProblem other = (WaveformSegmentProblem) obj;
        if (this.segmentId != other.segmentId) {
            return false;
        }
        if (this.waveformId != other.waveformId) {
            return false;
        }
        if (Double.doubleToLongBits(this.beginTime) != Double.doubleToLongBits(other.beginTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.endTime) != Double.doubleToLongBits(other.endTime)) {
            return false;
        }
        if (this.problemId != other.problemId) {
            return false;
        }
        if (!Objects.equals(this.problemDescription, other.problemDescription)) {
            return false;
        }
        if (!Objects.equals(this.auth, other.auth)) {
            return false;
        }
        return true;
    }

 
}
