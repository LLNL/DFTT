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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class ExampleTemplateCreator {

    private static final String sep = System.getProperty("line.separator");
    private final File stream1Dir;
    private File allTemplatesDir;
    private final ArrayList<StreamKey> staChansToUse;

    public ExampleTemplateCreator(File stream1Dir,Collection<StreamKey> staChansToUse) {
        this.stream1Dir = stream1Dir;
        this.staChansToUse = new ArrayList<>(staChansToUse);
    }

    public Collection<File> makeTemplateDirectories() throws FileNotFoundException {
        Collection<File> result = new ArrayList<>();
        allTemplatesDir = new File(stream1Dir, "templates");
        if (!allTemplatesDir.mkdirs()) {
            throw new IllegalStateException("Failed creating template directory: " + allTemplatesDir.getAbsolutePath());
        }
        for (int j = 0; j < 3; ++j) {
            String template = "template" + j;
            File templateDir = new File(allTemplatesDir, template);
            if (!templateDir.mkdirs()) {
                throw new IllegalStateException("Failed creating template directory: " + templateDir);
            }
            makeTemplateReadme(templateDir);
            result.add(makeTemplateDescriptorFile(templateDir, template));
            for (int k = 0; k < 2; ++k) {
                File eventDir = new File(templateDir, String.format("%08d", k));
                if (!eventDir.mkdirs()) {
                    throw new IllegalStateException("Failed creating event directory: " + eventDir);
                }
                makeEventDirReadme(eventDir);
            }
        }

        return result;
    }

    private void makeTemplateReadme(File templateDir) throws FileNotFoundException {
        File readmeFile = new File(templateDir, "README.txt");
        try (PrintWriter writer = new PrintWriter(readmeFile)) {
            writer.print("Each template is composed of 1 - N events." + sep);
            writer.print("For each event there is an event directory, and within that directory are the SAC file templates." + sep);
            writer.print("Event files may be named arbitrarily, but must match the regular expression given in DetectorsToCreate.txt." + sep);
        }

    }

    private void makeEventDirReadme(File eventDir) throws FileNotFoundException {
        File readmeFile = new File(eventDir, "README.txt");
        try (PrintWriter writer = new PrintWriter(readmeFile)) {
            writer.print("Within this directory should be a collection of SAC files, one per stream channel." + sep);
            writer.print("The files should have the same start and stop times, and the offset to the template waveform should be the same from event to event." + sep);
            writer.print("Event files may be named arbitrarily, but must match the regular expression given in DetectorsToCreate.txt." + sep);
        }
    }

    private File makeTemplateDescriptorFile(File templateDir, String templateName) throws FileNotFoundException {
        File descriptorFile = new File(templateDir, templateName + ".txt");
        try (PrintWriter writer = new PrintWriter(descriptorFile)) {
            printStaChanSection(writer);
            writer.print("detectorType = Subspace" + sep);
            String tmp = templateDir.getAbsolutePath();
            writer.print(String.format("eventDataPath=%s%s", tmp.replace("\\", "\\\\"), sep));
            writer.print("eventDirectoryPattern=[0-9]+" + sep);
            writer.print("eventFilePattern = .+sac" + sep);
            writer.print("templateStart=25.0" + sep);
            writer.print("templateDuration=20.0" + sep);
            writer.print("threshold=0.4" + sep);
            writer.print("blackoutPeriod=3.0" + sep);
            writer.print("energyCapture=0.9" + sep);
 
            return descriptorFile;
        }

    }
    
    
    private void printStaChanSection(PrintWriter writer) {
        writer.print(".StaChanList" + sep);
        for( StreamKey sck : staChansToUse){
            writer.print(String.format("%s %s%s", sck.getSta(), sck.getChan(), sep));
        }
        writer.print(".EndList" + sep);
    }

}
