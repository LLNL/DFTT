
package llnl.gnem.apps.detection.cancellation;

import java.util.concurrent.Callable;

import java.io.PrintStream;
import llnl.gnem.apps.detection.cancellation.io.ChannelID;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;

public interface SegmentedCancellor extends Callable< Void > {
  
  public void          put( StreamSegment segment ) throws InterruptedException ;
  
  public StreamSegment take() throws InterruptedException;
  
  public int           getNumTemplates();
  
  public ChannelID[]   getChannelIDs();
  
  public void          processSegment();
  
  public void          print( PrintStream ps );
  
  public void          shutdown();

}