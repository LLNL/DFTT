//                                                       OptimizationTechnique.java
//
//  Copyright (c) 2001  Regents of the University of California
//
//  Author:  Dave Harris
//
//  Created:        July 21, 2001
//  Last Modified:  July 23, 2001 
//

package llnl.gnem.core.optimization;

public abstract class OptimizationTechnique {

    protected ObjectiveFunction objective;
    protected double tolerance;
    protected int maxIterations;
    protected int iterationCount;
    protected int parameterDimension;

    public OptimizationTechnique( ObjectiveFunction OF,
                                  double _tolerance,
                                  int _maxIterations )
    {
        objective = OF;
        tolerance = _tolerance;
        maxIterations = _maxIterations;
        iterationCount = 0;
        parameterDimension = OF.dimension();
    }

    public abstract void initialize( float[] initialGuess, float problemScale );

    public abstract void run();

    public abstract float[] parameters();

    public abstract double residual();
    
    public int getIterations()
    {
        return iterationCount;
    }

}
