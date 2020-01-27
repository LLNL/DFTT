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
package llnl.gnem.core.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum InstrumentCode {

    
    
    H(InstrumentType.SEISMOMETER, "High Gain Seismometer", OrientationCode.getSeismometerCodes()),
    L(InstrumentType.SEISMOMETER, "Low Gain Seismometer", OrientationCode.getSeismometerCodes()),
    G(InstrumentType.SEISMOMETER, "Gravimeter", OrientationCode.getSeismometerCodes()),
    M(InstrumentType.SEISMOMETER, "Mass Position Seismometer", OrientationCode.getSeismometerCodes()),
    N(InstrumentType.SEISMOMETER, "Accelerometer", OrientationCode.getSeismometerCodes()),
    A(InstrumentType.TILT_METER, "Tilt Meter", OrientationCode.getTiltCodes()),
    B(InstrumentType.CREEP_METER, "Creep Meter", OrientationCode.getUnknownCode()),
    C(InstrumentType.CALIBRATION_INPUT, "Calibration Input", OrientationCode.getCalibrationCodes()),
    D(InstrumentType.PRESSURE, "Pressure", OrientationCode.getPressureCodes()),
    E(InstrumentType.TEST_POINT, "Electronic Test Point", Arrays.asList(OrientationCode.values())),
    F(InstrumentType.MAGNETOMETER, "Magnetometer", OrientationCode.getMagnetometerCodes()),
    I(InstrumentType.HUMIDITY, "Humidity", OrientationCode.getHumidityCodes()),
    J(InstrumentType.ROTATIONAL_SENSOR, "Rotational Sensor", OrientationCode.getSeismometerCodes()),
    K(InstrumentType.TEMPERATURE, "Temperature", OrientationCode.getHumidityCodes()),
    O(InstrumentType.WATER_CURRENT, "Water Current", OrientationCode.getUnknownCode()),
    P(InstrumentType.GEOPHONE, "Geophone", OrientationCode.getMagnetometerCodes()),
    Q(InstrumentType.ELECTRIC_POTENTIAL, "Electric Potential", OrientationCode.getUnknownCode()),
    R(InstrumentType.RAINFALL, "Rainfall", OrientationCode.getUnknownCode()),
    S(InstrumentType.LINEAR_STRAIN, "Linear Strain", OrientationCode.getMagnetometerCodes()),
    T(InstrumentType.TIDE, "Tide", OrientationCode.getTideCode()),
    U(InstrumentType.BOLOMETER, "Bolometer", OrientationCode.getUnknownCode()),
    V(InstrumentType.VOLUMETRIC_STRAIN, "Volumetric Strain", OrientationCode.getUnknownCode()),
    W(InstrumentType.WIND, "Wind", OrientationCode.getWindCodes()),
    X(InstrumentType.DERIVED, "Derived", OrientationCode.getUnknownCode()),
    Y(InstrumentType.NON_SPECIFIC, "Non-Specific", OrientationCode.getUnknownCode()),
    Z(InstrumentType.SYNTHESIZED_BEAM, "Synthesized Beam", OrientationCode.getBeamCodes());
    private final InstrumentType insType;
    private final String descrip;
    private final ArrayList<OrientationCode> allowedOrientations;

    private InstrumentCode(InstrumentType type, String descrip, List<OrientationCode> allowed) {
        insType = type;
        this.descrip = descrip;
        this.allowedOrientations = new ArrayList<>(allowed);
    }

    /**
     * @return the insType
     */
    public InstrumentType getInsType() {
        return insType;
    }

    /**
     * @return the descrip
     */
    public String getDescrip() {
        return descrip;
    }
    
    public List<OrientationCode> getAllowedOrientations()
    {
        return new ArrayList<>(allowedOrientations);
    }
}
