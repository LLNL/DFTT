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

public class RandomAlgorithmFactory {

    private static AlgorithmType alogorithmType = AlgorithmType.Simple;
    private static final int RANDOM_SEED = 129875439;
    
    public static RandomAlgorithm getAlgorithm() {
        return getAlgorithm(RANDOM_SEED);
    }

    public static RandomAlgorithm getAlgorithm(long seed) {
        switch (alogorithmType) {
            case Simple:
                return new SimpleRandom(seed);
            case XorShift:
                return new XorShiftRandom(seed);
            case MTRandom:
                return new MTRandom(seed);
            case SecureRandom:
                return new SecureRandomAlg(seed);
            default:
                throw new IllegalStateException("Do no know how to create algorithm of type : " + alogorithmType);

        }

    }
    
    public static void setAlgorithmType( AlgorithmType type){
        alogorithmType = type;
    }

    public static long getSeed() {
        return RANDOM_SEED;
    }

}