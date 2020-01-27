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
package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import java.io.File;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;

import llnl.gnem.core.correlation.*;
import llnl.gnem.core.gui.plotting.Limits;
import llnl.gnem.core.gui.util.ExceptionDialog;
import llnl.gnem.core.gui.util.ProgressDialog;
import llnl.gnem.core.io.SAC.SACFile;
import llnl.gnem.core.io.SAC.SACHeader;
import llnl.gnem.core.signalprocessing.Sequence;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.apps.detection.core.dataObjects.WaveformSegment;

/**
 * Created by dodge1 Date: Feb 12, 2012 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class WriteSACFilesWorker extends SwingWorker<Void, Void> {

    private final ProgressDialog progress;
    private final File baseDirectory;
    private final Limits limits;

    public WriteSACFilesWorker(File baseDirectory) {
        this.baseDirectory = baseDirectory;
        progress = ProgressDialog.getInstance();
        progress.setTitle("Write Current Data as SAC files");
        progress.setVisible(true);
        progress.setProgressBarIndeterminate(true);
        limits = ClusterBuilderFrame.getInstance().getCurrentXLimits();
    }

    @Override
    protected Void doInBackground() throws Exception {
        double latestStart = -Double.MAX_VALUE;
        double earliestEnd = Double.MAX_VALUE;

        CorrelatedTracesModel ctModel = CorrelatedTracesModel.getInstance();
        for (CorrelationComponent cc : ctModel.getMatchingTraces()) {
            CorrelationTraceData td = (CorrelationTraceData) cc.getTraceData();
            float[] plotData = td.getPlotData();
            double traceStart = td.getTime().getEpochTime();
            double nominalPickTime = td.getNominalPick().getTime();
            double ccShift = cc.getShift();
            double start = traceStart - nominalPickTime + ccShift;
            double end = start + (plotData.length - 1) * td.getDelta();

            if (start > latestStart) {
                latestStart = start;
            }
            if (end < earliestEnd) {
                earliestEnd = end;
            }
        }
        if (latestStart < limits.getMin()) {
            latestStart = limits.getMin();
        }
        if (earliestEnd > limits.getMax()) {
            earliestEnd = limits.getMax();
        }
        int detectorid = ctModel.getCurrentDetectorid();
        File detectorDir = new File(baseDirectory, String.format("%d", detectorid));
        if (!detectorDir.exists()) {
            boolean created = detectorDir.mkdirs();
            if (!created) {
                throw new FileSystemException(String.format("Failed to create: (%s)!", detectorDir.getAbsolutePath()));
            }
        }

        Map<CorrelationComponent, DetectionWaveforms> components = ctModel.getComponentMap();
        for (CorrelationComponent cc : components.keySet()) {
            double shift = cc.getShift();
            double correlation = cc.getCorrelation();
            int detectionid = (int)cc.getEvent().getEvid();
            DetectionWaveforms dw = components.get(cc);
            for (CorrelationComponent cc2 : dw.getSegments()) {
                CorrelationTraceData td = (CorrelationTraceData) cc2.getTraceData();
                CssSeismogram tmp = new CssSeismogram(td.getBackupSeismogram());

                float[] theData = td.getBackupSeismogram().getData();
                double delta = td.getDelta();
                double traceStart = td.getTime().getEpochTime();
                double nominalPickTime = td.getNominalPick().getTime();
                double ccShift = cc.getShift();
                double start = traceStart - nominalPickTime + ccShift;
                double end = start + (theData.length - 1) * td.getDelta();
                int idx0 = 0;
                if (start < latestStart) {
                    idx0 = (int) Math.round((latestStart - start) / delta);
                }
                int idx1 = theData.length - 1;
                if (end > earliestEnd) {
                    idx1 = (int) Math.round((earliestEnd - start) / delta);
                }

                tmp.cut(idx0, idx1);
                WaveformSegment ws = new WaveformSegment(tmp);
                writeSacFile(detectionid,ws, detectorDir,shift, correlation);
            }
        }

        return null;

    }
    
    private void writeSacFile(int detectionid, WaveformSegment data, File detectorDir, double shift, double correlation) throws FileSystemException {
        WaveformSegment waveformSegment = data;
        SACHeader header = new SACHeader();
        header.setTime(new llnl.gnem.core.util.TimeT(waveformSegment.getTimeAsDouble()));
        header.b = 0;
        header.delta = (float) (1.0 / data.getSamprate());
        header.kstnm = waveformSegment.getSta();
        header.kcmpnm = waveformSegment.getChan();
        header.kuser0  = "shift";
        header.user[0] = (float)shift;
        header.kuser1 = "corr";
        header.user[1] = (float)correlation;
        File finalDir = makeDetectionDirectory(detectionid, detectorDir);
        File file = new File(finalDir, String.format("%s_%s.sac", waveformSegment.getSta(), waveformSegment.getChan()));
        Sequence sequence = new Sequence(waveformSegment.getData());
        SACFile sacfile = new SACFile(file, header, sequence);
        sacfile.write();
    }
    private File makeDetectionDirectory(int detectionid, File detectorDir) throws FileSystemException {
        File finalDir = new File(detectorDir, String.format("%d", detectionid));
        if (!finalDir.exists()) {
            boolean created = finalDir.mkdirs();
            if (!created) {
                throw new FileSystemException(String.format("Failed to create: (%s)!", finalDir.getAbsolutePath()));
            }
        }
        return finalDir;
    }
    

    @Override
    public void done() {
        try {
            progress.setVisible(false);
            get();
        } catch (InterruptedException | ExecutionException e) {
            if (!(e instanceof CancellationException)) {
                ExceptionDialog.displayError(e);
                ApplicationLogger.getInstance().log(Level.WARNING, "Error writing SAC files.", e);
            }
        }
    }
}
