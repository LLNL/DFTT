package llnl.gnem.core.util.randomNumbers;

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