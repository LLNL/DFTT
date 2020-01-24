/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.tasks;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.framework.DetectionStatistic;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.DetectorInfo;
import llnl.gnem.core.util.ApplicationLogger;

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
