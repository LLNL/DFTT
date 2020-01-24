/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.classification;


import org.apache.commons.math3.complex.Complex;

import llnl.gnem.core.waveform.seismogram.TimeSeries;

/**
 *
 * @author dodge1
 */
public class SpectralEdgeFrequency {
    private final double edgeFrequency;

    public SpectralEdgeFrequency(double edgeFrequency) {
        this.edgeFrequency = edgeFrequency;
    }
    
    public SpectralEdgeFrequency( TimeSeries ts, double energyFraction)
    {
        
        Complex[] transformed = ts.FFT();
        int npts = transformed.length;
        
        double totalEnergy = 0;
        for( int j = 0; j < npts/2; ++j){
            double v = transformed[j].abs();
            totalEnergy += (v*v);
        }
       
        double threshold = totalEnergy * energyFraction;
        double energy = 0;
        double delFreq = ts.getSamprate() /2 / (npts/2-1);
        
        double edgeFreq = ts.getSamprate() / 2;
        for (int j = 0; j < npts / 2; ++j) {
            double v = transformed[j].abs();
            energy += v*v;
            if( energy >= threshold){
                edgeFreq = j * delFreq;
                break;
            }
        }
        
        edgeFrequency = edgeFreq;
    }

    /**
     * @return the edgeFrequency
     */
    public double getEdgeFrequency() {
        return edgeFrequency;
    }
    
}
