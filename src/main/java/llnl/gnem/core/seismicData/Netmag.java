/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.seismicData;

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
