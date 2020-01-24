package llnl.gnem.apps.detection.core.dataObjects;

import java.io.PrintStream;
import java.util.Collection;
import llnl.gnem.core.util.StreamKey;

public interface DetectorSpecification {

    DetectorType                     getDetectorType();                      // instance of enumeration denoting type of detector

    float                            getThreshold();                         // threshold on detection statistic for declaring triggers

    float                            getBlackoutPeriod();                    // blackout period (seconds) is period over which triggers are suppressed following 
                                                                             //   a declared trigger

    Collection< ? extends StreamKey> getStaChanList();                       // list of station-channel pairs to be processed by the detector

    StreamKey                        getStreamKey( int index );              // station-channel pair corresponding to channel index

    int                              getNumChannels();                       // returns the number of channels

    TriggerPositionType              getTriggerPositionType();               // specifies whether triggers are formed on threshold crossings or at maximum of the statistic

    boolean                          spawningEnabled();                      // returns "true" if spawning is enabled, false otherwise - always false for subspace detectors

    void                             printSpecification( PrintStream ps );   // prints specification values - intended for debugging

    void                             setThreshold( float value );

    boolean                          isArraySpecification();
}
