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
package llnl.gnem.core.signalprocessing.arrayProcessing;

/**
 * Copyright (c) 2005  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Dec 7, 2005
 * Time: 11:10:34 AM
 * Last Modified: Dec 14, 2005
 *
 *             |                                           | 2
 * computes:   | sum   z   exp( -j*omega*(sn*xn + se*xe ) )|
 *             |  i     i                      i       i   |
 *
 *                                  smax
 *     start here      o   x   x   x | x   x   x   x  -> evaluate in + horizontal direction
 *                                                       return in scanned order in this direction
 *                     x   x   x   x | x   x   x   x
 *
 *                     x   x   x   x | x   x   x   x
 *
 *                     x   x   x   x | x   x   x   o      end here
 *             -smax  -------------------------------  smax
 *                     x   x   x   x | x   x   x   x
 *
 *                     x   x   x   x | x   x   x   x  lower part obtained by symmetry
 *
 *                     x   x   x   x | x   x   x   x
 *
 *                     x   x   x   x | x   x   x   x
 *                                 -smax
 */

import java.util.Arrays;


public class FKCartesian {


    /**
     * @return the fks
     */
    public float[] getFks() {
        return fks;
    }

    private final float[] xn;          // parameters defining the FK spectrum calculation, grid etc.
    private final float[] xe;
    private final float smax;
    private final int ns;
    private final float ds;
    private final int nc;

    private final float[] C;          // complex exponential factors for the upper left corner of the FK spectrum
    private final float[] S;

    private final float[] dCn;        // incremental complex exponential factors used for vertical (downward) update
    private final float[] dSn;

    private final float[] dCe;        // incremental complex exponential factors used for horizontal (left to right) update
    private final float[] dSe;

    // evaluation space

    private final float[] W0r;
    private final float[] W0i;

    private final float[] Wr;
    private final float[] Wi;

    private final float[] fks;


    public FKCartesian( float smax, int ns, float[] xnorth, float[] xeast )
    {

        xn = xnorth;
        xe = xeast;
        nc = xn.length;
        this.smax = smax;
        this.ns = ns;
        ds = 2 * smax / ( (float) ( ns - 1 ) );

        C = new float[ nc ];
        S = new float[ nc ];
        dCn = new float[ nc ];
        dSn = new float[ nc ];
        dCe = new float[ nc ];
        dSe = new float[ nc ];

        // evaluation space

        W0r = new float[nc];
        W0i = new float[nc];

        Wr = new float[nc];
        Wi = new float[nc];

        fks = new float[ ns * ns ];
    }


    public void initialize( float omega )
    {

        double arg;
        for ( int i = 0; i < nc; i++ ){
            arg = -( xn[i] - xe[i] ) * smax * omega;
            C[i] = (float) Math.cos( arg );
            S[i] = (float) Math.sin( arg );

            arg = xn[i] * ds * omega;
            dCn[i] = (float) Math.cos( arg );
            dSn[i] = (float) Math.sin( arg );

            arg = -xe[i] * ds * omega;
            dCe[i] = (float) Math.cos( arg );
            dSe[i] = (float) Math.sin( arg );
        }

    }

    public float[] getBeamPattern( double frequency )
    {
        initialize( (float) ( Math.PI * 2 * frequency ) );
        float[] Zr = new float[ xn.length];
        Arrays.fill( Zr, 1.0f );
        float[] Zi = new float[ xn.length ];
        Arrays.fill( Zi, 0.0f );
        return evaluate( Zr, Zi );
    }

    public float[] evaluate( float[] Zr, float[] Zi )
    {

        int ptr = 0;
        int iptr = ns * ns - 1;
        int icolumn = 0;
        float ZrWr, ZrWi, ZiWr, ZiWi;

        for ( int i = 0; i < nc; i++ ){
            W0r[i] = C[i];
            W0i[i] = S[i];
            Wr[i] = W0r[i];
            Wi[i] = W0i[i];
        }

        while( ptr <= iptr ){

            ZrWr = 0.0f;
            ZrWi = 0.0f;
            ZiWr = 0.0f;
            ZiWi = 0.0f;
            for ( int i = 0; i < nc; i++ ){
                ZrWr += Zr[i] * Wr[i];
                ZrWi += Zr[i] * Wi[i];
                ZiWr += Zi[i] * Wr[i];
                ZiWi += Zi[i] * Wi[i];
            }
            float R, I;
            R = ZrWr - ZiWi;
            I = ZrWi + ZiWr;
            fks[ptr] = R * R + I * I;
            R = ZrWr + ZiWi;
            I = -ZrWi + ZiWr;
            fks[iptr] = R * R + I * I;

            icolumn++;
            ptr++;
            iptr--;

            if( icolumn < ns ){
                CoupledFormOscillator(Wr, Wi, dCe, dSe );        // Steering vector update in horizontal direction
            }
            else{
                icolumn = 0;
                CoupledFormOscillator(W0r, W0i, dCn, dSn );      // Steering vector update in vertical direction
                for ( int i = 0; i < nc; i++ ){
                    Wr[i] = W0r[i];
                    Wi[i] = W0i[i];
                }
            }

        }

        return getFks();
    }


    private void CoupledFormOscillator( float[] WC, float[] WS, float[] dC, float[] dS )
    {
        float tmp;
        for ( int i = 0; i < nc; i++ ){
            tmp = WC[i] * dC[i] - WS[i] * dS[i];
            WS[i] = WC[i] * dS[i] + WS[i] * dC[i];
            WC[i] = tmp;
        }
    }



}