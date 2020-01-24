/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util.seriesMathHelpers;

/**
 *
 * @author dodge1
 */
public class Glitch {

    private final int index;
    private final float correction;

    public Glitch(int index, float correction) {
        this.index = index;
        this.correction = correction;
    }

    public int getIndex() {
        return index;
    }

    public float getCorrection() {
        return correction;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.index;
        hash = 47 * hash + Float.floatToIntBits(this.correction);
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
        final Glitch other = (Glitch) obj;
        if (this.index != other.index) {
            return false;
        }
        if (Float.floatToIntBits(this.correction) != Float.floatToIntBits(other.correction)) {
            return false;
        }
        return true;
    }
    
    
}