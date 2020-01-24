package llnl.gnem.apps.detection.core.framework;

import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.Detection;

import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;

import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;

/**
 * Created by dodge1 Date: Sep 24, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public interface StreamProcessor {

    void processNewData() throws Exception;

    Collection<StreamKey> getChannels();

    void maybeAddChannel(WaveformSegment channel);

    int getStreamId();

    void addDetectors(Collection<? extends Detector> detectorCollection);

    Detector getDetector(Detection detection);

    String getStreamName();
    
    StreamSegment downSampleBlock(StreamSegment block);
    
    TransformedStreamSegment transformBlock(StreamSegment block);
    
    int getFFTSize();
    
    Collection<SubspaceDetector> getSubspaceDetectors();
}
