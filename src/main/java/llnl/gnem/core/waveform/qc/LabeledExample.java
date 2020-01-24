package llnl.gnem.core.waveform.qc;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author addair1
 */
public class LabeledExample<T extends QCFeatures> {
    private final T features;
    private final List<String> nominals;
    private final Enum label;
    
    public LabeledExample(T features, Enum label) {
        this(features, new ArrayList<String>(), label);
    }
    
    public LabeledExample(T features, List<String> nominals, Enum label) {
        this.features = features;
        this.nominals = nominals;
        this.label = label;
    }

    public T getFeatures() {
        return features;
    }
    
    public List<String> getNominals() {
        return nominals;
    }

    public Enum getLabel() {
        return label;
    }
}
