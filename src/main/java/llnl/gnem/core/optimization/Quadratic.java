//                                                         Quadratic.java
//
//  Copyright (c) 2001 Regents of the University of California
//
//  Author:  Dave Harris
//
//  Created:        July 23, 2001
//  Last Modified:  July 26, 2001
//
package llnl.gnem.core.optimization;

public class Quadratic implements ObjectiveFunction {

    private float x0, y0;
    private float a00, a01, a11, b0, b1, c;

    public Quadratic(float _x0, float _y0,
            float _a00, float _a01, float _a11, float _b0, float _b1, float _c) {
        x0 = _x0;
        y0 = _y0;
        a00 = _a00;
        a01 = _a01;
        a11 = _a11;
        b0 = _b0;
        b1 = _b1;
        c = _c;
    }

    @Override
    public int dimension() {
        return 2;
    }

    @Override
    public double evaluate(float[] parameters) {
        float x = parameters[0];
        x -= x0;
        float y = parameters[1];
        y -= y0;
        return a00 * x * x + 2.0f * a01 * x * y + a11 * y * y + b0 * x + b1 * y + c;
    }

    @Override
    public float[] gradient(float[] parameters) {
        float[] retval = null;
        return retval;
    }

}
