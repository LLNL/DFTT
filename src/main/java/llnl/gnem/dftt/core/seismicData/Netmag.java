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
package llnl.gnem.dftt.core.seismicData;

import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@Immutable
@ThreadSafe
public class Netmag {

    private final int magnitudeID;
    private final int eventID;
    private final int originID;
    private final String magtype;
    private final Integer nsta;
    private final double magnitude;
    private final Double uncertainty;
    private final String auth;

    public Netmag(int magid,
            int orid,
            int evid,
            String magtype,
            Integer nsta,
            double magnitude,
            Double uncertainty,
            String auth) {
        this.magnitudeID = magid;
        this.originID = orid;
        this.eventID = evid;
        this.magnitude = magnitude;
        this.uncertainty = uncertainty;
        this.nsta = nsta;
        this.magtype = magtype;
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "NetmagInfo{" + "magid=" + magnitudeID + "evid=" + eventID + "orid=" + originID + ", magnitude=" + magnitude + ", uncertainty=" + uncertainty + ", magtype=" + magtype + ", auth=" + auth + '}';
    }

    public int getOriginID() {
        return originID;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public Double getUncertainty() {
        return uncertainty;
    }

    public Integer getNsta() {
        return nsta;
    }

    public int getMagnitudeID() {
        return magnitudeID;
    }

    public int getEventID() {
        return eventID;
    }

    public String getMagtype() {
        return magtype;
    }

    public String getAuth() {
        return auth;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.magnitudeID;
        hash = 89 * hash + this.eventID;
        hash = 89 * hash + this.originID;
        hash = 89 * hash + Objects.hashCode(this.magtype);
        hash = 89 * hash + Objects.hashCode(this.nsta);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.magnitude) ^ (Double.doubleToLongBits(this.magnitude) >>> 32));
        hash = 89 * hash + Objects.hashCode(this.uncertainty);
        hash = 89 * hash + Objects.hashCode(this.auth);
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
        final Netmag other = (Netmag) obj;
        if (this.magnitudeID != other.magnitudeID) {
            return false;
        }
        if (this.eventID != other.eventID) {
            return false;
        }
        if (this.originID != other.originID) {
            return false;
        }
        if (Double.doubleToLongBits(this.magnitude) != Double.doubleToLongBits(other.magnitude)) {
            return false;
        }
        if (!Objects.equals(this.magtype, other.magtype)) {
            return false;
        }
        if (!Objects.equals(this.auth, other.auth)) {
            return false;
        }
        if (!Objects.equals(this.nsta, other.nsta)) {
            return false;
        }
        if (!Objects.equals(this.uncertainty, other.uncertainty)) {
            return false;
        }
        return true;
    }
    
}
