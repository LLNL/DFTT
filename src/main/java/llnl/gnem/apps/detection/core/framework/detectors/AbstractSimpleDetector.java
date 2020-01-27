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
