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
package llnl.gnem.core.util;

/**
 * Lightweight wrapper for accessing an array of arbitrary numerical type as a
 * double.
 *
 * @author addair1
 */
public interface NumericalList {
    public double get(int i);

    public void set(int i, double value);

    public int size();
    
    public NumericalList clone();

    public static class FloatList implements NumericalList {
        private final float[] data;

        public FloatList(float[] data) {
            this.data = data;
        }

        @Override
        public double get(int i) {
            return data[i];
        }

        @Override
        public void set(int i, double value) {
            data[i] = (float) value;
        }

        @Override
        public int size() {
            return data.length;
        }
        
        @Override
        public FloatList clone() {
            return new FloatList(data.clone());
        }
    }

    public static class DoubleList implements NumericalList {
        private final double[] data;

        public DoubleList(double[] data) {
            this.data = data;
        }

        @Override
        public double get(int i) {
            return data[i];
        }

        @Override
        public void set(int i, double value) {
            data[i] = value;
        }

        @Override
        public int size() {
            return data.length;
        }
        
        @Override
        public DoubleList clone() {
            return new DoubleList(data.clone());
        }
    }

    public static class NumberList implements NumericalList {
        private final Number[] data;

        public NumberList(Number[] data) {
            this.data = data;
        }

        @Override
        public double get(int i) {
            return data[i].doubleValue();
        }

        @Override
        public void set(int i, double value) {
            data[i] = value;
        }

        @Override
        public int size() {
            return data.length;
        }
        
        @Override
        public NumberList clone() {
            return new NumberList(data.clone());
        }
    }
}
