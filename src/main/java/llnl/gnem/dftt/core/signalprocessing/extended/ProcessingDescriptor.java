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
package llnl.gnem.dftt.core.signalprocessing.extended;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import llnl.gnem.dftt.core.util.Passband;

/**
 * A ProcessingDescriptor holds the parameters that the bandpass transformation
 * applied to the sequences in separate channels before they are multiplexed.
 *
 * @author Tim Paik AND Dave Harris
 */
public class ProcessingDescriptor {

    private static final int MAXSTACHANLENGTH = 16;
    private static final double GUARDBAND_CONSTANT = 2.0f;

    private Passband bandLimitFilterType;
    private double bandLimitFilter_cutoff1;
    private double bandLimitFilter_cutoff2;
    private double antiAliasFilter_cutoff;
    private int bandLimitFilter_npoles;
    private int antiAliasFilter_npoles;
    private double mod_freq;
    private double delta;
    private double decDelta;
    private int decRate;
    private int numChannels;
    private String[] staChanNames;

    /**
     * Creates a new ProcessingDescriptor from the fields necessary.
     *
     * @param _bandLimitFilterType - the String abbreviation for the filter type
     * bandLimitFilter is (i.e. "BP", "LP")
     * @param _bandLimitFilter_cutoff1 - the first frequency cutoff for
     * bandLimitFilter
     * @param _bandLimitFilter_cutoff2 - the second frequency cutoff for
     * bandLimitFilter
     * @param _antiAliasFilter_cutoff - the antialias filter cutoff
     * @param _bandLimitFilter_npoles - the number of poles for the
     * bandLimitFilter
     * @param _antiAliasFilter_npoles - the number of poles for the
     * antiAliasFilter
     * @param _mod_freq - the amount (frequency) that the Modulator demodulates
     * by
     * @param _delta - the time between samples in the signals
     * @param _decRate - the decimation rate
     * @param _staChanNames - the names of the channels that the data comes from
     */
    public ProcessingDescriptor(String _bandLimitFilterType,
            double _bandLimitFilter_cutoff1,
            double _bandLimitFilter_cutoff2,
            double _antiAliasFilter_cutoff,
            int _bandLimitFilter_npoles,
            int _antiAliasFilter_npoles,
            double _mod_freq,
            double _delta,
            int _decRate,
            String[] _staChanNames) {
        bandLimitFilterType = Passband.getPassbandFromString(_bandLimitFilterType);
        bandLimitFilter_cutoff1 = _bandLimitFilter_cutoff1;
        bandLimitFilter_cutoff2 = _bandLimitFilter_cutoff2;
        antiAliasFilter_cutoff = _antiAliasFilter_cutoff;
        bandLimitFilter_npoles = _bandLimitFilter_npoles;
        antiAliasFilter_npoles = _antiAliasFilter_npoles;
        mod_freq = _mod_freq;
        delta = _delta;
        decRate = _decRate;
        decDelta = decRate * delta;
        if (_staChanNames != null) {
            numChannels = _staChanNames.length;
        }

        staChanNames = _staChanNames;
    }

    /**
     * Constructor that implements a canned preprocessor design policy
     *
     * @param _bandLimitFilter_cutoff1
     * @param _bandLimitFilter_cutoff2
     * @param _bandLimitFilter_npoles
     * @param _antiAliasFilter_npoles
     * @param _delta
     * @param _staChanNames
     */
    public ProcessingDescriptor(double _bandLimitFilter_cutoff1,
            double _bandLimitFilter_cutoff2,
            int _bandLimitFilter_npoles,
            int _antiAliasFilter_npoles,
            double _delta,
            String[] _staChanNames) {

        bandLimitFilterType = Passband.BAND_PASS;
        bandLimitFilter_cutoff1 = _bandLimitFilter_cutoff1;
        bandLimitFilter_cutoff2 = _bandLimitFilter_cutoff2;
        antiAliasFilter_cutoff = (((bandLimitFilter_cutoff2 - bandLimitFilter_cutoff1) / 2.0) * GUARDBAND_CONSTANT);
        bandLimitFilter_npoles = _bandLimitFilter_npoles;
        antiAliasFilter_npoles = _antiAliasFilter_npoles;
        mod_freq = (bandLimitFilter_cutoff1 + bandLimitFilter_cutoff2) / 2.0;
        delta = _delta;
        decRate = (int) Math.floor(1.0 / (2 * delta * antiAliasFilter_cutoff));

        if (decRate > 5) {
            decRate = 5;
        }

        decDelta = decRate * delta;

        if (_staChanNames != null) {
            numChannels = _staChanNames.length;
        }

        staChanNames = _staChanNames;
    }

    /**
     * Creates a clone of a ProcessingDescriptor.
     *
     * @param PD - the ProcessingDescriptor to copy
     */
    public ProcessingDescriptor(ProcessingDescriptor PD) {

        bandLimitFilterType = PD.bandLimitFilterType;
        bandLimitFilter_cutoff1 = PD.bandLimitFilter_cutoff1;
        bandLimitFilter_cutoff2 = PD.bandLimitFilter_cutoff2;
        antiAliasFilter_cutoff = PD.antiAliasFilter_cutoff;
        bandLimitFilter_npoles = PD.bandLimitFilter_npoles;
        antiAliasFilter_npoles = PD.antiAliasFilter_npoles;
        mod_freq = PD.mod_freq;
        delta = PD.delta;
        decRate = PD.decRate;
        decDelta = PD.decDelta;
        numChannels = PD.numChannels;
        staChanNames = PD.staChanNames;

    }

    /**
     * Creates a ProcessingDescriptor from a DataInputStream.
     *
     * @param dis - the DataInputStream to read the fields for this
     * ProcessingDescriptor from
     * @throws IOException - if there is an error reading from the
     * DataInputStream
     */
    public ProcessingDescriptor(DataInputStream dis) throws IOException {

        char[] c = new char[2];
        c[ 0] = dis.readChar();
        c[ 1] = dis.readChar();
        bandLimitFilterType = Passband.getPassbandFromString(new String(c));

        bandLimitFilter_cutoff1 = dis.readDouble();
        bandLimitFilter_cutoff2 = dis.readDouble();
        antiAliasFilter_cutoff = dis.readDouble();
        bandLimitFilter_npoles = dis.readInt();
        antiAliasFilter_npoles = dis.readInt();
        mod_freq = dis.readDouble();
        delta = dis.readDouble();
        decRate = dis.readInt();
        decDelta = dis.readDouble();
        numChannels = dis.readInt();

        c = new char[MAXSTACHANLENGTH];

        staChanNames = new String[numChannels];
        for (int i = 0; i < numChannels; i++) {
            for (int j = 0; j < MAXSTACHANLENGTH; j++) {
                c[ j] = dis.readChar();
            }
            staChanNames[ i] = (new String(c)).trim();
        }

    }

    /**
     * Writes this ProcessingDescriptor's fields out to a DataOutputStream.
     *
     * @param dos - the DataOutputStream to write out to
     * @throws IOException - if there is an error writing to the
     * DataOutputStream
     */
    public void save(DataOutputStream dos) throws IOException {

        dos.writeChars(bandLimitFilterType.toString());
        dos.writeDouble(bandLimitFilter_cutoff1);
        dos.writeDouble(bandLimitFilter_cutoff2);
        dos.writeDouble(antiAliasFilter_cutoff);
        dos.writeInt(bandLimitFilter_npoles);
        dos.writeInt(antiAliasFilter_npoles);
        dos.writeDouble(mod_freq);
        dos.writeDouble(delta);
        dos.writeInt(decRate);
        dos.writeDouble(decDelta);
        dos.writeInt(numChannels);
        String S;

        for (int i = 0; i < numChannels; i++) {
            S = new String(staChanNames[ i]);
            while (S.length() < MAXSTACHANLENGTH) {
                S += " ";
            }
            dos.writeChars(S);
        }

    }

    /**
     * Returns true if this ProcessingDescriptor is equal to the
     * ProcessingDescriptor passed in as a parameter. Also, writes out this
     * ProcessingDescriptor's fields and the other ProcessingDescriptor's fields
     * out to a String[], also passed in as a parameter.
     *
     * @param PD - the other ProcessingDescriptor
     * @param descriptors - the String array holding the
     * @return true if the two ProcessingDescriptors are equal
     */
    public boolean compare(ProcessingDescriptor PD, String[] descriptors) {

        boolean retval = this.equals(PD);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        print(ps);
        descriptors[ 0] = baos.toString();
        ps.close();

        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
        PD.print(ps);
        descriptors[ 1] = baos.toString();
        ps.close();

        return retval;
    }

    public boolean equals(ProcessingDescriptor PD) {

        boolean retval = true;

        if (!(bandLimitFilterType.equals(PD.bandLimitFilterType))) {
            retval = false;
        }
        if (bandLimitFilter_cutoff1 != PD.bandLimitFilter_cutoff1) {
            retval = false;
        }
        if (bandLimitFilter_cutoff2 != PD.bandLimitFilter_cutoff2) {
            retval = false;
        }
        if (antiAliasFilter_cutoff != PD.antiAliasFilter_cutoff) {
            retval = false;
        }
        if (bandLimitFilter_npoles != PD.bandLimitFilter_npoles) {
            retval = false;
        }
        if (antiAliasFilter_npoles != PD.antiAliasFilter_npoles) {
            retval = false;
        }
        if (mod_freq != PD.mod_freq) {
            retval = false;
        }
        if (delta != PD.delta) {
            retval = false;
        }
        if (bandLimitFilter_npoles != PD.bandLimitFilter_npoles) {
            retval = false;
        }
        if (bandLimitFilter_npoles != PD.bandLimitFilter_npoles) {
            retval = false;
        }
        if (decRate != PD.decRate) {
            retval = false;
        }
        if (decDelta != PD.decDelta) {
            retval = false;
        }
        if (numChannels != PD.numChannels) {
            retval = false;
        }

        return retval;
    }

    public Passband getBandLimitFilterType() {
        return bandLimitFilterType;
    }

    public double getBandLimitFilter_cutoff1() {
        return bandLimitFilter_cutoff1;
    }

    public double getBandLimitFilter_cutoff2() {
        return bandLimitFilter_cutoff2;
    }

    public double getAntiAliasFilter_cutoff() {
        return antiAliasFilter_cutoff;
    }

    public int getBandLimitFilter_npoles() {
        return bandLimitFilter_npoles;
    }

    public int getAntiAliasFilter_npoles() {
        return antiAliasFilter_npoles;
    }

    public double getMod_freq() {
        return mod_freq;
    }

    public double getDelta() {
        return delta;
    }

    public int getDecRate() {
        return decRate;
    }

    public double getDecDelta() {
        return decDelta;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public String[] getStaChanNames() {
        return staChanNames;
    }

    /**
     * Prints this ProcessingDescriptor's fields out to a PrintStream.
     *
     * @param ps - the PrintStream to write this ProcessingDescriptor's fields
     * out to
     */
    public void print(PrintStream ps) {

        ps.println("ProcessingDescriptor: ");
        ps.println("  bandLimitFilter type:             " + bandLimitFilterType);
        ps.println("  bandLimitFilter_cutoff1:          " + bandLimitFilter_cutoff1);
        ps.println("  bandLimitFilter_cutoff2:          " + bandLimitFilter_cutoff2);
        ps.println("  antiAliasFilter_cutoff:           " + antiAliasFilter_cutoff);
        ps.println("  bandLimitFilter #poles:           " + bandLimitFilter_npoles);
        ps.println("  antiAliasFilter #poles:           " + antiAliasFilter_npoles);
        ps.println("  modulation frequency:             " + mod_freq);
        ps.println("  modulator delta:                  " + delta);
        ps.println("  decimation rate:                  " + decRate);
        ps.println("  decimated sampling interval:      " + decDelta);
        ps.println("  number of channels:               " + numChannels);

        for (int i = 0; i < numChannels; i++) {
            ps.println("  channel " + i + " name:      "
                    + staChanNames[ i]);
        }

    }
}
