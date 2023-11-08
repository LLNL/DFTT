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

import llnl.gnem.dftt.core.util.FileSystemException;
import llnl.gnem.dftt.core.util.StreamKey;

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
