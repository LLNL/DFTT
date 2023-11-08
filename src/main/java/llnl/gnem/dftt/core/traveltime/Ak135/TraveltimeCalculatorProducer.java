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
package llnl.gnem.dftt.core.traveltime.Ak135;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import llnl.gnem.dftt.core.traveltime.SinglePhaseTraveltimeCalculator;

/**
 *
 * @author dodge1
 */
public class TraveltimeCalculatorProducer implements Serializable {

    private final Map<String, EllipticityCorrection> ellipticities;
    private final Map<String, TTDistDepth> distDepths;
    private final Map<String, SinglePhaseTraveltimeCalculator> phaseCalculatorMap;
    static final long serialVersionUID = -2568109281687962369L;
    private static TraveltimeCalculatorProducer instance = null;
    private AirTraveltimeCalculator airCalculator;

    public static TraveltimeCalculatorProducer getInstance() throws IOException, ClassNotFoundException {
        if (instance == null) {
            instance = new TraveltimeCalculatorProducer();
            instance.initializeFromJar();
        }
        return instance;
    }

    private TraveltimeCalculatorProducer() {
        ellipticities = new ConcurrentHashMap<>();
        distDepths = new ConcurrentHashMap<>();
        airCalculator = new AirTraveltimeCalculator();
        phaseCalculatorMap = new ConcurrentHashMap<>();
    }

    public void replaceAirCalculator(AirTraveltimeCalculator calculator) {
        airCalculator = calculator;
        phaseCalculatorMap.clear(); //Remove cached calculators so new requests get this calculator
    }

    public Collection<String> getAllowablePhases() {
        return distDepths.keySet();
    }

    public TTDistDepth getDistDepth(String phase) {
        return distDepths.get(phase);
    }

    public EllipticityCorrection getEllipticityCorrection(String phase) {
        return ellipticities.get(phase);
    }

    public SinglePhaseTraveltimeCalculator getSinglePhaseTraveltimeCalculator(String phase) {
        SinglePhaseTraveltimeCalculator result = phaseCalculatorMap.get(phase);
        if (result == null) {
            result = getSinglePhaseTraveltimeCalculatorP(phase);
            phaseCalculatorMap.put(phase, result);
        }
        return result;
    }

    private SinglePhaseTraveltimeCalculator getSinglePhaseTraveltimeCalculatorP(String phase) {
        if (phase.equals("A")) {
            return airCalculator;
        }

        // When computing travel time, the whole phase corresponds to the P arrival
        if (phase.equalsIgnoreCase("Whole")) {
            phase = "P";
        }

        TTDistDepth distDepth = distDepths.get(phase);
        if (distDepth != null) {
            EllipticityCorrection ec = ellipticities.get(phase);
            if (ec != null) {
                ec = new EllipticityCorrection(ec);
            }
            return new SinglePhaseAk135TraveltimeCalculator(phase,
                    new TTDistDepth(distDepth),
                    ec);
        } else if (ConstantVelocityTraveltimeCalculator.supportsPhase(phase)) {
            return new ConstantVelocityTraveltimeCalculator(phase);
        } else {
            throw new IllegalArgumentException("Phase: " + phase + " not supported!");
        }

    }

    public void initializeFromJar() throws IOException, ClassNotFoundException {
        loadTTDistDepths();
        loadEllipticityCorrections();
    }

    private void loadTTDistDepths() throws IOException {
        PhaseMapper mapper = (String phase, InputStream stream) -> {
            distDepths.put(phase, new TTDistDepth(stream));
        };
        loadJar("ak135/TTDistDepth.jar", mapper);
    }

    private void loadEllipticityCorrections() throws IOException {
        PhaseMapper mapper = (String phase, InputStream stream) -> {
            ellipticities.put(phase, new EllipticityCorrection(stream));
        };
        loadJar("ak135/EllipticityCorrection.jar", mapper);
    }

    private void loadJar(String jarName, PhaseMapper mapper) throws IOException {
        URL resource = getClass().getClassLoader().getResource(jarName);
        ZipInputStream zip = new ZipInputStream(resource.openStream());

        ZipEntry ze;
        while ((ze = zip.getNextEntry()) != null) {
            String fileName = ze.getName();
            if (fileName.contains("ak135.")) {
                String phase = fileName.substring(fileName.lastIndexOf(".") + 1);
                mapper.map(phase, zip);
            }
        }
    }

    private interface PhaseMapper {

        public void map(String phase, InputStream stream) throws IOException;
    }
}
