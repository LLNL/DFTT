/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
