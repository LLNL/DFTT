/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author dodge1
 */
public class ComplexSpectrum {
    private final ArrayList<SpectralSample> samples;

    public ComplexSpectrum(ArrayList<SpectralSample> samples) {
        this.samples = samples;
    }

    public void print(PrintStream out) {
        for( SpectralSample samp : samples){
            samp.print(out);
        }
    }

    public ComplexSpectrum conjugate() {
        ArrayList<SpectralSample> result = new ArrayList<>();
        for( SpectralSample samp : samples){
            result.add(samp.conjugate());
        }
        return new ComplexSpectrum(result);
    }
    
    public Periodogram toPeriodogram()
    {
        return new Periodogram(this);
    }

    public ComplexSpectrum times(ComplexSpectrum other) {
        ArrayList<SpectralSample> result = new ArrayList<>();
        for( int j = 0; j < samples.size(); ++j){
            SpectralSample x = samples.get(j);
            SpectralSample y = other.samples.get(j);
            SpectralSample z = x.times(y);
            result.add(z);
        }
        return new ComplexSpectrum(result);
    }
    
    public SpectralSample getSample(int index){
        return samples.get(index);
    }

    public int size() {
        return samples.size();
    }
    
}
