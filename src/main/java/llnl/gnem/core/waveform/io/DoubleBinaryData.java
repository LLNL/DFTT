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
package llnl.gnem.core.waveform.io;

/**
 *
 * @author addair1
 */
public class DoubleBinaryData extends BinaryData {

    private final double[] data;

    /**
     * Constructs a BinaryData object using an array of doubles.
     *
     */
    public DoubleBinaryData(int n) {
        this.data = new double[n];
    }

    public DoubleBinaryData(DoubleBinaryData source, int newLength) {
        if (newLength > source.size()) {
            throw new IllegalStateException("New Length must be less than old length!");
        }
        data = new double[newLength];
        System.arraycopy(source.data, 0, data, 0, newLength);
    }

    @Override
    public int[] getIntData() {
        int[] result = new int[data.length];
        for (int j = 0; j < result.length; ++j) {
            result[j] = (int) data[j];
        }
        return result;
    }

    @Override
    public float[] getFloatData() {
        float[] result = new float[data.length];
        for (int j = 0; j < result.length; ++j) {
            result[j] = (float) data[j];
        }
        return result;
    }

    @Override
    public double[] getDoubleData() {
        double[] result = new double[data.length];
        System.arraycopy(data, 0, result, 0, result.length);
        return result;
    }

    @Override
    public int getInt(int i) {
        return (int) data[i];
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public void setInt(int i, int value) {
        data[i] = value;
    }

    @Override
    public void setFloat(int i, float value) {
        data[i] = value;
    }

    @Override
    public void setDouble(int i, double value) {
        data[i] = value;
    }
}
