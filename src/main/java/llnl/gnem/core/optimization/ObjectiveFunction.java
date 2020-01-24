//                                                               ObjectiveFunction.java
//
//  Copyright (c) 2001  Regents of the University of California
//
//  Author:  Dave Harris
//
//  Created:        July 21, 2001
//  Last Modified:  July 21, 2001 
//

package llnl.gnem.core.optimization;

public interface ObjectiveFunction {

    public int dimension();

    public double evaluate( float[] parameters );

    public float[] gradient( float[] parameters );

}
