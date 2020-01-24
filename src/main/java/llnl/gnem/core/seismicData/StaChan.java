package llnl.gnem.core.seismicData;

import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author addair1
 */
public class StaChan implements Comparable<StaChan> {
    private final StreamKey key;
    private final Station station;

    public StaChan(Station station, String chan) {
        this.key = new StreamKey(station.getSta(), chan);
        this.station = station;
    }

    public String getChan() {
        return key.getChan();
    }

    public Station getStation() {
        return station;
    }
    
    public String getSta() {
        return station.getSta();
    }
    
    public String getId() {
        return identifier(getSta(), key.getChan());
    }
    
    public StreamKey getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "StaChan{" + "station=" + station.getSta() + ", chan=" + key.getChan() + '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StaChan other = (StaChan) obj;
        return this.key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public int compareTo(StaChan t) {
        return this.key.compareTo(t.key);
    }
    
    public static String identifier(String sta, String chan) {
        return sta + chan;
    }
}
