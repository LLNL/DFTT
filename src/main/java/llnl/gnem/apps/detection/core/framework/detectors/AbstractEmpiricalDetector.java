/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors;

import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author harris2
 */
public abstract class AbstractEmpiricalDetector extends AbstractDetector {
    
    protected EmpiricalTemplate template;
    private static final long serialVersionUID = 6402337497569425207L;
    
    public EmpiricalTemplate getTemplate()
    {
        return template;
    }
    
    public AbstractEmpiricalDetector( int detectorid, double sampleRate, String streamName, int decimatedBlockSize, EmpiricalTemplate template ) throws FileSystemException {
        super( detectorid, template.getSpecification(), sampleRate, streamName, decimatedBlockSize );
        this.template = template;
    }
    
    
    
    @Override
    public int        getNumChannels() {
        return template.specification.getNumChannels();
    }
    
    
    
    @Override
    public StreamKey getStaChanKey( int index ) {
        return template.specification.getStreamKey(index);
    }
    
}
