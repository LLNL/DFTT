package llnl.gnem.apps.detection.cancellation;


import com.oregondsp.io.SACFileWriter;
import com.oregondsp.io.SACHeader;
import com.oregondsp.util.TimeStamp;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;


import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;


public class CancelledStreamWriter implements Callable< Boolean > {
  
  private final SegmentedCancellor cancellor;
  private final SACFileWriter[]    writers;
  private boolean                  firstSegment;   
  
  
  public CancelledStreamWriter( SegmentedCancellor cancellor, DataServer server, String outputPath )  {
    
    this.cancellor  = cancellor;
    ChannelID[] ids = cancellor.getChannelIDs();
    
    writers = new SACFileWriter[ ids.length ];
    for ( int ich = 0;  ich < ids.length;  ich++ ) try {
        
        writers[ich] = new SACFileWriter( outputPath + File.separator + ids[ich].getStation() + "." + ids[ich].getComponent() );
        SACHeader header = writers[ich].getHeader();
        header.b      = 0.0f;
        header.delta  = (float) server.getDelta();
        header.kstnm  = ids[ich].getStation();
        header.kcmpnm = ids[ich].getComponent();
        
    } catch (IOException ex) {
        Logger.getLogger(CancelledStreamWriter.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    firstSegment = true;
  }


  @Override
  public Boolean call() throws Exception {

    while (true) {
        
      StreamSegment segment = cancellor.take();
      
      if ( firstSegment ) {
          for ( SACFileWriter writer : writers ) {
              writer.getHeader().setStartTime( new TimeStamp(segment.getStartTime().getEpochTime()) );
          }
          firstSegment = false;
      }
      
      if ( segment.size() > 0 ) {
        for ( int i = 0;  i < writers.length;  i++ ) {
          writers[i].writeFloatArray( segment.getChannelData( i ) );
        }
      }
      else {
        for ( SACFileWriter writer : writers ) writer.close();
        return true;
      }
    }
    

  }

}
