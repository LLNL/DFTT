package llnl.gnem.core.waveform.io;

/**
 *
 * @author addair1
 */
public class FloatBinaryData extends BinaryData {

    private final float[] data;

    /**
     * Constructs a BinaryData object using an array of floats.
     *
     */
    public FloatBinaryData(int n) {
        this.data = new float[n];
    }

    public FloatBinaryData(FloatBinaryData source, int newLength) {
        if (newLength > source.size()) {
            throw new IllegalStateException("New Length must be less than old length!");
        }
        data = new float[newLength];
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
        // BinaryData contract that by the time this method is called, this data should
        // no longer be modified by the lower-level code
        return data;
    }

    @Override
    public double[] getDoubleData() {
        double[] result = new double[data.length];
        for (int j = 0; j < result.length; ++j) {
            result[j] = data[j];
        }
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
        data[i] = (float) value;
    }
}
