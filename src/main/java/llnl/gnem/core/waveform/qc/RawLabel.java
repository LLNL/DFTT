package llnl.gnem.core.waveform.qc;

/**
 *
 * @author addair1
 */
public enum RawLabel {
    artifact, signal;
    
    public static RawLabel valueOf(QualityLabel quality) {
        switch (quality) {
            case artifact:
                return artifact;
            case signal:
            case no_signal:
            case valid:
                return signal;
            default:
                throw new IllegalArgumentException("Cannot interpret " + quality.name() + " as a raw feature label");
        }
    }
}
