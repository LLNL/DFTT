package llnl.gnem.core.util.randomNumbers;

public interface RandomAlgorithm {
    //==========================================================================
    // Interface Name: RandomAlgorithm
    //
    //==========================================================================

// 	public int next( int bits );
    double nextDouble();

    int nextInt();

    int nextInt(int n);

    long nextLong();

    int getBoundedInt(int lower, int upper);

    double getBoundedDouble(double lower, double upper);

    double nextGaussian(double mean, double std);

    public void resetSeed(long seed);
}
