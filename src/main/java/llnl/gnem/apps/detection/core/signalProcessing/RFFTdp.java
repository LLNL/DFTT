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
package llnl.gnem.apps.detection.core.signalProcessing;

/**
*
* Author:  Dave Harris
* Created: Feb 13, 2008
* Time: 2:51:38 PM
* Last Modified: Feb 13, 2008
*/


public class RFFTdp extends FFTdp {


 public RFFTdp( int log2N ) {
   super( log2N );
 }


// The forward dft is an adaptation to Java of the Fortran routine
// described in this comment block with changes to incorporate sine 
// and cosine lookup tables  
/*=================================================================CC
CC                                                                 CC
CC  Subroutine RSRFFT(X,M):                                        CC
CC      A real-valued, in-place, split-radix FFT program           CC
CC      Decimation-in-time, cos/sin in second loop                 CC
CC      and computed recursively                                   CC
CC      Output in order:                                           CC
CC              [ Re(0),Re(1),....,Re(N/2),Im(N/2-1),...Im(1)]     CC
CC                                                                 CC
CC  Input/output                                                   CC
CC      X    Array of input/output (length >= N)                   CC
CC      M    Transform length is N=2**M                            CC
CC                                                                 CC
CC  Calls:                                                         CC
CC      RSTAGE,RBITREV                                             CC
CC                                                                 CC
CC  Author:                                                        CC
CC      H.V. Sorensen,   University of Pennsylvania,  Oct. 1985    CC
CC                       Arpa address: hvs@ee.upenn.edu            CC
CC  Modified:                                                      CC
CC      F. Bonzanigo,    ETH-Zurich,                  Sep. 1986    CC
CC      H.V. Sorensen,   University of Pennsylvania,  Mar. 1987    CC
CC      H.V. Sorensen,   University of Pennsylvania,  Oct. 1987    CC
CC                                                                 CC
CC  Reference:                                                     CC
CC      Sorensen, Jones, Heideman, Burrus :"Real-valued fast       CC
CC      Fourier transform algorithms", IEEE Tran. ASSP,            CC
CC      Vol. ASSP-35, No. 6, pp. 849-864, June 1987                CC
CC      Mitra&Kaiser: "Digital Signal Processing Handbook, Chap.   CC
CC      8, page 491-610, John Wiley&Sons, 1993                     CC
CC                                                                 CC
CC      This program may be used and distributed freely as         CC
CC      as long as this header is included                         CC
CC                                                                 CC
CC=================================================================*/

 public void dft( double[] x ) {

   bitReverse( x );

   length2Butterflies( x );

   // L-shaped Butterflies

   int n2 = 2;
   int its = N/4;
   for ( int k = 2;   k <= log2N;  k++ ) {
     n2 *= 2;
     int n4 = n2 / 4;
     rstage( n2, n4, its, x );
     its /= 2;
   }

 }



// rstage is an adaptation to Java of the Fortran routine
// described in this comment block with changes to incorporate sine 
// and cosine lookup tables  
/*===================================================================C
 C  Subroutine RSTAGE - the work-horse of the RFFT                   C
 C       Computes a stage of a real-valued split-radix length N      C
 C       transform.                                                  C
 C  Author                                                           C
 C       H.V. Sorensen,   University of Pennsylvania,  Mar. 1987     C
 C==================================================================*/

 private void rstage( int n2, int n4, int its, double[] x ) {

   int n8 = n2/8;
   int is = 0;
   int id  = n2*2;

   do {
     for ( int i0 = is;  i0 < N;  i0 += id ) {

       double t1;

       int i2 = i0 + 2*n4;
       int i3 = i2 + n4;

       t1    = x[i3] + x[i2];
       x[i3] = x[i3] - x[i2];
       x[i2] = x[i0] - t1;
       x[i0] = x[i0] + t1;
     }
     is = 2*id - n2;
     id *= 4;
   } while ( is < N );

   if ( n4 <= 1 ) return;

   is = 0;
   id = n2*2;
   do {

     for ( int i0 = is + n8;  i0 < N;  i0 += id ) {

       double t1, t2;
       int i1 = i0 + n4;
       int i2 = i1 + n4;
       int i3 = i2 + n4;

       t1     = ( x[i2] + x[i3] ) * SQRT2OVER2;
       t2     = ( x[i2] - x[i3] ) * SQRT2OVER2;

       x[i3] =  x[i1] - t1;
       x[i2] = -x[i1] - t1;
       x[i1] =  x[i0] - t2;
       x[i0] =  x[i0] + t2;
     }
     is  = 2*id - n2;
     id *= 4;
   } while ( is < N );

   if ( n8 <= 1 ) return;

   int it = 0;

   for ( int j = 2;  j <= n8;  j++ ) {

     it += its;

     is = 0;
     id = 2*n2;
     int jn = n4 - 2*j + 2;

     do {

       for ( int i0 = is + j - 1;  i0 <  N;  i0 += id ) {

         double t1, t2, t3, t4, t5;

         int i1 = i0 + n4;
         int i2 = i1 + n4;
         int i3 = i2 + n4;

         int j0 = i0 + jn;
         int j1 = j0 + n4;
         int j2 = j1 + n4;
         int j3 = j2 + n4;

         t1 = x[i2]*ct1[it] + x[j2]*st1[it];
         t2 = x[j2]*ct1[it] - x[i2]*st1[it];
         t3 = x[i3]*ct3[it] + x[j3]*st3[it];
         t4 = x[j3]*ct3[it] - x[i3]*st3[it];
         t5 = t1 + t3;
         t3 = t1 - t3;
         t1 = t2 + t4;
         t4 = t2 - t4;
         x[i2] =  t1    - x[j1];
         x[j3] =  t1    + x[j1];
         x[j2] = -x[i1] - t3;
         x[i3] =  x[i1] - t3;
         x[j1] =  x[i0] - t5;
         x[i0] =  x[i0] + t5;
         x[i1] =  x[j0] + t4;
         x[j0] =  x[j0] - t4;
       }

       is  = 2*id - n2;
       id *= 4;
     }  while ( is < N );

   }

   return;
 }



// The inverse dft is an adaptation to Java of the Fortran routine
// described in this comment block with changes to incorporate sine 
// and cosine lookup tables
/*=================================================================CC
CC                                                                 CC
CC  Subroutine IRSRFFT(X,M):                                       CC
CC      A inverse real-valued, in-place, split-radix FFT program   CC
CC      Decimation-in-frequency, cos/sin in second loop            CC
CC      and computed recursively                                   CC
CC      Symmetric input in order:                                  CC
CC              [ Re(0),Re(1),....,Re(N/2),Im(N/2-1),...Im(1)]     CC
CC      The output is real-valued                                  CC
CC                                                                 CC
CC  Input/output                                                   CC
CC      X    Array of input/output (length >= N)                   CC
CC      M    Transform length is N=2**M                            CC
CC                                                                 CC
CC  Calls:                                                         CC
CC      IRSTAGE, RBITREV                                           CC
CC                                                                 CC
CC  Author:                                                        CC
CC      H.V. Sorensen,   University of Pennsylvania,  Oct. 1985    CC
CC                       Arpa address: hvs@ee.upenn.edu            CC
CC  Modified:                                                      CC
CC      F. Bonzanigo,    ETH-Zurich,                  Sep. 1986    CC
CC      H.V. Sorensen,   University of Pennsylvania,  Mar. 1987    CC
CC                                                                 CC
CC  Reference:                                                     CC
CC      Sorensen, Jones, Heideman, Burrus :"Real-valued fast       CC
CC      Fourier transform algorithms", IEEE Tran. ASSP,            CC
CC      Vol. ASSP-35, No. 6, pp. 849-864, June 1987                CC
CC      Mitra&Kaiser: "Digital Signal Processing Handbook, Chap.   CC
CC      8, page 491-610, John Wiley&Sons, 1993                     CC
CC                                                                 CC
CC      This program may be used and distributed freely provided   CC
CC      this header is included and left intact                    CC
CC                                                                 CC
CC=================================================================*/
 public void idft( double[] x ) {

   // L-shaped Butterflies

   int n2 = 2*N;
   int its = 1;
   for ( int k = 1;  k <= log2N-1;  k++ ) {
     n2 /= 2;
     int n4 = n2/4;
     irstage( n2, n4, its, x );
     its *= 2;
   }

   length2Butterflies( x );

   bitReverse( x );

   // scale

   double scaleFactor = 1.0 / N ;
   for ( int i = 0;  i < N;  i++ ) x[i] *= scaleFactor;

 }



// irstage is an adaptation to Java of the Fortran routine
// described in this comment block with changes to incorporate sine 
// and cosine lookup tables  
 /*=================================================================CC
 CC                                                                 CC
 CC  Subroutine IRSTAGE - the work-horse of the IRFFT               CC
 CC       Computes a stage of an inverse real-valued split-radix    CC
 CC       length N transform.                                       CC
 CC  Author                                                         CC
 CC       H.V. Sorensen,   University of Pennsylvania,  Mar. 1987   CC
 CC                                                                 CC
 CC      This program may be used and distributed freely provided   CC
 CC      this header is included and left intact                    CC
 CC                                                                 CC
 CC=================================================================*/

 private void irstage( int n2, int n4, int its, double[] x ) {

   int n8 = n4/2;
   int is = 0;
   int id = n2*2;

   do {

     for ( int i0 = is;  i0 < N;  i0 += id ) {

       double t1, t2;
       int i1 = i0 + n4;
       int i2 = i1 + n4;
       int i3 = i2 + n4;

       t1    = x[i0] - x[i2];
       x[i0] = x[i0] + x[i2];
       x[i1] = 2*x[i1];
       t2    = 2*x[i3];
       x[i3] = t1 + t2;
       x[i2] = t1 - t2;
     }

     is = 2*id - n2;
     id *= 4;
   } while ( is < N );

   if ( n4 <= 1 ) return;

   is = 0;
   id = 2*n2;
   do {
     for ( int i0 = is + n8;  i0 < N;  i0 += id ) {

       double t1, t2;
       int i1 = i0 + n4;
       int i2 = i1 + n4;
       int i3 = i2 + n4;

       t1    = ( x[i1] - x[i0] )*SQRT2;
       t2    = ( x[i3] + x[i2] )*SQRT2;
       x[i0] = x[i0] + x[i1];
       x[i1] = x[i3] - x[i2];
       x[i2] = -t2 - t1;
       x[i3] = -t2 + t1;
     }

     is = 2*id - n2;
     id *= 4;
   } while ( is < N-1 );

   if ( n8 <= 1 ) return;

   int it = 0;
   for ( int j = 2;  j <= n8;  j++ ) {

     it += its;

     is = 0;
     id = 2*n2;
     int jn = n4 - 2*j + 2;

     do {
       for ( int i0 = is + j - 1;  i0 < N;  i0 += id ) {

         double t1, t2, t3, t4, t5;

         int i1 = i0 + n4;
         int i2 = i1 + n4;
         int i3 = i2 + n4;

         int j0 = i0 + jn;
         int j1 = j0 + n4;
         int j2 = j1 + n4;
         int j3 = j2 + n4;

         t1    = x[i0] - x[j1];
         x[i0] = x[i0] + x[j1];
         t2    = x[j0] - x[i1];
         x[j0] = x[i1] + x[j0];
         t3    = x[j3] + x[i2];
         x[j1] = x[j3] - x[i2];
         t4    = x[i3] + x[j2];
         x[i1] = x[i3] - x[j2];
         t5 = t1 - t4;
         t1 = t1 + t4;
         t4 = t2 - t3;
         t2 = t2 + t3;
         x[i2] =  t5*ct1[it] + t4*st1[it];
         x[j2] = -t4*ct1[it] + t5*st1[it];
         x[i3] =  t1*ct3[it] - t2*st3[it];
         x[j3] =  t2*ct3[it] + t1*st3[it];
       }

       is = 2*id - n2;
       id *= 4;
     } while ( is < N );

   }

   return;
 }



 private void bitReverse( double[] x ) {

   for ( int k = 1;  k < nbit;  k++ ) {

     int j0 = nbit * itab[k];
     int i = k;
     int j = j0;

     for ( int l = 2;  l <= itab[k] + 1;  l++ ) {

       double t1;

       t1   = x[i];
       x[i] = x[j];
       x[j] = t1;

       i += nbit;
       j = j0 + itab[l-1];
     }

   }

 }



 private void length2Butterflies( double[] x ) {

   //------- length-two butterflies --------

   int is = 1;
   int id = 4;

   do {
     for ( int i = is;  i <= N;  i += id ) {

       double t1;

       int i1 = i - 1;
       t1    =      x[i1];
       x[i1] = t1 + x[i];
       x[i]  = t1 - x[i];

     }

     is = 2*id - 1;
     id *= 4;
   } while ( is < N );

 }

}
