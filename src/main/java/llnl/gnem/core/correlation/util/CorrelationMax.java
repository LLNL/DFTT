package llnl.gnem.core.correlation.util;

/**
 *
 * @author dodge1
 */
public class CorrelationMax {

    @Override
    public String toString() {
        return "CorrelationMax{" + "ccMax=" + ccMax + ", shift=" + shift + ", windowLength=" + windowLength + '}';
    }

    private final double ccMax;
    private final double shift;
    private final int windowLength;

    public CorrelationMax(double ccMax, double shift, int windowLength) {

        this.ccMax = ccMax;
        this.shift = shift;
        this.windowLength = windowLength;

    }

    public double getCcMax() {
        return ccMax;
    }

    /**
     * @return the shift
     */
    public double getShift() {
        return shift;
    }

    /**
     * @return the windowLength
     */
    public int getWindowLength() {
        return windowLength;
    }

}
