/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util.randomNumbers;

import java.util.Random;

/**
 *
 * @author dodge1
 */
public class SimpleRandom extends BaseRandomAlgorithm {

    private final Random myRandom;

    public SimpleRandom() {
        myRandom = new Random();
    }

    public SimpleRandom(final long seed) {
        myRandom = new Random(seed);

    }

    @Override
    public double nextDouble() {
        return myRandom.nextDouble();
    }

    @Override
    public int nextInt() {
        return myRandom.nextInt();
    }

    @Override
    public int nextInt(int n) {
        return myRandom.nextInt(n);
    }

    @Override
    public long nextLong() {
        return myRandom.nextLong();
    }

    @Override
    public void resetSeed(long seed) {
        myRandom.setSeed(seed);
    }
   
}
