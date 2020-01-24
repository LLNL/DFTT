package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.AbstractEmpiricalSpecification;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import llnl.gnem.core.util.StreamKey;

public class SubspaceSpecification extends AbstractEmpiricalSpecification {

    private final float energyCaptureThreshold;

    private static final long serialVersionUID = 7508035857852574537L;

    /*
   * Constructor to support instantiation of SubspaceDetectors from a flat file specification
     */
    public SubspaceSpecification(InputStream stream) throws IOException {

        super(stream);

        energyCaptureThreshold = Float.parseFloat(parameterList.getProperty("energyCapture", "0.7"));

        triggerPositionType = TriggerPositionType.STATISTIC_MAX;
        detectorType = DetectorType.SUBSPACE;
    }

    /*
   * Constructor to support instantiation of detectors stored in the database.
     */
    public SubspaceSpecification( float                  threshold,
                                  float                  blackoutPeriod,
                                  double                 offsetSecondsToWindowStart,
                                  double                 windowDurationSeconds,
                                  float                  energyCaptureThreshold,
                                  Collection< StreamKey> staChanList    ) {

        super( threshold, blackoutPeriod, staChanList, offsetSecondsToWindowStart, windowDurationSeconds );

        this.energyCaptureThreshold = energyCaptureThreshold;
        triggerPositionType = TriggerPositionType.STATISTIC_MAX;
        detectorType = DetectorType.SUBSPACE;
    }

    public float getEnergyCaptureThreshold() {
        return energyCaptureThreshold;
    }

    @Override
    public boolean spawningEnabled() {
        return false;
    }

    public static void printSpecificationTemplate(PrintStream ps) {

        AbstractEmpiricalSpecification.printSpecificationTemplate(ps);

        ps.println("energyCapture              = <0.0 <= energy capture value <= 1.0>");
        ps.println("detectorType               = Subspace");
    }

    @Override
    public void printSpecification(PrintStream ps) {

        super.printSpecification(ps);

        ps.println();
        ps.println("energyCapture              = " + energyCaptureThreshold);
        ps.println("spawning enabled: false");
    }

}
