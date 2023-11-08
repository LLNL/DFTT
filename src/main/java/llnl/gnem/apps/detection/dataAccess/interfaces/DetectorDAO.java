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
import java.util.List;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.TemplateNormalization;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DetectorStats;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public interface DetectorDAO {

    List<StreamKey> getDetectorChannels(long detectorid) throws DataAccessException;

    List<StreamKey> getDetectorChannelsFromConfig(long configid) throws DataAccessException;

    void writeChangedThreshold(int runid, int detectorid, TimeT streamTime, double newThreshold) throws DataAccessException;

    /**
     * Replaces the template of a subspace detector with a compatible template
     * from another detector (assumed to be for same signal) but with a possible
     * shift and improved SNR from stacking or other enhancement. Also updates
     * the times and signal_durations in trigger_record if the offset is
     * non-zero. Does not update any other statistics. The source detector is
     * deleted upon completion.
     *
     * @param oldDetectorid The detector whose template will be replaced
     * @param newDetectorid The detector whos template will be used
     * @param templateOffset The offset in seconds of the new template start
     * relative to the old
     * @param templateDuration The duration of the new template
     * @param sourceInfo
     * @throws llnl.gnem.dftt.core.dataAccess.DataAccessException
     */    void replaceSubspaceTemplate(int oldDetectorid,
            int newDetectorid,
            double templateOffset,
            double templateDuration,
            String sourceInfo) throws DataAccessException;
     
     void deleteDetector(int detectorid) throws DataAccessException;
     
     ArrayList<Integer> getSubspaceDetectorIDsWithDetections(int runid) throws DataAccessException;
     
     String getDetectorSourceInfo(int detectorid) throws DataAccessException;
     
     Collection<DetectorStats> getDetectorStats(int runid, boolean suppressBadDetectors) throws DataAccessException;
     
     int getNewDetectorid() throws DataAccessException;
     
     Collection<? extends Detector> retrieveSubspaceDetectors(ConcreteStreamProcessor processor,
            TemplateNormalization normalizationType, DetectorType type) throws DataAccessException;
     
     Collection<Integer> getBootDetectorIds(int streamid) throws DataAccessException;
}
