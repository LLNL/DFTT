/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.SubspaceParameters;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.statistics.HistogramData;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.TimeStamp;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.dataAccess.dataObjects.ProgressMonitor;

/**
 *
 * @author dodge1
 */
public interface SubspaceDetectorDAO {

    HistogramData getHistogramData(int detectorid, int runid) throws DataAccessException;

    SubspaceDetector createAndSaveSubspaceDetector(ConcreteStreamProcessor processor, 
            Collection<StreamSegment> eventSegments, 
            double prepickSeconds, 
            double correlationWindowLength, 
            SubspaceParameters params, 
            boolean fixToSpecifiedDimension, 
            boolean capSubspaceDimension, 
            int requiredDimension, 
            String creationInfo,
            ProgressMonitor monitor) throws DataAccessException;

    void writeHistograms(Collection<SubspaceDetector> detectors) throws DataAccessException;

    /**
     * This method creates rank-1 subspace detectors based on a segment of
     * pre-processed data extracted from the stream. This is the method by which
     * power detections are used to create correlators.
     *
     * @param segment
     * @param triggerTime
     * @param templateLeadSeconds
     * @param duration
     * @param streamProcessor
     * @return
     * @throws llnl.gnem.dftt.core.dataAccess.DataAccessException
     * @throws IllegalStateException
     */
    SubspaceDetector createDetectorFromStreamSegment(StreamSegment segment,
            TimeStamp triggerTime,
            double templateLeadSeconds,
            double duration,
            ConcreteStreamProcessor streamProcessor) throws DataAccessException;

    void updateTemplateInDB(int detectorid, SubspaceTemplate template) throws DataAccessException;

    Collection<? extends Detector> retrieveSubspaceDetectors(ConcreteStreamProcessor processor) throws DataAccessException;

    Collection<Detector> buildFromExternalTemplate(DetectorSpecification spec,
            ConcreteStreamProcessor processor,
            Collection<Detector> ssDetectors
    ) throws DataAccessException;
}
