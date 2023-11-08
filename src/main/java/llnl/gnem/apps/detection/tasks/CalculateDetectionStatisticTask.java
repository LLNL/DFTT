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
package llnl.gnem.apps.detection.tasks;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.dftt.core.util.ApplicationLogger;

/**
 *
 * @author dodge
 */
public class CalculateDetectionStatisticTask implements Callable<DetectionStatistic> {

    private final Detector detector;
    private final TransformedStreamSegment segment;

    public CalculateDetectionStatisticTask(Detector detector, TransformedStreamSegment segment) {
        this.detector = detector;
        this.segment = segment;
    }

    @Override
    public DetectionStatistic call() throws Exception {
        try {
            return detector.calculateDetectionStatistic(segment);
        } catch (Exception e) {
            String msg = String.format("Failed calculating detection statistic (%s)! Exception message is(%s)", detector.toString(), e.getMessage());
            ApplicationLogger.getInstance().log(Level.SEVERE, msg);
        }
        DetectorInfo detectorInfo = new DetectorInfo(detector.getdetectorid(), detector.getName(), detector.getDetectorType(),
                detector.getProcessingDelayInSeconds(), detector.getSpecification(), detector.getDetectorDelayInSeconds(), null, null, null);
        return new DetectionStatistic(segment.size(),
                segment.getStartTime(),
                segment.getSamplerate(),
                detectorInfo);
    }

}
