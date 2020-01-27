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

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class StationInfo {
    private final int configid;
    private final String sta;
    private final double stla;
    private final double stlo;

    public StationInfo(int configid, String sta, double stla, double stlo) {
        this.configid = configid;
        this.sta = sta;
        this.stla = stla;
        this.stlo = stlo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.configid;
        hash = 37 * hash + Objects.hashCode(this.sta);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.stla) ^ (Double.doubleToLongBits(this.stla) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.stlo) ^ (Double.doubleToLongBits(this.stlo) >>> 32));
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
        final StationInfo other = (StationInfo) obj;
        if (this.configid != other.configid) {
            return false;
        }
        if (Double.doubleToLongBits(this.stla) != Double.doubleToLongBits(other.stla)) {
            return false;
        }
        if (Double.doubleToLongBits(this.stlo) != Double.doubleToLongBits(other.stlo)) {
            return false;
        }
        if (!Objects.equals(this.sta, other.sta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EventStationInfo{" + "configid=" + configid + ", sta=" + sta + ", stla=" + stla + ", stlo=" + stlo + '}';
    }

    public int getConfigid() {
        return configid;
    }

    public String getSta() {
        return sta;
    }

    public double getStla() {
        return stla;
    }

    public double getStlo() {
        return stlo;
    }
    
}
