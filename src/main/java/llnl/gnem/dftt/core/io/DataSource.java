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

import java.io.PrintStream;

/**
 * Copyright (c) 2003  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Jan 25, 2004
 * Time: 1:11:12 PM
 * Last Modified: Jan 25, 2004
 */



public interface DataSource {

  public void   getData( float[] dataArray );

  public void   getData( float[] dataArray, int offset, int numSamples );

  public void   skipSamples( long numSamples );

  public long   getTotalNumSamples();

  public long   getNumSamplesAvailable();

  public long   getNextSampleIndex();

  public String getChannel();

  public String getStation();

  public double getSamplingRate();

  public double getEpochStartTime();

  public double getEpochEndTime();

  public double getCurrentEpochTime();

  public void   initiate();

  public void   close();

  public void   print( PrintStream ps );


}
