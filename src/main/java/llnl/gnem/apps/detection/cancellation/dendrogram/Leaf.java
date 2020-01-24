// Copyright (c) 2014 Deschutes Signal Processing LLC
// Author:  Dave Harris

package llnl.gnem.apps.detection.cancellation.dendrogram;

import java.io.PrintStream;
import java.util.ArrayList;


public class Leaf extends Node {
  
  Object O;
  float  delay;
  
  
  
  public Leaf( Node parent, Object O ) {
    
    super( parent, null, null, 0.0f );
    
    this.O = O;
    delay  = 0.0f;
    
  }

  
  
  @Override
  ArrayList< Leaf > leaves() {
    
    ArrayList< Leaf > retval = new ArrayList<  >();
    retval.add( this );
    
    return retval;
  }
  
  
  
  @Override
  void print( PrintStream ps, String indentation ) {
    ps.println( indentation + O.toString() + "  " + delay );
  }
  
}
