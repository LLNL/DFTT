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
package llnl.gnem.apps.detection.core.framework;

import java.io.IOException;

import llnl.gnem.apps.detection.core.dataObjects.DetectorSpecification;
import llnl.gnem.apps.detection.core.dataObjects.PreprocessorParams;
import llnl.gnem.apps.detection.core.framework.detectors.Detector;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayPowerDetector;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationDetector;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTADetector;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceDetector;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;

/**
 *
 * @author dodge1
 */
public class FileBasedDetectorFactory {

    public static Detector createDetectorFromFiles(DetectorSpecification spec, int detectorid, ConcreteStreamProcessor processor, int decimatedBlockSize)
            throws IOException, UnsupportedOperationException {
        PreprocessorParams params = processor.getParams();
        String streamName = processor.getStreamName();
        double decimatedSampleRate = params.getPreprocessorParams().getDecimatedSampleRate();

        Detector detector = null;
        switch (spec.getDetectorType()) {
        case SUBSPACE:
            detector = new SubspaceDetector(detectorid, (SubspaceSpecification) spec, params, decimatedSampleRate, streamName, processor.getFFTSize(), decimatedBlockSize);
            break;
        case ARRAY_CORRELATION:
            detector = new ArrayCorrelationDetector(detectorid, (ArrayCorrelationSpecification) spec, params, decimatedSampleRate, streamName, decimatedBlockSize);
            break;
        case STALTA:
            detector = new STALTADetector(detectorid, (STALTASpecification) spec, decimatedSampleRate, streamName, decimatedBlockSize);
            break;
        case ARRAYPOWER:
            detector = new ArrayPowerDetector(detectorid, (ArrayDetectorSpecification) spec, decimatedSampleRate, streamName, decimatedBlockSize);
            break;
        case FSTATISTIC:
            throw new UnsupportedOperationException("FStatistic not implemented");
        default:
            throw new UnsupportedOperationException("Detector type unspecified");
        }
        return detector;
    }

}
