package llnl.gnem.core.util.randomNumbers;

import java.security.SecureRandom;

public class SecureRandomAlg extends BaseRandomAlgorithm {
    //==========================================================================
    // Class Name: SecureRandomAlg
    //
    //==========================================================================

    SecureRandom rnd;

    SecureRandomAlg(long seed) {

        //======================================================================
        //
        //  Purpose: Initialize the class
        //
        //  Arguments:
        //
        //  seed   - 
        //
        //  Return: Class instance
        //
        //======================================================================

        rnd = new SecureRandom();
        rnd.setSeed(seed);
    }

// 	public int next( int bits ) {
//         int i = rnd.next(bits);
//         return i;
//     }
    @Override
    public double nextDouble() {
        return rnd.nextDouble();
    }

    @Override
    public int nextInt() {
        return rnd.nextInt();
    }

    @Override
    public int nextInt(int n) {
        return rnd.nextInt(n);
    }

    @Override
    public long nextLong() {
        return rnd.nextLong();
    }

    @Override
    public void resetSeed(long seed) {
        rnd.setSeed(seed);
    }
}