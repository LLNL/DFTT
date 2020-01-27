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
package llnl.gnem.core.signalprocessing.extended;

import llnl.gnem.core.signalprocessing.extended.ComplexSequence;

/**
 * The main purpose of this class is to provide the modulate function, which
 * translates the frequencies of a function over a specific amount without
 * altering the amount of each frequency. An instantiation of the Modulator
 * class holds the values governing the type of signals it can modulate, and
 * the amount it will translate the frequencies. It also holds its own
 * cos/sin table, represented as a DoubleComplexSequence.
 * <p></p>
 *
 * @author Timothy Paik
 *         written on 6/24/04
 */
public class Modulator {
    private static final double TWO_PI = Math.PI * 2;
    private static final int REFRESH_RATE = 10000;

    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////
    private float freq;
    private float delta;
    private int counter;
    private int refreshIndex;

    private double[] currState;
    private double[] refreshState;

    // Values saved to speed up the process
    private double c;
    private double cosValue;
    private double sinValue;

    /**
     * Constructs a new Modulator given the necessary fields.
     *
     * @param f - the amount this Modulator will translate frequencies.
     * @param d - the amount of time between samples in the signals this
     *          Modulator can affect.
     */
    public Modulator(float f, float d) {
        freq = f;
        delta = d;
        counter = 0;
        refreshIndex = 0;

        c = TWO_PI * f * d;
        cosValue = Math.cos(c);
        sinValue = Math.sin(c);

        currState = new double[2];
        refreshState = new double[2];
        currState[0] = 1;
        currState[1] = 0;
        refreshState[0] = 1;
        refreshState[1] = 0;
    }

    /**
     * Constructs a new Modulator using values from another Modulator.
     *
     * @param m - the other Modulator
     */
    public Modulator(Modulator m) {
        freq = m.freq;
        delta = m.delta;
        counter = m.counter;
        refreshIndex = m.refreshIndex;
        c = m.c;
        cosValue = m.cosValue;
        sinValue = m.sinValue;

        currState = new double[2];
        refreshState = new double[2];
        currState[0] = m.currState[0];
        currState[1] = m.currState[1];
        refreshState[0] = m.refreshState[0];
        refreshState[1] = m.refreshState[1];
    }

    /**
     * Demodulates a Sequence by the frequency specified by this Modulator.
     *
     * @param seq - the Sequence to be modulated
     */
    public ComplexSequence modulate(RealSequence seq) {
        ComplexSequence seq1 = new ComplexSequence(seq.length());

        float[] real1 = seq1.getRealArray();
        float[] imag1 = seq1.getImagArray();
        float[] real2 = seq.getArray();

        for (int i = 0; i < seq1.length(); i++) {
            real1[i] = ((float) currState[0] * real2[i]);
            imag1[i] = (real2[i] * (float) currState[1]);
            getNextState();
        }

        return seq1;
    }

    /**
     * Demodulates a float array by the frequency specified by this Modulator.
     *
     * @param arr - the array to be modulated
     */
    public ComplexSequence modulate(float[] arr) {
        ComplexSequence seq = new ComplexSequence(arr.length);

        float[] real1 = seq.getRealArray();
        float[] imag1 = seq.getImagArray();

        for (int i = 0; i < arr.length; i++) {
            real1[i] = ((float) currState[0] * arr[i]);
            imag1[i] = (arr[i] * (float) currState[1]);
            getNextState();
        }

        return seq;
    }

    /**
     * Demodulates a Sequence and places the values into a
     * ComplexSequence of the same length.
     *
     * @param seq1 - the ComplexSequence to be replaced
     * @param seq2 - the Sequence to be modulated
     * @throws SignalProcessingException - if the ComplexSequence and the Sequence
     *                                   have different lengths
     */
    public void modulate(ComplexSequence seq1, RealSequence seq2) throws SignalProcessingException {
        if (seq1.length() != seq2.length()) {
            throw new SignalProcessingException
                    ("Trying to modulate a Sequence into a ComplexSequence of different length");
        }

        float[] real1 = seq1.getRealArray();
        float[] imag1 = seq1.getImagArray();
        float[] real2 = seq2.getArray();

        for (int i = 0; i < seq1.length(); i++) {
            real1[i] = ((float) currState[0] * real2[i]);
            imag1[i] = (real2[i] * (float) currState[1]);
            getNextState();
        }
    }

    /**
     * Demodulates a float array and places the values into a
     * ComplexSequence of the same length.
     *
     * @param seq - the ComplexSequence to be replaced
     * @param arr - the array to be modulated
     * @throws SignalProcessingException - if the ComplexSequence and the Sequence
     *                                   have different lengths
     */
    public void modulate(ComplexSequence seq, float[] arr) throws SignalProcessingException {
        if (seq.length() != arr.length) {
            throw new SignalProcessingException
                    ("Trying to modulate a float array into a ComplexSequence of different length");
        }

        float[] real1 = seq.getRealArray();
        float[] imag1 = seq.getImagArray();

        for (int i = 0; i < arr.length; i++) {
            real1[i] = ((float) currState[0] * arr[i]);
            imag1[i] = (arr[i] * (float) currState[1]);
            getNextState();
        }
    }

    /**
     * Moves the internal cos/sin state to the next state.
     */
    private void getNextState() {
        counter++;

        if ((counter % REFRESH_RATE) == 0) {
            refreshIndex++;
            currState[0] = Math.cos(c * REFRESH_RATE * refreshIndex);
            currState[1] = Math.sin(c * REFRESH_RATE * refreshIndex);
        } else {
            double tmpC = currState[0];
            double tmpS = currState[1];

            currState[0] = (cosValue * tmpC) + (-sinValue * tmpS);
            currState[1] = (sinValue * tmpC) + (cosValue * tmpS);
        }
    }
}
