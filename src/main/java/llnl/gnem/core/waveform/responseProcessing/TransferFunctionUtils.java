package llnl.gnem.core.waveform.responseProcessing;

public class TransferFunctionUtils {
    /*
     * given a int, return the next power of 2 (the smalles power of 2 greater than num).
     */
    public static int next2(int num) {
        int result = 2;
        while (true) {
            if (result > num) {
                return result;
            } else {
                result *= 2;
            }
        }
    }
}
