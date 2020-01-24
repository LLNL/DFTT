/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing;

/**
 * Formulas from https://en.wikipedia.org/wiki/Window_function
 * @author dodge1
 */
public class WindowFunction {

    private static float getWindowValue(int j, int n, WindowType type) {
        switch (type) {
            case NONE:
                return 1.0f;
            case BARTLETT:
                return bartlettValue(j, n);
            case WELCH:
                return welchValue(j, n);
            case HANNING:
                return hanningValue(j, n);
            case HAMMING:
                return hammingValue(j, n);
            case BLACKMAN:
                return blackmanValue(j,n);
            case BLACKMAN_NUTTALL:
                return blackmanNuttallValue(j,n);
            case TUKEY:
                return tukeyValue(j,n);
            default:
                throw new IllegalStateException("Unknown type : " + type);
        }
    }

    private static float tukeyLowValue(int j, int n) {
        double term = 2 * j / tukeyCoeff / (n-1) -1;
        return (float)(1 + Math.cos(term * Math.PI))/2;
    }

    private static float tukeyHighValue(int j, int n) {
        double term = 2 * j / tukeyCoeff / (n-1) +1 - 2/tukeyCoeff;
        return (float)(1 + Math.cos(term * Math.PI))/2;
    }

    public static synchronized float getTukeyCoeff() {
        return tukeyCoeff;
    }

    public static synchronized void setTukeyCoeff(float aTukeyCoeff) {
        tukeyCoeff = Math.min(Math.max(0, aTukeyCoeff),1);
    }

    public static enum WindowType {

        NONE, BARTLETT, WELCH, HANNING, HAMMING, BLACKMAN, BLACKMAN_NUTTALL, TUKEY
    }
    
    private static float tukeyCoeff = 0.5f;

    public static void applyWindow(float[] data, WindowType type) {
        int n = data.length;
        for (int j = 0; j < n; ++j) {
            float w = getWindowValue(j, n, type);
            data[j] *= w;
        }
    }

    public static void applyWindow(double[] data, WindowType type) {
        int n = data.length;
        for (int j = 0; j < n; ++j) {
            float w = getWindowValue(j, n, type);
            data[j] *= w;
        }
    }
    
    public static float[] getWindow(WindowType type, int windowLength){
        float[] result = new float[windowLength];
        for (int j = 0; j < windowLength; ++j) {
            float w = getWindowValue(j, windowLength, type);
            result[j] = w;
        }
        return result;
    }

    private static float bartlettValue(int j, int n) {
        float l = n - 1;
        float t2 = l / 2;
        return 1 - Math.abs((j - t2) / t2);
    }

    private static float welchValue(int j, int n) {
        float v = (n - 1) / 2.0f;
        float num = j - v;
        float p = num / v;
        return 1 - p * p;
    }

    private static float hanningValue(int j, int n) {
        double num = 2 * Math.PI * j;
        double cv = Math.cos(num / (n - 1));
        return 0.5f * (float) (1 - cv);
    }

    private static float hammingValue(int j, int n) {
        double num = 2 * Math.PI * j;
        double cv = Math.cos(num / (n - 1));
        return 0.53836f - 0.46164f * (float) cv;
    }

    private static float blackmanValue(int j, int n) {
        float a0 = 0.42659f;
        float a1 = 0.49656f;
        float a2 = 0.076849f;
        double num = 2 * Math.PI * j;
        double cv = Math.cos(num / (n - 1));
        double cv2 = Math.cos(num * 2 / (n - 1));
        return a0 - a1 * (float) cv + a2 * (float) cv2;
    }
    
    private static float blackmanNuttallValue(int j, int n){
        float a0 = 0.3635819f;
        float a1 = 0.4891775f;
        float a2 = 0.1365995f;
        float a3 = 0.0106411f;
        double num = 2 * Math.PI * j;
        double cv = Math.cos(num / (n - 1));
        double cv2 = Math.cos(num * 2 / (n - 1));
        double cv3 = Math.cos(num * 3 / (n - 1));
        return a0 - a1 * (float)cv + a2 * (float)cv2 - a3 * (float)cv3;
    }
    
    private static float tukeyValue(int j, int n){
        float test1 = tukeyCoeff * (n-1)/2;
        if(j <= test1)
            return tukeyLowValue(j,n);
        float test2 = (n-1) * (1-tukeyCoeff/2);
        if(j <= test2)return 1;
        return tukeyHighValue(j,n);
    }

}
