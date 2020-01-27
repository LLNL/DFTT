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
package llnl.gnem.core.signalprocessing.filter;


public class HilbertIIR {


    private float x1, x2, x3;
    private float y, y1, y2;
    private float p, p1, p2;
    private float q, q1, q2;
    private float w, w1, w2;

    private float out1, out2;


    public HilbertIIR()
    {
        clearstates();
    }                  // constructor



// evaluation operators


    public void clearstates()
    {
        x1 = 0.0f;
        x2 = 0.0f;
        x3 = 0.0f;
        y = 0.0f;
        y1 = 0.0f;
        y2 = 0.0f;
        p = 0.0f;
        p1 = 0.0f;
        p2 = 0.0f;
        q = 0.0f;
        q1 = 0.0f;
        q2 = 0.0f;
        w = 0.0f;
        w1 = 0.0f;
        w2 = 0.0f;
    }


    public void singlestep( float x )
    {
        y = 0.94167f * ( y2 - x1 ) + x3;
        w = 0.53239f * ( w2 - y ) + y2;
        w2 = w1;
        w1 = w;
        y2 = y1;
        y1 = y;

//

        p = 0.186540f * ( p2 - x ) + x2;
        q = 0.7902015f * ( q2 - p ) + p2;
        p2 = p1;
        p1 = p;
        q2 = q1;
        q1 = q;

//

        x3 = x2;
        x2 = x1;
        x1 = x;

//

        out1 = w;
        out2 = q;
    }


    public float getInPhase()
    {
        return out1;
    }


    public float getQuadrature()
    {
        return out2;
    }

}

