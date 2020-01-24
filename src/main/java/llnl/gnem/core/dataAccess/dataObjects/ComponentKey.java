/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class ComponentKey {
    private final long stationId;
    private final String band;
    private final String instrumentCode;
    private final String locationCode;

    public ComponentKey(long stationId, String band, String instrumentCode, String locationCode) {
        this.stationId = stationId;
        this.band = band;
        this.instrumentCode = instrumentCode;
        this.locationCode = locationCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int)this.stationId;
        hash = 89 * hash + Objects.hashCode(this.band);
        hash = 89 * hash + Objects.hashCode(this.instrumentCode);
        hash = 89 * hash + Objects.hashCode(this.locationCode);
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
        final ComponentKey other = (ComponentKey) obj;
        if (this.stationId != other.stationId) {
            return false;
        }
        if (!Objects.equals(this.band, other.band)) {
            return false;
        }
        if (!Objects.equals(this.instrumentCode, other.instrumentCode)) {
            return false;
        }
        if (!Objects.equals(this.locationCode, other.locationCode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ComponentKey{" + "stationId=" + stationId + ", band=" + band + ", instrumentCode=" + instrumentCode + ", locationCode=" + locationCode + '}';
    }
    
}
