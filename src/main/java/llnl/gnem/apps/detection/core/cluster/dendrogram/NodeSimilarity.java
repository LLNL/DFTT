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
package llnl.gnem.apps.detection.core.cluster.dendrogram;

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
