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
