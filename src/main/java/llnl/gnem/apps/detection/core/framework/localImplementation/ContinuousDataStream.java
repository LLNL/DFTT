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
package llnl.gnem.apps.detection.core.framework.localImplementation;

import java.io.IOException;

import com.oregondsp.util.TimeStamp;
import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;

import llnl.gnem.core.util.StreamKey;




public interface ContinuousDataStream {

    // accessors
  
    int                       numSamplesAvailable();                              // returns the number of samples available currently in the stream 

    TransformedStreamSegment  getSegment( int segmentLength ) throws IOException; // returns a new data segment
  
    int                       getNumChannels();                                   // returns number of data channels

    StreamKey[]              getStaChanArray();                                  // returns array of StaChan objects  
    
    double                    getSamplingRate();                                  // returns sampling rate (samples/seconds)

    TimeStamp                 getTimeStamp();                                     // returns epoch time of next available sample
                                                                                  //   null if no data available

    void                      close() throws IOException;
    
    int                       getFFTSize();
}