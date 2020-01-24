// Copyright (c) 2014 Deschutes Signal Processing LLC
// Author:  Dave Harris

package llnl.gnem.apps.detection.cancellation.dendrogram;


public class SimilarityMeasure implements Comparable <SimilarityMeasure> {
  
  
  protected Object O1;
  protected Object O2;
  protected float  value;
  protected float  delay;
  
  
  
  public SimilarityMeasure ( Object O1, Object O2 ) {
    this.O1 = O1;
    this.O2 = O2;
  }
  
  
  
  public SimilarityMeasure( Object O1, Object O2, float value, float delay ) {
    this.O1 = O1;
    this.O2 = O2;
    this.value = value;
    this.delay = delay;
  }
  
  

  public Object getObject1() {
    return O1;
  }
  
  
  
  public Object getObject2() {
    return O2;
  }
  
  
  
  public float  getValue() {
    return value;
  }
  
  
  
  public float  getDelay() {
    return delay;
  }

  
  
  @Override
  public int compareTo( SimilarityMeasure arg ) {
    
    if ( ( (SimilarityMeasure) arg ).value > value )
      return -1;
    else if ( ( (SimilarityMeasure) arg).value < value ) 
      return 1;
    else
      return 0;
    
  }
  
  
  
  @Override
  public String toString() {
    return O1 + "  *  " + O2 + "  " + value + "  " + delay;
  }

}
