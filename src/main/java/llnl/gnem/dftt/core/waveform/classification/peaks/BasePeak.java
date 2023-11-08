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
package llnl.gnem.dftt.core.waveform.classification.peaks;

/**
 *
 * @author dodge1
 */
public class BasePeak {

    private final int index;
    private final float value;
    private final double dt;

    public BasePeak(int index, float value, double dt) {
        this.index = index;
        this.value = value;
        this.dt = dt;
    }

    public BasePeak(BasePeak other) {
        this.index = other.index;
        this.value = other.value;
        this.dt = other.dt;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.index;
        hash = 89 * hash + Float.floatToIntBits(this.value);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.dt) ^ (Double.doubleToLongBits(this.dt) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasePeak other = (BasePeak) obj;
        if (this.index != other.index) {
            return false;
        }
        if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dt) != Double.doubleToLongBits(other.dt)) {
            return false;
        }
        return true;
    }

    public double getSampleInterval() {
        return dt;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    public double getTime() {
        return index * dt;
    }

    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Peak at index %d (%f seconds) with value %f", index, getTime(), value);
    }

}
