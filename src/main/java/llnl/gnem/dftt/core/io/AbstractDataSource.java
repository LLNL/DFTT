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
package llnl.gnem.dftt.core.io;

/**
 * Copyright (c) 2003  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Jan 25, 2004
 * Time: 1:21:26 PM
 * Last Modified: Jan 25, 2004
 */

import llnl.gnem.dftt.core.io.DataSource;
import llnl.gnem.dftt.core.util.TimeT;

import java.io.PrintStream;



public abstract class AbstractDataSource implements DataSource {

  protected long   totalNumSamples;
  protected long   nextSample;
  protected long   numSamplesRemaining;
  protected String station;
  protected String channel;
  protected double samplingRate;
  protected double startTime;


  public void   getData( float[] dataArray ) {
    getData( dataArray, 0, dataArray.length );
  }


  public long   getNumSamplesAvailable() { return numSamplesRemaining; }


  public long   getTotalNumSamples() {     return totalNumSamples; }


  public String getStation() {             return station; }


  public String getChannel(){              return channel; }


  public double getSamplingRate() {        return samplingRate; }


  public long   getNextSampleIndex() {     return nextSample; }


  public double getEpochStartTime() {      return startTime; }


  public double getEpochEndTime() {
    return startTime + ( (double) ( totalNumSamples - 1 ) ) / samplingRate;
  }


  public double getCurrentEpochTime() {    return  startTime + ( (double) ( nextSample ) ) / samplingRate; }


  public void   initiate() {
    numSamplesRemaining = totalNumSamples;
    nextSample = 0;
  }


  public void print( PrintStream ps ) {
    ps.println( "  Station:                     " + station );
    ps.println( "  Channel:                     " + channel );
    ps.println( "  Sampling rate:               " + samplingRate + " samples per second");
    TimeT T = new TimeT( startTime );
    ps.println( "  Start time:                  " + T.toString() );
    T = new TimeT( getCurrentEpochTime() );
    ps.println( "  Current time:                " + T.toString() );
    T = new TimeT( getEpochEndTime() );
    ps.println( "  End time:                    " + T.toString() );
    ps.println( "  Number of samples:           " + totalNumSamples );
    ps.println( "  Current sample index:        " + nextSample );
    ps.println( "  Number of samples available: " + numSamplesRemaining );
  }

}
