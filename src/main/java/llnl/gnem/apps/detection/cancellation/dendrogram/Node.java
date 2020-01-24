// Copyright (c) 2014 Deschutes Signal Processing LLC
// Author:  Dave Harris

package llnl.gnem.apps.detection.cancellation.dendrogram;

import java.io.PrintStream;
import java.util.ArrayList;


public class Node {
  
  Node  parent;
  Node  child1;
  Node  child2;
  float linkValue;
  
  
  public Node( Node parent, Node child1, Node child2, float linkValue ) {
    this.parent    = parent;
    this.child1    = child1;
    this.child2    = child2;
    this.linkValue = linkValue;
  }
  
  
  
  public Node root( float threshold ) {
    
    Node retval = null;
    if ( parent == null ) {
      retval = this;
    }
    else {
      if ( parent.linkValue < threshold ) 
        retval = this;
      else
        retval = parent.root( threshold );
    }
    
    return retval;
  }
  
  
  
  ArrayList< Leaf > leaves() {
    
    ArrayList< Leaf > retval = new ArrayList<   >();
    retval.addAll( child1.leaves() );
    retval.addAll( child2.leaves() );
    
    return retval;
  }
  
  
  
  void print( PrintStream ps, String indentation ) {
    ps.println( indentation + linkValue );
    child1.print(  ps, indentation + "   " );
    child2.print(  ps, indentation + "   " );
  }
  

}
