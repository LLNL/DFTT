//                                                                           ContinuousDataStream.java
//  Copyright (c) 2009 Lawrence Livermore National Laboratory
//  All rights reserved
//
//  Interface for continuous data stream classes.  Intended to be implemented
//    for CSS3.0 and SAC continuous data stream access.
//    These classes return multichannel data in StreamSegment instances.
//      Behavior:
//        1.  Channels of final segment to be zero-filled if requested segment 
//            length is greater than the available amount of data, but a 
//            non-zero amount of data is available.
//        2.  getSegment returns null if no data are available.

//  Author:  D. Harris
//  Creation date:  December 1, 2001  by conversion from C++
//  Last modified:  November 16, 2009
//  Dependencies:
/*
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