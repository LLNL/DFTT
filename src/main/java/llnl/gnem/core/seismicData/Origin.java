package llnl.gnem.core.seismicData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.geom.Location;

public class Origin extends Location<GeographicCoordinate> {

    private final Double depth;
    private final double time;
    private final Integer nass;
    private final Integer ndef;
    private final Integer ndp;
    private final Double depdp;
    private final String dtype;
    private final String etype;
    private final String algorithm;
    private final Origerr origerr;
    private final Collection<Netmag> netmags;

    public Origin(double lat, double lon, Double depth, double time) {
        this(lat, lon, depth, time, null);
        
    }

    public Origin(double lat, double lon, Double depth, double time, String auth) {
        this(lat, lon, depth, time, null, null, null, null, null, new ArrayList<Netmag>(), auth);
    }

    public Origin(Event<GeographicCoordinate> event) {
        this(event.getCoordinate().getLat(), event.getCoordinate().getLon(), event.getDepth(), event.getTime().getEpochTime());
    }

    public Origin(
            double lat,
            double lon,
            Double depth,
            double time,
            Integer nass,
            Integer ndef,
            Integer ndp,
            String etype,
            Origerr origerr,
            Collection<Netmag> netmags) {
        this(lat, lon, depth, time, nass, ndef, ndp, etype, origerr, netmags, null);
    }

    public Origin(
            double lat,
            double lon,
            Double depth,
            double time,
            Integer nass,
            Integer ndef,
            Integer ndp,
            String etype,
            Origerr origerr,
            Collection<Netmag> netmags,
            String auth) {
        super(new GeographicCoordinate(lat, lon), auth);
        this.depth = depth;
        this.time = time;
        this.nass = nass;
        this.ndef = ndef;
        this.ndp = ndp;
        this.etype = etype;
        this.origerr = origerr;
        this.netmags = new ArrayList<>(netmags);
        depdp = null;
        dtype = null;
        algorithm = null;
    }

public Origin(
            double lat,
            double lon,
            Double depth,
            double time,
            Integer nass,
            Integer ndef,
            Integer ndp,
            String etype,
            Origerr origerr,
            Collection<Netmag> netmags,
            String auth,
            Double depdp,
            String dtype,
            String algorithm) {
        super(new GeographicCoordinate(lat, lon), auth);
        this.depth = depth;
        this.time = time;
        this.nass = nass;
        this.ndef = ndef;
        this.ndp = ndp;
        this.etype = etype;
        this.origerr = origerr;
        this.netmags = new ArrayList<>(netmags);
        this.depdp = depdp;
        this.dtype = dtype;
        this.algorithm = algorithm;
    }    
    
    public Double getDepth() {
        return depth;
    }

    public Double getTime() {
        return time;
    }

    public Integer getNass() {
        return nass;
    }

    public Integer getNdef() {
        return ndef;
    }

    public Integer getNdp() {
        return ndp;
    }

    public String getEtype() {
        return etype;
    }

    public String getDtype() {
        return dtype;
    }

    public Double getDepdp() {
        return depdp;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Origerr getOrigerr() {
        return origerr;
    }

    public Collection<Netmag> getNetmags() {
        return new ArrayList<>(netmags);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.depth);
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
        hash = 73 * hash + Objects.hashCode(this.nass);
        hash = 73 * hash + Objects.hashCode(this.ndef);
        hash = 73 * hash + Objects.hashCode(this.ndp);
        hash = 73 * hash + Objects.hashCode(this.depdp);
        hash = 73 * hash + Objects.hashCode(this.dtype);
        hash = 73 * hash + Objects.hashCode(this.etype);
        hash = 73 * hash + Objects.hashCode(this.algorithm);
        hash = 73 * hash + Objects.hashCode(this.origerr);
        hash = 73 * hash + Objects.hashCode(this.netmags);
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
        final Origin other = (Origin) obj;
        if (Double.doubleToLongBits(this.time) != Double.doubleToLongBits(other.time)) {
            return false;
        }
        if (!Objects.equals(this.dtype, other.dtype)) {
            return false;
        }
        if (!Objects.equals(this.etype, other.etype)) {
            return false;
        }
        if (!Objects.equals(this.algorithm, other.algorithm)) {
            return false;
        }
        if (!Objects.equals(this.depth, other.depth)) {
            return false;
        }
        if (!Objects.equals(this.nass, other.nass)) {
            return false;
        }
        if (!Objects.equals(this.ndef, other.ndef)) {
            return false;
        }
        if (!Objects.equals(this.ndp, other.ndp)) {
            return false;
        }
        if (!Objects.equals(this.depdp, other.depdp)) {
            return false;
        }
        if (!Objects.equals(this.origerr, other.origerr)) {
            return false;
        }
        if (!Objects.equals(this.netmags, other.netmags)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Origin{" + "depth=" + depth + ", time=" + time + ", nass=" + nass + ", ndef=" + ndef + ", ndp=" + ndp + ", depdp=" + depdp + ", dtype=" + dtype + ", etype=" + etype + ", algorithm=" + algorithm + ", origerr=" + origerr + ", netmags=" + netmags + '}';
    }

}
