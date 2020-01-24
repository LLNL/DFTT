/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.dataObjects;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class SeismicPhase implements Serializable{
    private final PhaseType type;
    private final String name;
    private final String description;
    private static final long serialVersionUID = 6314439108570962520L;

    public SeismicPhase(PhaseType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * @return the type
     */
    public PhaseType getType() {
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.description);
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
        final SeismicPhase other = (SeismicPhase) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  name;
    }
    
}
