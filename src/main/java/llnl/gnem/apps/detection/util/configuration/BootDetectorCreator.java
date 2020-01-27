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
package llnl.gnem.apps.detection.util.configuration;

import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class BootDetectorCreator {

    private static final String sep = System.getProperty("line.separator");
    private final File bootDetDir;
    private final String refSta;
    private final Collection<StreamKey> staChansToUse;
    private final double staLtaThresh;
    DetectorType bootDetectorType;
    private final Double beamAzimuth;
    private final Double beamVelocity;

    public BootDetectorCreator(File stream1Dir,
            String refSta,
            double staLtaThreshold,
            Collection<StreamKey> staChansToUse,
            DetectorType bootDetectorType,
            Double beamAzimuth,
            Double beamVelocity) {
        this.staChansToUse = new ArrayList<>(staChansToUse);
        this.refSta = refSta;
        this.staLtaThresh = staLtaThreshold;
        this.bootDetectorType = bootDetectorType;
        this.beamAzimuth = beamAzimuth;
        this.beamVelocity = beamVelocity;

        bootDetDir = new File(stream1Dir, "bootDetectors");
        if (!bootDetDir.mkdirs()) {
            throw new IllegalStateException("Failed creating directory: " + bootDetDir);
        }
    }

    public File createBootDetectors(File bulletinFile) throws FileNotFoundException {
        if (null != bootDetectorType) {
            switch (bootDetectorType) {
                case STALTA:
                    File staLtaDetectorFile = writeStaLtaFile();
                    return writeBootDetectorsFile(staLtaDetectorFile, bootDetectorType);
                case ARRAYPOWER:
                    File arrayDetectorFile = writeArrayDetectorFile();
                    return writeBootDetectorsFile(arrayDetectorFile, bootDetectorType);
                case BULLETIN:
                    File bulletinDetectorFile = writeBulletinDetectorFile(bulletinFile);
                    return writeBootDetectorsFile(bulletinDetectorFile, bootDetectorType);
                default:
                    throw new IllegalStateException("Unsupported boot detector type: " + bootDetectorType);
            }
        }
        return null;
    }

    private File writeBootDetectorsFile(File detectorFile, DetectorType bootDetectorType) throws FileNotFoundException {
        File bootDetFile = new File(bootDetDir, "BootDetector.txt");
        try (PrintWriter writer = new PrintWriter(bootDetFile)) {
            String tmp = detectorFile.getAbsolutePath();
            writer.print(String.format("%s  %s%s", bootDetectorType.toString(), tmp.replace("\\", "\\\\"), sep));

            return bootDetFile;
        }
    }

    private File writeArrayDetectorFile() throws FileNotFoundException {
        File arrayFile = new File(bootDetDir, "array_params.txt");
        try (PrintWriter writer = new PrintWriter(arrayFile)) {
            printStaChanSection(writer);
            writer.print("detectorType = ArrayPower" + sep);
            writer.print(String.format("threshold=%f %s", staLtaThresh, sep));
            writer.print("blackoutPeriod=10" + sep);
            writer.print("STADuration=3" + sep);
            writer.print("LTADuration=30.0" + sep);
            writer.print("gapDuration=1.5" + sep);
            writer.print("enableSpawning=true" + sep);
            writer.print(String.format("backAzimuth=%f %s", beamAzimuth, sep));
            writer.print(String.format("velocity=%f %s", beamVelocity, sep));
            return arrayFile;
        }
    }

    private File writeStaLtaFile() throws FileNotFoundException {
        File staltaFile = new File(bootDetDir, "stalta_params.txt");
        try (PrintWriter writer = new PrintWriter(staltaFile)) {
            printStaChanSection(writer);
            writer.print("detectorType = STALTA" + sep);
            writer.print(String.format("threshold=%f %s", staLtaThresh, sep));
            writer.print("blackoutPeriod=10" + sep);
            writer.print("STADuration=3" + sep);
            writer.print("LTADuration=30.0" + sep);
            writer.print("gapDuration=1.5" + sep);
            writer.print("enableSpawning=true" + sep);
            return staltaFile;
        }
    }

    private File writeBulletinDetectorFile(File bulletinFile) throws FileNotFoundException {
        File staltaFile = new File(bootDetDir, "bulletin_params.txt");
        try (PrintWriter writer = new PrintWriter(staltaFile)) {
            printStaChanSection(writer);
            writer.print("detectorType = Bulletin" + sep);
            writer.print(String.format("threshold=%f %s", 0.5, sep));
            writer.print("blackoutPeriod=10" + sep);
            writer.print("enableSpawning=true" + sep);
            writer.printf("BulletinFile=%s%s", bulletinFile.getAbsolutePath(), sep);
            return staltaFile;
        }
    }

    private void printStaChanSection(PrintWriter writer) {
        writer.print(".StaChanList" + sep);
        for (StreamKey sck : staChansToUse) {
            writer.print(String.format("%s %s  1%s", sck.getSta(), sck.getChan(), sep));
        }
        writer.print(".EndList" + sep);
    }
}
