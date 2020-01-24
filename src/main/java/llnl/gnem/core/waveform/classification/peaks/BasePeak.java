/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.classification.peaks;

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
