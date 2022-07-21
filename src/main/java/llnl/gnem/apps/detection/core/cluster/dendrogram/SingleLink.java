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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;


public class SingleLink extends AbstractLinker {
  
 
  
  public SingleLink( ArrayList< SimilarityMeasure > measurements, float threshold, ArrayList< Object > objects ) {
    
    Collections.sort( measurements );
    Collections.reverse( measurements );
    
    // make leaves for objects 
    
    leaves = new ArrayList<  >();
    map    = new HashMap<  >( objects.size() );
    for ( Object O : objects ) {
      Leaf L = new Leaf( null, O );
      leaves.add( L );
      map.put( O, L );
    }
    
    // create dendrogram(s) from similarity measurements
    
    for ( SimilarityMeasure measurement : measurements ) {
      
      if ( measurement.getValue() < threshold ) break;
      
      // check to see if roots of the objects are the same
      
      Leaf A     = (Leaf) map.get( measurement.getObject1() );
      Leaf B     = (Leaf) map.get( measurement.getObject2() );
      if( A == null || B == null){
          continue;
      }
      Node rootA = A.root( threshold );
      Node rootB = B.root( threshold );
      
      if ( rootA != rootB ) {   // add new link if not the same
        
        // add offset to all leaves in B's group
        
        float offset = measurement.getDelay() + A.delay - B.delay;
        
        ArrayList< Leaf > Bgroup = rootB.leaves();
        Bgroup.stream().forEach((Bgroup1) -> {
            Bgroup1.delay += offset;
        });
        
        // link two groups together
        
        Node newRoot = new Node( null, rootA, rootB, measurement.getValue() );
        rootA.parent = newRoot;
        rootB.parent = newRoot;
      }
      
    }
    
  }
  
 

}
