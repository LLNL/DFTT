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
package llnl.gnem.apps.detection.sdBuilder.multiStationStack;

import java.util.Arrays;
import java.util.Objects;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class StackElement {
    private final StreamKey key;
    private final float[] data;
    private final double delta;

    public StackElement(StreamKey key, float[] data, double delta) {
        this.key = key;
        this.data = data;
        this.delta = delta;
    }

    public StreamKey getKey() {
        return key;
    }

    public float[] getData() {
        return data;
    }

    public double getDelta() {
        return delta;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.key);
        hash = 59 * hash + Arrays.hashCode(this.data);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.delta) ^ (Double.doubleToLongBits(this.delta) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StackElement other = (StackElement) obj;
        if (Double.doubleToLongBits(this.delta) != Double.doubleToLongBits(other.delta)) {
            return false;
        }
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }
    
}
