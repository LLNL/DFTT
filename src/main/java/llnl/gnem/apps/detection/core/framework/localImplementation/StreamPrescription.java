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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.oregondsp.util.DirectoryListing;

public class StreamPrescription {
  
  public String    streamName;
  public String    streamFilePath;           // path to directory containing stream files
  public String    filePattern;              // regex defining file names
  
  
  public StreamPrescription( String StreamSpecificationFile ) {
    
    Properties parameterList = new Properties();
    try {
      parameterList.load( new FileInputStream( StreamSpecificationFile ) );
      
      streamName     = parameterList.getProperty( "streamName",    "stream0" );
      streamFilePath = parameterList.getProperty( "streamFilePath"                 );
      filePattern    = parameterList.getProperty( "filePattern"                    );
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }
  
  
  
  public StreamPrescription( String streamName, String streamFilePath, String filePattern ) {
    this.streamName     = streamName;
    this.streamFilePath = streamFilePath;
    this.filePattern    = filePattern;
  }
  

  
  public String[] getFileList() {
    
    DirectoryListing D      = new DirectoryListing( streamFilePath, filePattern );
    String[]         retval = new String[ D.nFiles() ];
    for ( int i = 0;  i < D.nFiles();  i++ ) {
      retval[i] = streamFilePath + File.separator + D.file( i );
    }
    
    return retval;
  }
  
  
  
  public String getStreamName() {
    return streamName;
  }
  
  
}
