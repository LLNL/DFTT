package llnl.gnem.core.gui.map.origins;

import java.util.Collection;
import java.util.Objects;
import llnl.gnem.core.gui.map.location.LocationInfo;
import llnl.gnem.core.seismicData.Netmag;
import llnl.gnem.core.seismicData.Origerr;
import llnl.gnem.core.seismicData.Origin;
import llnl.gnem.core.traveltime.Point3D;
import llnl.gnem.core.util.TimeT;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class OriginInfo extends LocationInfo<Origin> {

    /**
     * @return the prime
     */
    public boolean isPrime() {
        return prime;
    }
    private final long eventID;
    private final int originID;
    private final String auth;
    private final boolean prime;

    public OriginInfo(long eventID, 
            int originID, 
            double lat, 
            double lon, 
            Double depth, 
            double time, 
            Integer nass, 
            Integer ndef, 
            Integer ndp, 
            String etype, 
            String auth, 
            Origerr origerr, 
            Collection<Netmag> netmags, 
            boolean prime) {
        this(eventID, originID, new Origin(lat, lon, depth, time, nass, ndef, ndp, etype, origerr, netmags), auth, prime);
    }

    public OriginInfo(long evid, int orid, Origin origin, String auth, boolean prime) {
        super(origin);
        this.eventID = evid;
        this.originID = orid;
        this.auth = auth;
        this.prime = prime;
    }

    public int getOriginID() {
        return originID;
    }

    public Double getDepth() {
        return getLocation().getDepth();
    }
    
    public double getNonNullDepth()
    {
        Double tmp = getDepth();
        return tmp !=null ? tmp : 0.0;
    }

    public double getTime() {
        return getLocation().getTime();
    }

    public Integer getNass() {
        return getLocation().getNass();
    }

    public Integer getNdef() {
        return getLocation().getNdef();
    }

    public Integer getNdp() {
        return getLocation().getNdp();
    }

    public String getEtype() {
        return getLocation().getEtype();
    }

    public Origerr getOrigerr() {
        return getLocation().getOrigerr();
    }

    public Collection<Netmag> getNetmags() {
        return getLocation().getNetmags();
    }

    public String getAuth() {
        return auth;
    }

    public Point3D getPoint3D() {
        return new Point3D((float) getLon(), (float) getLat(), (float) -getNonNullDepth());
    }

    public String getLabelText() {
        String timeString = new TimeT(getTime()).toString();
        return String.format("<html><h3><font color=blue>Orid: %d, Lat: %8.4f, Lon: %9.4f, Depth: %5.1f, Time: %s, Nass: %d, Ndef: %d, Ndp: %d, Etype: %s, Auth: %s</font></h3></html>",
                originID, getLat(), getLon(), getDepth(), timeString, getNass(), getNdef(), getNdp(), getEtype(), auth);
    }

    @Override
    public String getMapAnnotation() {
        String title = String.format("<p><b>Evid: %d, Orid: %d</b></p>", getEventID(), getOriginID());
        String timeString = new TimeT(getTime()).toString();
        String summary = String.format("%s<br>Lat = %7.5f<br>Lon = %8.5f<br>Depth = %5.1f km<br>Author = %s<br>",
                timeString, getLat(), getLon(), getDepth(), auth);

        return title + summary;
    }
    
    @Override
    public String toString()
    {
        String primeStr = prime ? "Prime" : "Non-Prime";
        return String.format("%s solution by %s",primeStr, auth);
    }

    public long getEventID() {
        return eventID;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int)this.eventID;
        hash = 97 * hash + this.originID;
        hash = 97 * hash + Objects.hashCode(this.auth);
        hash = 97 * hash + (this.prime ? 1 : 0);
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
        final OriginInfo other = (OriginInfo) obj;
        if (this.eventID != other.eventID) {
            return false;
        }
        if (this.originID != other.originID) {
            return false;
        }
        if (this.prime != other.prime) {
            return false;
        }
        if (!Objects.equals(this.auth, other.auth)) {
            return false;
        }
        return true;
    }
    
}
