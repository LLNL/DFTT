package llnl.gnem.core.util.MathFunctions;

/**
 *
 * From http://www.paulinternet.nl/?page=bicubic
 */
public class TricubicInterpolator extends BicubicInterpolator {

    private double[] arr = new double[4];

    public double getValue(double[][][] p, double x, double y, double z) {
        arr[0] = getValue(p[0], y, z);
        arr[1] = getValue(p[1], y, z);
        arr[2] = getValue(p[2], y, z);
        arr[3] = getValue(p[3], y, z);
        return getValue(arr, x);
    }
}
