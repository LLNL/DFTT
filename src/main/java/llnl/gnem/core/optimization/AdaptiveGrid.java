//                                           AdaptiveGrid.java
//
//  Copyright (c) 2001 Regents of the University of California
//
//  Author:  Dave Harris
//
//  Created:        July 25, 2001
//  Last Modified:  July 26, 2001
//

package llnl.gnem.core.optimization;

import java.util.Arrays;
import java.io.PrintStream;
import java.lang.Comparable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class AdaptiveGrid extends OptimizationTechnique {

    Grid grid;
    double[] objectiveValues;
    int bestIndex;
    float contractionFactor;
    boolean marginCheck = true;
    boolean minimize = true;

    public AdaptiveGrid( ObjectiveFunction OF,
                         double _tolerance,
                         int _maxIterations,
                         int[] nx,
                         float[] dx )
    {
        super( OF, _tolerance, _maxIterations );
        float[] x = new float[parameterDimension];
        grid = new Grid( parameterDimension, x, dx, nx );
        objectiveValues = new double[grid.size()];
        bestIndex = -1;
        contractionFactor = 2.0f;
        marginCheck = true;
        minimize = true;
    }


    public void initialize( float[] initialGuess, float problemScale )
    {
        grid.recenter( initialGuess );
        contractionFactor = problemScale;
    }


    public void suppressMarginCheck()
    {
        marginCheck = false;
    }


    public void run()
    {

        int iterationCount = 0;
        double bestValue = 1000000.0;
        int nGrid = grid.size();
        double previousBest = 0.0;
        double ratio;
        float[] params;

        while( true ) {

            // evaluate objective function on grid, find best value

            for ( int i = 0; i < nGrid; i++ ) {
                objectiveValues[i] = objective.evaluate( grid.get( i ) );
                if( i == 0 ) {
                    bestValue = objectiveValues[0];
                    bestIndex = 0;
                }
                else {
                    if( minimize ) {
                        if( objectiveValues[i] < bestValue ) {
                            bestIndex = i;
                            bestValue = objectiveValues[i];
                        }
                    }
                    else {
                        if( objectiveValues[i] > bestValue ) {
                            bestIndex = i;
                            bestValue = objectiveValues[i];
                        }
                    }
                }
            }

            // check for best value on margins of grid, move and recompute grid if found

            if( marginCheck ) {

                while( !grid.interior( bestIndex ) ) {

                    grid.recenter( grid.get( bestIndex ) );
                    for ( int i = 0; i < nGrid; i++ ) {
                        objectiveValues[i] = objective.evaluate( grid.get( i ) );
                        if( i == 0 ) {
                            bestValue = objectiveValues[0];
                            bestIndex = 0;
                        }
                        else {
                            if( minimize ) {
                                if( objectiveValues[i] < bestValue ) {
                                    bestIndex = i;
                                    bestValue = objectiveValues[i];
                                }
                            }
                            else {
                                if( objectiveValues[i] > bestValue ) {
                                    bestIndex = i;
                                    bestValue = objectiveValues[i];
                                }
                            }
                        }
                    }

                }

            }

            iterationCount++;

            // test for completion

            ratio = 2.0 * Math.abs( bestValue - previousBest ) / ( bestValue + previousBest );

            System.out.println( "" + iterationCount + "  " + ratio );

            if( iterationCount >= maxIterations ) return;
            if( ratio <= tolerance ) return;

            grid.contract( contractionFactor );
            previousBest = bestValue;

        }

    }


    public float[] parameters()
    {
        return grid.get( bestIndex );
    }


    public double residual()
    {
        return objectiveValues[bestIndex];
    }


    public void maximize()
    {
        minimize = false;
    }


    public void minimize()
    {
        minimize = true;
    }


    public void writeToFile( String filename )
            throws IOException, FileNotFoundException
    {

        PrintStream out = new PrintStream( new FileOutputStream( filename ) );
        float[] params;

        for ( int i = 0; i < grid.size(); i++ ) {
            params = grid.get( i );
            for ( int j = 0; j < params.length; j++ ) {
                out.print( "  " + params[j] );
            }
            out.println( "  " + objectiveValues[i] );
        }
        out.close();
    }

}

