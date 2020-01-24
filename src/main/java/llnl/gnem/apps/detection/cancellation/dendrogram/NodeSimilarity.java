// Copyright (c) 2014 Deschutes Signal Processing LLC
// Author:  Dave Harris

package llnl.gnem.apps.detection.cancellation.dendrogram;

public class NodeSimilarity implements Comparable< NodeSimilarity > {
  
  private final Node  node1;
  private final Node  node2;
  private final float value;
  
  

  public NodeSimilarity( Node node1, Node node2, float value ) {
    this.node1 = node1;
    this.node2 = node2;
    this.value = value;
  }
  
  
  
  public Node getNode1() {
    return node1;
  }  
  
  
  
  public Node getNode2() {
    return node2;
  }
  
  
  
  public float getValue() {
    return value;
  }

  
  
  public boolean contains( Node n ) {
    if ( n == node1  ||  n == node2 ) 
      return true;
    else 
      return false;
  }
  
  
  
  public Node otherNode( Node n ) {
    if ( n == node1 ) 
      return node2;
    else if ( n == node2 ) 
      return node1;
    else
      return null;
  }
  
  
  
  @Override
  public int compareTo( NodeSimilarity arg ) {
    
    if ( ( (NodeSimilarity) arg ).value > value )
      return -1;
    else if ( ( (NodeSimilarity) arg).value < value ) 
      return 1;
    else
      return 0;
    
  }

}
