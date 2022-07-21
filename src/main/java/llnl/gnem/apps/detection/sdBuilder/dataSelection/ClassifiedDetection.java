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
package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.Objects;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Trigger;

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
    
    @Override
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
