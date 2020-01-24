package llnl.gnem.core.waveform;

import llnl.gnem.core.util.Geometry.EModel;

public enum DistanceType {

    km(EModel.getKilometersPerDegree()), degrees(1.0);
    private final double scale;

    DistanceType(double value) {
        scale = value;
    }

    public double getScaleFactor() {
        return scale;
    }
}
