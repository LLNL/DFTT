// Copyright (c) 2014 Deschutes Signal Processing LLC
// Author:  Dave Harris

package llnl.gnem.apps.detection.cancellation.dendrogram;

import java.io.PrintStream;
import java.util.ArrayList;

public interface HierarchicalLinker {

  
  public ArrayList< Object[] > getClusters( float threshold ) ;
  
  public float getDelay( Object O );
   
  public void print( PrintStream ps, float threshold );
  
}
