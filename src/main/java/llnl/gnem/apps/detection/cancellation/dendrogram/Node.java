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
