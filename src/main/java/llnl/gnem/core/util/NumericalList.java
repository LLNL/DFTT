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
