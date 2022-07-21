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
package llnl.gnem.apps.detection.core.dataObjects;

import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import java.io.PrintStream;
import java.util.Collection;
import llnl.gnem.core.util.StreamKey;

public interface DetectorSpecification {

    DetectorType getDetectorType();                      // instance of enumeration denoting type of detector

    float getThreshold();                         // threshold on detection statistic for declaring triggers

    float getBlackoutPeriod();                    // blackout period (seconds) is period over which triggers are suppressed following 
    //   a declared trigger

    Collection<StreamKey> getStreamKeys();                       // list of station-channel pairs to be processed by the detector

    StreamKey getStreamKey(int index);              // station-channel pair corresponding to channel index

    int getNumChannels();                       // returns the number of channels

    TriggerPositionType getTriggerPositionType();               // specifies whether triggers are formed on threshold crossings or at maximum of the statistic

    boolean spawningEnabled();                      // returns "true" if spawning is enabled, false otherwise - always false for subspace detectors

    void printSpecification(PrintStream ps);   // prints specification values - intended for debugging

    void setThreshold(float value);

    boolean isArraySpecification();
}
