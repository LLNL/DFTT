/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors;

import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author harris2
 */
public abstract class AbstractSimpleDetector extends AbstractDetector {

    private static final long serialVersionUID = 7203831519211830946L;
    
    protected DetectorSpecification specification;
    
    
    
    public AbstractSimpleDetector( int detectorid, double sampleRate, String streamName, int decimatedBlockSize, DetectorSpecification specification ) throws FileSystemException {
        super( detectorid, specification, sampleRate, streamName, decimatedBlockSize );
        this.specification = specification;
    }
    
    
    
    @Override
    public int        getNumChannels() {
        return specification.getNumChannels();
    }

    
    
    @Override
    public StreamKey getStaChanKey( int index ) {
        return specification.getStreamKey(index);
    }
}
