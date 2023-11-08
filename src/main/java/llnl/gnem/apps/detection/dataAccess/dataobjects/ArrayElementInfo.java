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
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import java.util.Objects;
import llnl.gnem.dftt.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public class ArrayElementInfo {

    private final String agency;
    private final String networkCode;
    private final String arrayName;
    private final String stationCode;
    private final String description;
    private final double beginTime;
    private final int ondate;
    private final double endTime;
    private final int offdate;
    private final double lat;
    private final double lon;
    private final double elev;
    private final String statype;
    private final double dnorth;
    private final double deast;
    private double dz = 0.0;

    public ArrayElementInfo(String agency,
            String networkCode,
            String arrayName,
            String stationCode,
            String description,
            double beginTime,
            int ondate,
            double endTime,
            int offdate,
            double lat,
            double lon,
            double elev,
            String statype,
            double dnorth,
            double deast) {
        this.agency = agency;
        this.networkCode = networkCode;
        this.arrayName = arrayName;
        this.stationCode = stationCode;
        this.description = description;
        this.beginTime = beginTime;
        this.ondate = ondate;
        this.endTime = endTime;
        this.offdate = offdate;
        this.lat = lat;
        this.lon = lon;
        this.elev = elev;
        this.statype = statype;
        this.dnorth = dnorth;
        this.deast = deast;
    }

    public String getAgency() {
        return agency;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public String getArrayName() {
        return arrayName;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getDescription() {
        return description;
    }

    public double getBeginTime() {
        return beginTime;
    }

    public int getOndate() {
        return ondate;
    }

    public double getEndTime() {
        return endTime;
    }

    public int getOffdate() {
        return offdate;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getElev() {
        return elev;
    }

    public String getStatype() {
        return statype;
    }

    public double getDnorth() {
        return dnorth;
    }

    public double getDeast() {
        return deast;
    }

    public double getDz() {
        return dz;
    }

    public void setDz(double dz) {
        this.dz = dz;
    }

    @Override
    public String toString() {
        return "ArrayInfo{" + "agency=" + agency + ", networkCode=" + networkCode + ", arrayName=" + arrayName + ", station_code=" + stationCode + ", description=" + description + ", beginTime=" + beginTime + ", ondate=" + ondate + ", endTime=" + endTime + ", offdate=" + offdate + ", lat=" + lat + ", lon=" + lon + ", elev=" + elev + ", statype=" + statype + ", dnorth=" + dnorth + ", deast=" + deast + '}';
    }

    public double delayInSeconds(float[] s) {
        switch (s.length) {
            case 2:
                return delayInSeconds(s[0], s[1]);
            case 3:
                return delayInSeconds(s[0], s[1], s[2]);
            default:
                throw new IllegalStateException("Slowness vector length " + s.length + " not correct for delay calculation");
        }
    }

    public double delayInSeconds(float sn, float se) {
        return delayInSeconds(sn, se, 0.0f);
    }

    // slowness vector points back toward the source in the local coordinate (x,y,z) frame
    public double delayInSeconds(float sn, float se, float sz) {
        return -(sn * getDnorth() + se * getDeast() + sz * dz);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.agency);
        hash = 23 * hash + Objects.hashCode(this.networkCode);
        hash = 23 * hash + Objects.hashCode(this.arrayName);
        hash = 23 * hash + Objects.hashCode(this.stationCode);
        hash = 23 * hash + Objects.hashCode(this.description);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.beginTime) ^ (Double.doubleToLongBits(this.beginTime) >>> 32));
        hash = 23 * hash + this.ondate;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.endTime) ^ (Double.doubleToLongBits(this.endTime) >>> 32));
        hash = 23 * hash + this.offdate;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.elev) ^ (Double.doubleToLongBits(this.elev) >>> 32));
        hash = 23 * hash + Objects.hashCode(this.statype);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.dnorth) ^ (Double.doubleToLongBits(this.dnorth) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.deast) ^ (Double.doubleToLongBits(this.deast) >>> 32));
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
        final ArrayElementInfo other = (ArrayElementInfo) obj;
        if (Double.doubleToLongBits(this.beginTime) != Double.doubleToLongBits(other.beginTime)) {
            return false;
        }
        if (this.ondate != other.ondate) {
            return false;
        }
        if (Double.doubleToLongBits(this.endTime) != Double.doubleToLongBits(other.endTime)) {
            return false;
        }
        if (this.offdate != other.offdate) {
            return false;
        }
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        if (Double.doubleToLongBits(this.elev) != Double.doubleToLongBits(other.elev)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dnorth) != Double.doubleToLongBits(other.dnorth)) {
            return false;
        }
        if (Double.doubleToLongBits(this.deast) != Double.doubleToLongBits(other.deast)) {
            return false;
        }
        if (!Objects.equals(this.agency, other.agency)) {
            return false;
        }
        if (!Objects.equals(this.networkCode, other.networkCode)) {
            return false;
        }
        if (!Objects.equals(this.arrayName, other.arrayName)) {
            return false;
        }
        if (!Objects.equals(this.stationCode, other.stationCode)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.statype, other.statype)) {
            return false;
        }
        return true;
    }

    public boolean isReferenceElement() {
        return arrayName.equals(stationCode) && statype.equals("ar");
    }

    public Epoch getEpoch(){
        return new Epoch(beginTime, endTime);
    }
}
