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
package llnl.gnem.apps.detection;

import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;

import llnl.gnem.apps.detection.util.initialization.StreamsConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.database.DetectorUtil;


import llnl.gnem.apps.detection.util.DetectoridRestriction;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.dftt.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Oct 11, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DetectorFactory {

    public static Collection<Detector> getDetectorsForStream(ConcreteStreamProcessor processor) throws Exception {
        String streamName = processor.getStreamName();
        Collection<Detector> result = new ArrayList<>();

        Collection<? extends Detector> bootdetectors = getBootDetectors(processor);
        result.addAll(bootdetectors);
        Collection<Detector> ssDetectors = new ArrayList<>();
        Collection<Detector> arrayCorrDetectors = new ArrayList<>();
        if (StreamsConfig.getInstance().isLoadCorrelatorsFromDb(streamName)) {
            Set<Integer> useThese = DetectoridRestriction.getInstance().getDetectoridSet();
            if (useThese.isEmpty()) {
                ssDetectors.addAll(DetectionDAOFactory.getInstance().getSubspaceDetectorDAO().retrieveSubspaceDetectors(processor));
            } else {
                Collection<? extends Detector> tmp = DetectionDAOFactory.getInstance().getSubspaceDetectorDAO().retrieveSubspaceDetectors(processor);
                for (Detector det : tmp) {
                    if (useThese.contains(det.getdetectorid())) {
                        ssDetectors.add(det);
                    }
                }
            }
            result.addAll(ssDetectors);
            arrayCorrDetectors.addAll(DetectionDAOFactory.getInstance().getArrayCorrelationDetectorDAO().retrieveStoredDetectors(processor));
            result.addAll(arrayCorrDetectors);
        }
        Collection<? extends Detector> userSpecifiedDetectors = loadUserSpecifiedDetectors(processor, ssDetectors, arrayCorrDetectors);
        result.addAll(userSpecifiedDetectors);
        ApplicationLogger.getInstance().log(Level.INFO, String.format(
                "Added %d boot detectors, %d correlators, %d, array correlation,  from DB, and %d user-specified detectors to wideband stream (%s).",
                bootdetectors.size(), ssDetectors.size(), arrayCorrDetectors.size(),
                userSpecifiedDetectors.size(), streamName));

        return result;
    }

    private static ArrayList<SubspaceTemplate> readTemplates(List<String> detectorids) throws Exception {
        ArrayList<SubspaceTemplate> result = new ArrayList<>();
        for (String detid : detectorids) {
            int detectorid = Integer.parseInt(detid);
            SubspaceTemplate template = DetectionDAOFactory.getInstance().getSubspaceTemplateDAO().getSubspaceTemplate(detectorid);
            result.add(template);
        }
        return result;
    }

    private static Collection<Detector> getBootDetectors(ConcreteStreamProcessor processor) throws Exception {
        Collection<Detector> result = new ArrayList<>();
        try {
            Collection<Integer> detectorids = DetectionDAOFactory.getInstance().getDetectorDAO().getBootDetectorIds( processor.getStreamId());
            for (int detectorid : detectorids) {
                Detector detector = retrieveDetector(processor, detectorid);
                result.add(detector);
            }
            return result;
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failure retrieving boot detectors!", ex);
            throw ex;
        } 
    }

    private static Detector retrieveDetector( ConcreteStreamProcessor processor, int detectorid) throws Exception {
        DetectorType type = DetectorUtil.getDetectorType(detectorid);
        switch (type) {
            case ARRAYPOWER:
                return DetectionDAOFactory.getInstance().getArrayDetectorDAO().retrieveArrayDetector(detectorid, processor);
            case FSTATISTIC:
                throw new UnsupportedOperationException("FSTATISTIC detector not implemented yet");
            case STALTA:
                return DetectionDAOFactory.getInstance().getStaLtaDetectorDAO().retrieveStaLtaDetector(detectorid, processor);
            case BULLETIN:
                return DetectionDAOFactory.getInstance().getBulletinDetectorDAO().retrieveBulletinDetector(detectorid, processor);
            default: {
                throw new IllegalStateException("Invalid type for boot detector!");
            }
        }
    }

    private static Collection<? extends Detector> loadUserSpecifiedDetectors(ConcreteStreamProcessor processor,
            Collection<Detector> ssDetectors,
            Collection<Detector> arrayCorrDetectors) throws Exception {
        Collection<Detector> result = new ArrayList<>();
        int streamid = processor.getStreamId();
        String streamName = processor.getStreamName();
        PreprocessorParams params = processor.getParams();
        try {
            Collection<DetectorSpecification> specs = StreamsConfig.getInstance().getDetectorSpecifications(streamName);
            for (DetectorSpecification spec : specs) {
                ApplicationLogger.getInstance().log(Level.INFO, String.format("Creating detector from specification: %s...", spec.toString()));

                switch (spec.getDetectorType()) {
                    case SUBSPACE: {
                        SubspaceSpecification sSpec = (SubspaceSpecification) spec;
                        if (sSpec.getWindowDurationSeconds() > ProcessingPrescription.getInstance().getMaxTemplateLength()) {
                            throw new IllegalStateException(String.format("User-specified subspace detector has duration (%f s) which is greater than max template length of (%f s)!",
                                    sSpec.getWindowDurationSeconds(), ProcessingPrescription.getInstance().getMaxTemplateLength()));
                        }
                        result.addAll(DetectionDAOFactory.getInstance().getSubspaceDetectorDAO().buildFromExternalTemplate(spec, processor, ssDetectors));
                        break;
                    }
                    case ARRAY_CORRELATION: {
                        result.addAll(DetectionDAOFactory.getInstance().getArrayCorrelationDetectorDAO().buildFromExternalTemplate(spec, processor, arrayCorrDetectors));
                        break;
                    }
                    default: {
                    }
                }
            }

            return result;
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed loading user detectors!", ex);
            throw ex;
        }
    }
}
