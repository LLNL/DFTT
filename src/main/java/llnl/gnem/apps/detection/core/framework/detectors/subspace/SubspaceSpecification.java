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
package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import llnl.gnem.apps.detection.core.dataObjects.AbstractEmpiricalSpecification;
import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
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
