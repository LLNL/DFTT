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
