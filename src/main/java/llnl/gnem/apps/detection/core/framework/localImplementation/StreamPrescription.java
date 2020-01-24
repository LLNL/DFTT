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
