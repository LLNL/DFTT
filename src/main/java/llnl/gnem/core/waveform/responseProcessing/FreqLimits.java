package llnl.gnem.core.waveform.responseProcessing;

import java.util.ArrayList;
import llnl.gnem.core.util.BandInfo;
import net.jcip.annotations.ThreadSafe;

/*
 *  COPYRIGHT NOTICE
 *  RBAP Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
/**
 * A class to hold the four frequency limits used by transfer when removing an
 * instrument response.
 *
 * @author Doug Dodge
 */
@ThreadSafe
public class FreqLimits
{

    public FreqLimits(double nyquist, double windowLength)
    {
        double tfactor = 2;
        double highpassFrac = 0.85;
        double highCutFrac = 0.9;
        double lowCutFrac = 0.8;

        highpass = highpassFrac * nyquist;
        highcut = highCutFrac * nyquist;

        double minfreq = 0.003;

        lowpass = Math.max(tfactor / windowLength, minfreq);
        lowcut = lowCutFrac * lowpass;
    }

    /**
     * Constructor for the FreqLimits object
     *
     * @param lowcut The low cut frequency
     * @param lowpass The low pass frequency
     * @param highpass The high pass frequency
     * @param highcut The high cut frequency
     */
    public FreqLimits(double lowcut, double lowpass, double highpass, double highcut)
    {
        this.lowcut = lowcut;
        this.lowpass = lowpass;
        this.highpass = highpass;
        this.highcut = highcut;
    }

    /**
     * Copy Constructor for the FreqLimits object
     *
     * @param s FreqLimits object to be copied
     */
    public FreqLimits(FreqLimits s)
    {
        this.lowcut = s.lowcut;
        this.lowpass = s.lowpass;
        this.highpass = s.highpass;
        this.highcut = s.highcut;
    }

    /**
     * Create a set of frequency bands evenly sampled in log10 space
     *
     * @param broadband the broadest band desired
     * @param nbands the number of narrow bands within the broad frequency range
     * @return an ArrayList of FreqLimits objects including the broadband and
     * all narrow bands desired
     */
    public static ArrayList<FreqLimits> createMultiBandFreqLimits(FreqLimits broadband, int nbands)
    {
        ArrayList<FreqLimits> freqlimitsarray = new ArrayList<>();

        freqlimitsarray.add(broadband);

        System.out.println("broadband:\t" + broadband.toString());
        if (nbands > 1)
        {
            // evenly sample in log10 space
            double lowcut = Math.log10(broadband.getLowcut());
            double lowpass = Math.log10(broadband.getLowpass());
            double highpass = Math.log10(broadband.getHighpass());
            double highcut = Math.log10(broadband.getHighcut());

            double dband = (highpass - lowpass) / nbands;

            for (int ii = 0; ii < nbands; ii++)// set to run 3 separate bands
            {
                double lowp = lowpass + (ii * dband);
                double lowc = lowpass - ((lowpass - lowcut) / nbands);
                double highp = lowp + dband;
                double highc = highp + ((highcut - highpass) / nbands);

                // Convert back to linear domain
                lowc = Math.pow(10, lowc);
                lowp = Math.pow(10, lowp);
                highp = Math.pow(10, highp);
                highc = Math.pow(10, highc);

                FreqLimits narrowband = new FreqLimits(lowc, lowp, highp, highc);
                freqlimitsarray.add(narrowband);
                System.out.println(ii + "\t" + narrowband.toString());
            }
        }

        return freqlimitsarray;
    }

    /**
     * Gets the lowcut attribute of the FreqLimits object
     *
     * @return The lowcut value
     */
    public double getLowcut()
    {
        return lowcut;
    }

    /**
     * Gets the lowpass attribute of the FreqLimits object
     *
     * @return The lowpass value
     */
    public double getLowpass()
    {
        return lowpass;
    }

    /**
     * Gets the highpass attribute of the FreqLimits object
     *
     * @return The highpass value
     */
    public double getHighpass()
    {
        return highpass;
    }

    /**
     * Gets the highcut attribute of the FreqLimits object
     *
     * @return The highcut value
     */
    public double getHighcut()
    {
        return highcut;
    }

    /**
     * Produce a String representation of the FreqLimits object
     *
     * @return Descriptive String
     */
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder("Lowcut = " + lowcut + ", Lowpass = " + lowpass);
        s.append(", Highpass = ").append(highpass).append(", Highcut = ").append(highcut);
        return s.toString();
    }

    /**
     * Tests for equality of two FreqLimits objects
     *
     * @param o Object to be compared to this object
     * @return true if both objects are the same.
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (o instanceof FreqLimits)
        {
            // No need to check for null because instanceof handles that check

            FreqLimits tmp = (FreqLimits) o;
            return lowcut == tmp.lowcut && lowpass == tmp.lowpass && highpass == tmp.highpass && highcut == tmp.highcut;
        }
        else
        {
            return false;
        }
    }

    /**
     * Produce an integer hash code based on the four double values contained in
     * this class.
     *
     * @return The has code.
     */
    @Override
    public int hashCode()
    {
        return new Double(lowcut).hashCode() ^ new Double(lowpass).hashCode() ^ new Double(highpass).hashCode() ^ new Double(highcut).hashCode();
    }
    private final double lowcut;
    private final double lowpass;
    private final double highpass;
    private final double highcut;

    public boolean containsBand(BandInfo band) {
        return lowpass <= band.getLowpass() && highpass >= band.getHighpass();
    }
}
