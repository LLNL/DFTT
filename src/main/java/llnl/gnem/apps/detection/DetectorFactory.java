package llnl.gnem.apps.detection;

import java.nio.file.Files;
import java.nio.file.Paths;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.database.ArrayCorrelationDetectorDAO;
import llnl.gnem.apps.detection.database.ArrayDetectorDAO;
import llnl.gnem.apps.detection.database.DbOps;
import llnl.gnem.apps.detection.database.StaLtaDetectorDAO;
import llnl.gnem.apps.detection.database.SubspaceDetectorDAO;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import llnl.gnem.apps.detection.cancellation.CancellationTemplateSource;
import llnl.gnem.apps.detection.cancellation.CancellorFactory;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.database.BulletinDetectorDAO;
import llnl.gnem.apps.detection.database.SubspaceTemplateDAO;
import llnl.gnem.apps.detection.streams.StreamModifierManager;
import llnl.gnem.apps.detection.util.DetectoridRestriction;
import llnl.gnem.apps.detection.util.initialization.ProcessingPrescription;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Created by dodge1 Date: Oct 11, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class DetectorFactory {
    
    public static Collection<Detector> getDetectorsForStream(ConcreteStreamProcessor processor) throws Exception {
        String streamName = processor.getStreamName();
        if (StreamsConfig.getInstance().isApplyStreamCancellation(streamName)) {
            int decimatedBlockSize = processor.getParams().getDecimatedDataBlockSize();
            double decdimatedRate = processor.getParams().getPreprocessorParams().getDecimatedSampleRate();
            
            String cancellorParamsFile = StreamsConfig.getInstance().getCancellorParamsFile(streamName);
            CancellationTemplateSource templateSource = StreamsConfig.getInstance().getCancellationTemplateSource(streamName);
            switch (templateSource) {
                case SAC_FILES:
                    StreamModifierManager.getInstance().addModifier(CancellorFactory.createCancellor(cancellorParamsFile, 1.0 / decdimatedRate, decimatedBlockSize));
                    break;
                case SUBSPACE_TEMPLATES:
                    String filename = StreamsConfig.getInstance().getCancellationDetectoridFile(streamName);
                    List<String> detectorids = Files.lines(Paths.get(filename)).collect(Collectors.toList());
                    ArrayList<SubspaceTemplate> templates = readTemplates( detectorids);
                    StreamModifierManager.getInstance().addModifier(CancellorFactory.createCancellor(cancellorParamsFile, 
                            1.0 / decdimatedRate, 
                            decimatedBlockSize,
                            templates));
                    break;
            }
            
        }
        Collection<Detector> result = new ArrayList<>();
        
        Collection<? extends Detector> bootdetectors = getBootDetectors(processor);
        result.addAll(bootdetectors);
        Collection<Detector> ssDetectors = new ArrayList<>();
        Collection<Detector> arrayCorrDetectors = new ArrayList<>();
        if (StreamsConfig.getInstance().isLoadCorrelatorsFromDb(streamName)) {
            Set<Integer> useThese = DetectoridRestriction.getInstance().getDetectoridSet();
            if (useThese.isEmpty()) {
                ssDetectors.addAll(SubspaceDetectorDAO.getInstance().retrieveSubspaceDetectors(processor));
            } else {
                Collection<? extends Detector> tmp = SubspaceDetectorDAO.getInstance().retrieveSubspaceDetectors(processor);
                for (Detector det : tmp) {
                    if (useThese.contains(det.getdetectorid())) {
                        ssDetectors.add(det);
                    }
                }
            }
            result.addAll(ssDetectors);
            arrayCorrDetectors.addAll(ArrayCorrelationDetectorDAO.getInstance().retrieveStoredDetectors(processor));
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
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            for (String detid : detectorids) {
                int detectorid = Integer.parseInt(detid);
                SubspaceTemplate template = SubspaceTemplateDAO.getInstance().getSubspaceTemplate(conn, detectorid);
                result.add(template);
            }
            return result;
        } finally {
            ConnectionManager.getInstance().checkIn(conn);
        }
    }
    
    private static Collection<Detector> getBootDetectors(ConcreteStreamProcessor processor) throws Exception {
        Collection<Detector> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            Collection<Integer> detectorids = DbOps.getInstance().getBootDetectorIds(conn, processor.getStreamId());
            for (int detectorid : detectorids) {
                Detector detector = retrieveDetector(conn, processor, detectorid);
                result.add(detector);
            }
            return result;
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failure retrieving boot detectors!", ex);
            throw ex;
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
        
    }
    
    private static Detector retrieveDetector(Connection conn, ConcreteStreamProcessor processor, int detectorid) throws Exception {
        DetectorType type = DbOps.getInstance().getDetectorType(conn, detectorid);
        switch (type) {
            case ARRAYPOWER:
                return ArrayDetectorDAO.getInstance().retrieveArrayDetector(conn, detectorid, processor);
            case FSTATISTIC:
                throw new UnsupportedOperationException("FSTATISTIC detector not implemented yet");
            case STALTA:
                return StaLtaDetectorDAO.getInstance().retrieveStaLtaDetector(conn, detectorid, processor);
            case BULLETIN:
                return BulletinDetectorDAO.getInstance().retrieveBulletinDetector(conn, detectorid, processor);
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
                        result.addAll(SubspaceDetectorDAO.getInstance().buildFromExternalTemplate(spec, processor, ssDetectors));
                        break;
                    }
                    case ARRAY_CORRELATION: {
                        result.addAll(ArrayCorrelationDetectorDAO.getInstance().buildFromExternalTemplate(spec, processor, arrayCorrDetectors));
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
