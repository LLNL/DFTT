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
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.DetectionObjects;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Trigger;
import llnl.gnem.apps.detection.core.dataObjects.TriggerDataFeatures;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectionSummary;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ShortDetectionSummary;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.ClassifiedDetection;
import llnl.gnem.apps.detection.util.DetectorSubstitution;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.util.Epoch;

/**
 *
 * @author dodge1
 */
public interface DetectionDAO {

    Collection<ShortDetectionSummary> getDetectionsInTimeInterval(int configid, Epoch epoch) throws DataAccessException;

    Detection detectionFromTrigger(Trigger trigger) throws DataAccessException;
    
    Collection<ClassifiedDetection> getDetections(int runid, int detectorid) throws DataAccessException;
    
    Detection getSingleDetection(int detectionid) throws DataAccessException;
    
    DetectionObjects getDetectionObjects(int runid, int detectorid, boolean retrieveByBlocks, int blockSize, int lastRetrievedDetectionId) throws DataAccessException;
    
    TriggerDataFeatures getTriggerDataFeatures(int detectionid) throws DataAccessException;
    
    void deleteDetection(Detection detection) throws DataAccessException;
    
    void deleteDetections(ArrayList<Integer> detectionIdValues) throws DataAccessException;
    
    void reassignDetection(Detection detection, DetectorSubstitution substitute) throws DataAccessException;
    
    Collection<String> reportAllDetections(int runid) throws DataAccessException;
    
    Collection<String> reportDetectionSummary(int runid)throws DataAccessException;

    Collection<DetectionSummary> getDetectionSummaries(int runid, int detectorid, double detStatThreshold)throws DataAccessException;
}
