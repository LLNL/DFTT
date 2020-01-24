package llnl.gnem.core.waveform.responseProcessing;

import com.oregondsp.signalProcessing.fft.CDFTdp;

/**
 *
 * @author addair1
 */
public class DCPFT {
    
    private int mpow2;
    private final CDFTdp fft;

    public DCPFT(int nfreq) {
        mpow2 = 0;

        while (Math.pow(2, mpow2) < nfreq) {
            mpow2 += 1;
        }

        
        fft = new CDFTdp(mpow2);
    }

    public void dcpft(double[] re, double[] im, int nfreq, int sgn) {
        double[] tre = new double[nfreq];
        double[] tim = new double[nfreq];
        if( sgn < 0 ){
            fft.evaluate(re, im, tre, tim);
            System.arraycopy(tre, 0, re, 0, nfreq);
            System.arraycopy(tim, 0, im, 0, nfreq);
        }
        else{
            fft.evaluateInverse(re, im, tre, tim);
            System.arraycopy(tre, 0, re, 0, nfreq);
            System.arraycopy(tim, 0, im, 0, nfreq);
            for( int j = 0; j < nfreq; ++j){
                re[j] *= nfreq;
                im[j] *= nfreq;
            }
        }

    }
}
