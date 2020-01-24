/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.Objects;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.Trigger;

/**
 *
 * @author dodge1
 */
public class ClassifiedDetection extends Detection {

    private final String artifactStatus;
    private final String usabilityStatus;
    
    public ClassifiedDetection(int detectionid, Trigger trigger, String artifactStatus,String usabilityStatus )
    {
        super(detectionid, trigger);
        this.artifactStatus = artifactStatus;
        this.usabilityStatus = usabilityStatus;
    }

    public String getArtifactStatus() {
        return artifactStatus;
    }

    public String getUsabilityStatus() {
        return usabilityStatus;
    }
    
    public String toString()
    {
        return String.format("%s A_STAT = %s, U_STAT = %s",super.toString(),artifactStatus, usabilityStatus);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.artifactStatus);
        hash = 67 * hash + Objects.hashCode(this.usabilityStatus);
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
        final ClassifiedDetection other = (ClassifiedDetection) obj;
        if (!Objects.equals(this.artifactStatus, other.artifactStatus)) {
            return false;
        }
        if (!Objects.equals(this.usabilityStatus, other.usabilityStatus)) {
            return false;
        }
        return true;
    }
    
    
}
