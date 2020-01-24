//                                           NelderMead.java
//
//  Copyright (c) 2001 Regents of the University of California
//
//  Author:  Dave Harris
//
//  Created:        July 21, 2001
//  Last Modified:  July 23, 2001
//
package llnl.gnem.core.optimization;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observer;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.gui.util.ProgressBarMessage;

public class NelderMead extends OptimizationTechnique {

    public double getValueSpread() {
        return valueSpread;
    }

    // inner Vertex class
    public class Vertex implements Comparable {

        private float[] parameters;
        private double objectiveValue;

        // constructors
        public Vertex() {
            parameters = new float[parameterDimension];
            objectiveValue = -1.0;
        }

        public Vertex(float[] _parameters) {
            parameters = new float[parameterDimension];
            for (int i = 0; i < parameterDimension; i++) {
                parameters[i] = _parameters[i];
            }
            objectiveValue = -1.0;
        }

        public Vertex(Vertex V) {
            parameters = new float[parameterDimension];
            for (int i = 0; i < parameterDimension; i++) {
                parameters[i] = V.parameters[i];
            }
            objectiveValue = V.objectiveValue;
        }

        public void setParameters(float[] newParameters) {
            parameters = newParameters;
            setObjectiveValue(-1.0);
        }

        public void evaluate() {
            setObjectiveValue(objective.evaluate(getParameters()));
        }

        //  arithmetic operators:
        public void zero() {
            for (int i = 0; i < parameterDimension; i++) {
                parameters[i] = 0.0f;
            }
        }

        public Vertex plus(Vertex V) {
            float[] sumParameters = new float[parameterDimension];
            for (int i = 0; i < parameterDimension; i++) {
                sumParameters[i] = getParameters()[i] + V.getParameters()[i];
            }
            Vertex Vnew = new Vertex();
            Vnew.setParameters(sumParameters);
            return Vnew;
        }

        public void plusEquals(Vertex V) {
            for (int i = 0; i < parameterDimension; i++) {
                parameters[i] += V.getParameters()[i];
            }
        }

        public Vertex minus(Vertex V) {
            float[] difParameters = new float[parameterDimension];
            for (int i = 0; i < parameterDimension; i++) {
                difParameters[i] = getParameters()[i] - V.getParameters()[i];
            }
            Vertex Vnew = new Vertex();
            Vnew.setParameters(difParameters);
            return Vnew;
        }

        public Vertex multiplyBy(float a) {
            float[] scaledParameters = new float[parameterDimension];
            for (int i = 0; i < parameterDimension; i++) {
                scaledParameters[i] = a * getParameters()[i];
            }
            Vertex Vnew = new Vertex();
            Vnew.setParameters(scaledParameters);
            return Vnew;
        }

        public Vertex divideBy(float a) {
            float[] scaledParameters = new float[parameterDimension];
            for (int i = 0; i < parameterDimension; i++) {
                scaledParameters[i] = getParameters()[i] / a;
            }
            Vertex Vnew = new Vertex();
            Vnew.setParameters(scaledParameters);
            return Vnew;
        }

        public double distance(Vertex other) {
            double sum = 0;
            for (int j = 0; j < parameters.length; ++j) {
                double del = parameters[j] - other.parameters[j];
                sum += del * del;
            }
            return Math.sqrt(sum) / parameters.length;
        }

        public void print(PrintStream ps) {
            ps.println("objective:  " + getObjectiveValue());
            ps.println("parameters:  ");
            for (int i = 0; i < parameterDimension; i++) {
                ps.println("  " + getParameters()[i]);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parameterDimension; i++) {
                sb.append(String.format("p(%d) = %f  ", i, parameters[i]));
            }
            sb.append(String.format(",Res = %f", objectiveValue));
            return sb.toString();
        }

        // method for Comparable implementation
        @Override
        public int compareTo(Object o) {
            if (getObjectiveValue() < ((Vertex) o).getObjectiveValue()) {
                return -1;
            } else if (getObjectiveValue() == ((Vertex) o).getObjectiveValue()) {
                return 0;
            } else {
                return 1;
            }
        }

        public float[] getParameters() {
            return parameters;
        }

        public double getObjectiveValue() {
            return objectiveValue;
        }

        public void setObjectiveValue(double objectiveValue) {
            this.objectiveValue = objectiveValue;
        }

        private boolean isInside(Dimension[] dimensions) {
            for (int j = 0; j < parameters.length; ++j) {
                if (!dimensions[j].contains(parameters[j])) {
                    return false;
                }
            }
            return true;
        }

        private void constrainTo(Dimension[] dimensions) {
            for (int j = 0; j < parameters.length; ++j) {
                if (!dimensions[j].contains(parameters[j])) {
                    parameters[j] = (float) dimensions[j].nearestBoundary(parameters[j]);
                }
            }
        }
    }

    public class Simplex {

        public Vertex[] vertices;
        private int count;

        public void printVertices() {
            for (Vertex v : vertices) {
                ApplicationLogger.getInstance().log(Level.FINEST, v.toString());
            }
            ApplicationLogger.getInstance().log(Level.FINEST, "");
        }

        public double getMaxVertexSeparation() {
            double max = 0;
            for (int j = 1; j < vertices.length; ++j) {
                double dist = vertices[0].distance(vertices[j]);
                if (dist > max) {
                    max = dist;
                }
            }
            return max;
        }

        public Simplex() {
            vertices = new Vertex[parameterDimension + 1];
            count = 0;
        }

        public void clear() {
            count = 0;
        }

        public void sort() {
            if (count == parameterDimension + 1) {
                Arrays.sort(vertices);
            }
        }

        public void add(Vertex V) {
            vertices[count] = V;
            count++;
        }

        public int size() {
            return count;
        }

        public void evaluate() {
            for (int i = 0; i < count; i++) {
                vertices[i].evaluate();
            }
        }

        public void print(PrintStream ps) {
            for (int i = 0; i < parameterDimension + 1; i++) {
                ps.println("Vertex " + i);
                vertices[i].print(ps);
            }
        }
    }
    /**
     * **********************************************************
     * NelderMead class variables and methods *
     * ***********************************************************
     */
    private Simplex simplex = null;
    private final float alpha = 1.0f;
    private final float beta = 0.5f;
    private final float gamma = 2.0f;
    private double valueSpread;
    private final int nD = parameterDimension;
    private Dimension[] dimensions = null;
    private Observer iterationObserver;

    public NelderMead(ObjectiveFunction OF,
            double _tolerance,
            int _maxIterations) {
        super(OF, _tolerance, _maxIterations);
        simplex = new Simplex();
        iterationObserver = null;
    }

    @Override
    public void initialize(float[] initialGuess, float problemScale) {

        // create simplex
        dimensions = null;
        iterationCount = 0;
        simplex.clear();
        simplex.add(new Vertex(initialGuess));
        for (int i = 0; i < nD; i++) {
            initialGuess[i] += problemScale;
            simplex.add(new Vertex(initialGuess));
            initialGuess[i] -= problemScale;
        }

    }

    public void setUp(float[] initialGuess, Dimension[] dimensions) {
        iterationCount = 0;
        simplex.clear();
        simplex.add(new Vertex(initialGuess));
        for (int i = 0; i < nD; i++) {
            float problemScale = (float) dimensions[i].getLengthScale();
            if (!dimensions[i].contains(initialGuess[i] + problemScale)) {
                problemScale = (float) dimensions[i].nearestBoundary(initialGuess[i] + problemScale);
            }
            float tmp = initialGuess[i];
            initialGuess[i] += problemScale;
            if (!dimensions[i].contains(initialGuess[i])) {
                initialGuess[i] = (float) dimensions[i].getMaximum();
            }
            simplex.add(new Vertex(initialGuess));
            initialGuess[i] = tmp;
        }
        this.dimensions = dimensions.clone();
    }

    public void setUp(ArrayList<float[]> initialVertices, Dimension[] dimensions) {
        iterationCount = 0;
        simplex.clear();
        for (float[] vertex : initialVertices) {
            simplex.add(new Vertex(vertex));
        }
        this.dimensions = dimensions.clone();
    }

    public void setIterationObserver(Observer obs) {
        iterationObserver = obs;
    }

    public Vertex getResult() {
        double minResidual = Double.MAX_VALUE;
        Vertex result = simplex.vertices[0];
        for (Vertex v : simplex.vertices) {
            if (v.getObjectiveValue() < minResidual) {
                minResidual = v.getObjectiveValue();
                result = v;
            }
        }
        return result;
    }

    @Override
    public void run() {


        Vertex vCenter = new Vertex();
        Vertex vNear, vFar, vMid;
        Vertex[] vertices = simplex.vertices;


        /*
         * initialization - calculate the values of the objective function at
         * each of the vertices, and rank the vertices in order of increasing
         * objective function value
         */

        simplex.evaluate();

        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            simplex.sort();
            simplex.printVertices();

// test for completion - two types:  maximum number of iterations
// exceeded or successful contraction of the simplex

            double tmp = 2.0 * Math.abs((vertices[nD].getObjectiveValue() - vertices[0].getObjectiveValue()))
                    / Math.abs((vertices[nD].getObjectiveValue()) + Math.abs(vertices[0].getObjectiveValue()));
            valueSpread = Math.min(simplex.getMaxVertexSeparation(), tmp);

            if (valueSpread < tolerance) {
                return;
            }
            if (iterationCount > maxIterations) {
                return;
            }
            iterationCount++;


// start of new iteration

//   calculate centroid of simplex face opposite vertex with maximum
//     objective function value

            vCenter.zero();
            for (int i = 0; i < nD; i++) {
                vCenter.plusEquals(vertices[i]);
            }
            vCenter = vCenter.divideBy((float) nD);

//   first trial point along the vector from the high vertex through
//     the centroid (reflected through the centroid)

            vNear = (vCenter.multiplyBy(1.0f + alpha)).minus(vertices[nD].multiplyBy(alpha));
            if (dimensions != null) {
                if (!vNear.isInside(dimensions)) {
                    vNear.constrainTo(dimensions);
                }
            }
            vNear.evaluate();

//  if near value is lower, extend the trial point further along
//    this vector

            if (vNear.getObjectiveValue() < vertices[0].getObjectiveValue()) {

//  try a further point

                vFar = (vNear.multiplyBy(gamma)).plus(vCenter.multiplyBy(1.0f - gamma));
                if (dimensions != null) {
                    if (!vFar.isInside(dimensions)) {
                        vFar.constrainTo(dimensions);
                    }
                }
                vFar.evaluate();

//  if it succeeds, use it, and if not, use the near one

                if (vFar.getObjectiveValue() < vertices[0].getObjectiveValue()) {
                    vertices[nD] = vFar;
                } else {
                    vertices[nD] = vNear;
                }

            } // if the reflected point is worse than the second highest
            else if (vNear.getObjectiveValue() > vertices[nD - 1].getObjectiveValue()) {

                if (vNear.getObjectiveValue() < vertices[nD].getObjectiveValue()) {  // but better than the
                    vertices[nD] = vNear;                                        // highest, replace it
                }

// check an intermediate point, contracting along one dimension

                vMid = (vertices[nD].multiplyBy(beta)).plus(vCenter.multiplyBy(1.0f - beta));
                vMid.evaluate();
                if (vMid.getObjectiveValue() < vertices[nD].getObjectiveValue()) {   // accept contraction
                    vertices[nD] = vMid;
                } else {                                                         // contract around low point
                    for (int i = 1; i <= nD; i++) {
                        vertices[i] = (vertices[i].plus(vertices[0])).divideBy(2.0f);
                        vertices[i].evaluate();
                    }
                }
            } else {           // replace old high point with reflection
                vertices[nD] = vNear;
            }
            if (iterationObserver != null) {
                ProgressBarMessage msg = new ProgressBarMessage(iterationCount, maxIterations, true, false);
                iterationObserver.update(null, msg);
            }

        }  // end of while loop

    }

    @Override
    public float[] parameters() {
        return simplex.vertices[0].getParameters();
    }

    @Override
    public double residual() {
        return simplex.vertices[0].getObjectiveValue();
    }
}
