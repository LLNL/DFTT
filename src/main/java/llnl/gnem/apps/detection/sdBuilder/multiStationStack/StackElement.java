/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
