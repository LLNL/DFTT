/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.dftt.core.util.randomNumbers;

import java.util.Random;

/**
 * Fast, high-quality pseudorandom number generator suggested by George
 * Marsaglia in <a
 * href="http://www.jstatsoft.org/v08/i14/paper/">&ldquo;Xorshift
 * RNGs&rdquo;</a>, <i>Journal of Statistical Software</i>, 8:1&minus;6, 2003.
 * Calls to {@link #nextLong()} will be one order of magnitude faster than {@link Random}'s.
 * <p> This class extends {@link Random}, overriding (as usual) the {@link Random#next(int)}
 * method. Nonetheless, since the generator is inherently 64-bit also {@link Random#nextLong()}
 * and {@link Random#nextDouble()} have been overridden for speed (preserving,
 * of course, {@link Random}'s semantics).
 */
public class XorShiftRandom extends BaseRandomAlgorithm {
    //==========================================================================
    // Class Name: XorShiftRandom
    //
    //==========================================================================

    private static final long serialVersionUID = 1L;
    private final Random myRandom;
    /**
     * The internal state (and last returned value) of the algorithm.
     */
    private long x;

    public XorShiftRandom() {
        myRandom = new Random();
      
    }

    public XorShiftRandom(final long seed) {
        myRandom = new Random(seed);
        x = seed;
    }

    public int next(int bits) {
        return (int) (nextLong() >>> (64 - bits));
    }

    @Override
    public long nextLong() {
        x ^= x << 13;
        x ^= x >>> 7;
        return x ^= (x << 17);
    }

    @Override
    public double nextDouble() {
        return (nextLong() >>> 11) / (double) (1L << 53);
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
    public void resetSeed(long seed) {
        myRandom.setSeed(seed);
        x = seed;
    }

}