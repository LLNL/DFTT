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
public class Periodogram {

    private final ArrayList<PeriodogramSample> samps;

    private Periodogram(Periodogram first) {
        this.samps = new ArrayList<>(first.samps);
    }

    private Periodogram(ArrayList<PeriodogramSample> other) {
        this.samps = new ArrayList<>(other);
    }

    public Periodogram(ComplexSpectrum spectrum) {
        this.samps = new ArrayList<>();
        for (int j = 0; j < spectrum.size(); ++j) {
            SpectralSample s = spectrum.getSample(j);
            samps.add(new PeriodogramSample(s));
        }
    }

    public Periodogram(ComplexSpectrum xSpec, ComplexSpectrum ySpec) {
        if(xSpec.size() != ySpec.size()){
            throw new IllegalStateException("Spectra have different lengths!");
        }
        this.samps = new ArrayList<>();
        for (int j = 0; j < xSpec.size(); ++j) {
            SpectralSample sX = xSpec.getSample(j);
            SpectralSample sY = ySpec.getSample(j);
            samps.add(new PeriodogramSample(sX,sY));
        }
    }

    private boolean isConsistent(Periodogram other) {
        if (other.size() != this.size()) {
            return false;
        }

        return other.frequenciesMatch(this);
    }

    public int size() {
        return samps.size();
    }

    public void print(PrintStream out) {
        for (PeriodogramSample samp : samps) {
            samp.print(out);
        }
    }

    private boolean frequenciesMatch(Periodogram other) {
        for (int j = 0; j < samps.size(); ++j) {
            if (samps.get(j).getFrequency() != other.samps.get(j).getFrequency()) {
                return false;
            }
        }
        return true;
    }

    public PeriodogramSample getSample(int index) {
        return samps.get(index);
    }

    public static Periodogram average(ArrayList<Periodogram> periodograms) {
        if (periodograms.isEmpty()) {
            throw new IllegalStateException("Empty periuodogram list!");
        }
        Periodogram first = periodograms.get(0);
        for (Periodogram p : periodograms) {
            if (!p.isConsistent(first)) {
                throw new IllegalStateException("Inconsistent periodogram list!");
            }
        }
        ArrayList<PeriodogramSample> resultArray = new ArrayList<>();
        for (int j = 0; j < first.size(); ++j) {
            double sum = 0;
            double f = 0;
            for (Periodogram p : periodograms) {
                sum += p.getSample(j).getValue();
                f = p.getSample(j).getFrequency();
            }
            sum /= periodograms.size();
            resultArray.add(new PeriodogramSample(f, sum));
        }
        return new Periodogram(resultArray);
    }

    public ArrayList<PeriodogramSample> getTwoSidedPSD() {
        return new ArrayList<>(samps);
    }

    public ArrayList<PeriodogramSample> getOneSidedPSD() {
        ArrayList<PeriodogramSample> result = new ArrayList<>();
        for (PeriodogramSample ps : samps) {
            if (ps.getFrequency() >= 0) {
                result.add(ps);
            }
        }
        return result;
    }

}
