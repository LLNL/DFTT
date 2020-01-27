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
