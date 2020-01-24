package llnl.gnem.apps.detection.core.signalProcessing;

//Adaptation to Java of the complex split-radix FFT routine described in
//the original Fortran comment block below.  This implementation uses
//table lookup for the sine and cosine values, and a partial bit-reverse
//table.
//D. Harris    February 11, 2008

/*=================================================================CC
CC                                                                 CC
CC  Subroutine CTFFTSR(X,Y,M,CT1,CT3,ST1,ST3,ITAB):                CC
CC      An in-place, split-radix complex FFT program               CC
CC      Decimation-in-frequency, cos/sin in third loop             CC
CC      and is looked-up in table. Tables CT1,CT3,ST1,ST3          CC
CC      have to have length>=N/8-1. The bit reverser uses partly   CC
CC      table lookup.                                              CC
CC                                                                 CC
CC  Input/output                                                   CC
CC      X    Array of real part of input/output (length >= N)      CC
CC      Y    Array of imaginary part of input/output (length >= N) CC
CC      M    Transform length is N=2**M                            CC
CC      CT1  Array of cos() table (length >= N/8-1)                CC
CC      CT3  Array of cos() table (length >= N/8-1)                CC
CC      ST1  Array of sin() table (length >= N/8-1)                CC
CC      ST3  Array of sin() table (length >= N/8-1)                CC
CC      ITAB Array of bitreversal indices (length >= sqrt(2*N)     CC
CC                                                                 CC
CC  Calls:                                                         CC
CC      CTSTAG                                                     CC
CC      and TINIT has to be called before this program!!           CC
CC                                                                 CC
CC  Author:                                                        CC
CC      H.V. Sorensen,   University of Pennsylvania,  Dec. 1984    CC
CC                       Arpa address: hvs@ee.upenn.edu            CC
CC  Modified:                                                      CC
CC      H.V. Sorensen,   University of Pennsylvania,  Jul. 1987    CC
CC                                                                 CC
CC  Reference:                                                     CC
CC      Sorensen, Heideman, Burrus :"On computing the split-radix  CC
CC      FFT", IEEE Tran. ASSP, Vol. ASSP-34, No. 1, pp. 152-156    CC
CC      Feb. 1986                                                  CC
CC      Mitra&Kaiser: "Digital Signal Processing Handbook, Chap.   CC
CC      8, page 491-610, John Wiley&Sons, 1993                     CC
CC                                                                 CC
CC      This program may be used and distributed freely as long    CC
CC      as this header is included                                 CC
CC                                                                 CC
CC=================================================================*/
public class CFFTdp extends FFTdp {

    public CFFTdp(int log2N) {
        super(log2N);
    }

    public void dft(double[] x, double[] y) {

        //------- L-shaped butterflies ---------
        int its = 1;
        int n2 = 2 * N;
        for (int k = 1; k <= log2N - 1; k++) {
            n2 /= 2;
            int n4 = n2 / 4;
            stage(n2, n4, its, x, y);
            its *= 2;
        }

        //------- length-two butterflies --------
        int is = 1;
        int id = 4;

        while (is < N) {

            for (int i1 = is; i1 <= N; i1 += id) {

                double t1;
                int i0 = i1 - 1;

                t1 = x[i0];
                x[i0] = t1 + x[i1];
                x[i1] = t1 - x[i1];

                t1 = y[i0];
                y[i0] = t1 + y[i1];
                y[i1] = t1 - y[i1];
            }

            is = 2 * id - 1;
            id *= 4;
        }

        //------- bit reverse arrays -------
        for (int k = 1; k < nbit; k++) {

            int j0 = nbit * itab[k];
            int i = k;
            int j = j0;

            for (int l = 2; l <= itab[k] + 1; l++) {

                double t1;

                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;

                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;

                i += nbit;
                j = j0 + itab[l - 1];
            }
        }

    }

    private void stage(int n2, int n4, int its, double[] x, double[] y) {

        int n8 = n4 / 2;

        //------- zero butterfly -------
        int is = 0;
        int id = 2 * n2;
        while (is < N) {
            for (int i = is; i < N; i += id) {

                double t1, t2;
                int i1 = i + n4;
                int i2 = i1 + n4;
                int i3 = i2 + n4;

                t1 = x[i] - x[i2];
                x[i] = x[i] + x[i2];
                t2 = y[i1] - y[i3];
                y[i1] = y[i1] + y[i3];
                x[i2] = t1 + t2;
                t2 = t1 - t2;
                t1 = x[i1] - x[i3];
                x[i1] = x[i1] + x[i3];
                x[i3] = t2;
                t2 = y[i] - y[i2];
                y[i] = y[i] + y[i2];
                y[i2] = t2 - t1;
                y[i3] = t2 + t1;
            }

            is = 2 * id - n2;
            id *= 4;
        }

        if (n4 <= 1) {
            return;
        }

        //------- N/8 butterfly --------
        is = 0;
        id = 2 * n2;
        while (is < N - 1) {

            for (int i = is + n8; i < N; i += id) {

                double t1, t2, t3, t4, t5;
                int i1 = i + n4;
                int i2 = i1 + n4;
                int i3 = i2 + n4;

                t1 = x[i] - x[i2];
                x[i] = x[i] + x[i2];
                t2 = x[i1] - x[i3];
                x[i1] = x[i1] + x[i3];
                t3 = y[i] - y[i2];
                y[i] = y[i] + y[i2];
                t4 = y[i1] - y[i3];
                y[i1] = y[i1] + y[i3];
                t5 = (t4 - t1) * SQRT2OVER2;
                t1 = (t4 + t1) * SQRT2OVER2;
                t4 = (t3 - t2) * SQRT2OVER2;
                t2 = (t3 + t2) * SQRT2OVER2;
                x[i2] = t4 + t1;
                y[i2] = t4 - t1;
                x[i3] = t5 + t2;
                y[i3] = t5 - t2;
            }

            is = 2 * id - n2;
            id *= 4;
        }

        if (n8 <= 1) {
            return;
        }

        //-------general butterfly. two at a time-------
        is = 1;
        id = n2 * 2;
        while (is < N) {

            for (int i = is; i <= N; i += id) {

                int it = 0;
                int jn = i + n4;

                for (int j = 0; j < n8 - 1; j++) {

                    double t1, t2, t3, t4, t5;

                    it = it + its;
                    int i0 = i + j;
                    int i1 = i0 + n4;
                    int i2 = i1 + n4;
                    int i3 = i2 + n4;

                    t1 = x[i0] - x[i2];
                    x[i0] = x[i0] + x[i2];
                    t2 = x[i1] - x[i3];
                    x[i1] = x[i1] + x[i3];
                    t3 = y[i0] - y[i2];
                    y[i0] = y[i0] + y[i2];
                    t4 = y[i1] - y[i3];
                    y[i1] = y[i1] + y[i3];
                    t5 = t1 - t4;
                    t1 = t1 + t4;
                    t4 = t2 - t3;
                    t2 = t2 + t3;
                    x[i2] = t1 * ct1[it] - t4 * st1[it];
                    y[i2] = -t4 * ct1[it] - t1 * st1[it];
                    x[i3] = t5 * ct3[it] + t2 * st3[it];
                    y[i3] = t2 * ct3[it] - t5 * st3[it];

                    int j0 = jn - 2 - j;
                    int j1 = j0 + n4;
                    int j2 = j1 + n4;
                    int j3 = j2 + n4;

                    t1 = x[j0] - x[j2];
                    x[j0] = x[j0] + x[j2];
                    t2 = x[j1] - x[j3];
                    x[j1] = x[j1] + x[j3];
                    t3 = y[j0] - y[j2];
                    y[j0] = y[j0] + y[j2];
                    t4 = y[j1] - y[j3];
                    y[j1] = y[j1] + y[j3];
                    t5 = t1 - t4;
                    t1 = t1 + t4;
                    t4 = t2 - t3;
                    t2 = t2 + t3;
                    x[j2] = t1 * st1[it] - t4 * ct1[it];
                    y[j2] = -t4 * st1[it] - t1 * ct1[it];
                    x[j3] = -t5 * st3[it] - t2 * ct3[it];
                    y[j3] = -t2 * st3[it] + t5 * ct3[it];
                }

            }

            is = 2 * id - n2 + 1;
            id *= 4;
        }

        return;
    }

// Inverse using M. R. Portnoff's trick
    public void idft(double[] x, double[] y) {

        dft(x, y);

        double scale = 1.0 / N;

        x[0] *= scale;    // special case at DC
        y[0] *= scale;
        x[N / 2] *= scale;    // special case at Nyquist
        y[N / 2] *= scale;

        int i = 1;
        int j = N - 1;
        while (i < j) {
            double temp = x[i];
            x[i] = x[j] * scale;
            x[j] = temp * scale;
            temp = y[i];
            y[i] = y[j] * scale;
            y[j] = temp * scale;
            i++;
            j--;
        }

    }

}
