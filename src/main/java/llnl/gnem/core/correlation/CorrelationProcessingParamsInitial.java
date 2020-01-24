package llnl.gnem.core.correlation;

public class CorrelationProcessingParamsInitial {

    private final double maxRadius;
    private final int maxRows;
    private final String referencePhase;
    private final double clusterThreshold;
    private final double selectionThreshold;

    public CorrelationProcessingParamsInitial() {
        maxRadius = 10;
        maxRows = 50;
        referencePhase = "P";
        clusterThreshold = 0.8;
        selectionThreshold = 0.8;
    }

    public double getClusterThreshold() {
        return clusterThreshold;
    }

    public double getMaxRadius() {
        return maxRadius;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public String getReferencePhase() {
        return referencePhase;
    }

    public double getSelectionThreshold() {
        return selectionThreshold;
    }

}
