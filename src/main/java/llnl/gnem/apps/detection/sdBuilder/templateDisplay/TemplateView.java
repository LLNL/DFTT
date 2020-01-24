/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import llnl.gnem.apps.detection.core.framework.detectors.EmpiricalTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.arrayCorrelation.ArrayCorrelationTemplate;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceTemplate;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.DetectorProjection;
import llnl.gnem.core.gui.plotting.PaintMode;
import llnl.gnem.core.gui.plotting.PenStyle;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PinnedText;
import llnl.gnem.core.signalprocessing.SpectralOps;
import llnl.gnem.core.signalprocessing.statistics.SignalPairStats;
import llnl.gnem.core.signalprocessing.statistics.TimeBandwidthComponents;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class TemplateView extends JMultiAxisPlot {

    private static final long serialVersionUID = -6755410524791532174L;

    Map<StreamKey, JSubplot> streamPlotMap;
    Map<StreamKey, Line> streamLineMap;
    private int dimension = 0;
    private DimensionSelector selector;

    public TemplateView() {
        this.getSubplotManager().setplotSpacing(0);
        streamPlotMap = new HashMap<>();
        streamLineMap = new HashMap<>();
    }

    void templateWasUpdated() {
        clear();
        streamPlotMap.clear();
        streamLineMap.clear();
        dimension = 0;
        selector.reset();
        int numDimensions = plotSingleDimension(0);

        if (selector != null) {
            selector.updateDimensions(numDimensions);
        }
    }

    private void plotTemplateDimension(EmpiricalTemplate templateIn, int numDimensions, double currentSingularValue) {
        float[][] data = null;

        if (templateIn instanceof SubspaceTemplate) {
            SubspaceTemplate template = (SubspaceTemplate) templateIn;
            data = template.getRepresentation().get(dimension);
        } else if (templateIn instanceof ArrayCorrelationTemplate) {
            ArrayCorrelationTemplate template = (ArrayCorrelationTemplate) templateIn;
            data = new float[template.getnchannels()][template.getTemplateLength()];
            for (int j = 0; j < template.getnchannels(); ++j) {
                float[][] f = template.getRepresentation().get(j);
                System.arraycopy(f[dimension], 0, data[j], 0, template.getTemplateLength());
            }

        }
        if (data == null) {
            return;
        }

        double rate = templateIn.getProcessingParameters().samplingRate / templateIn.getProcessingParameters().decrate;

        for (int j = 0; j < data.length; ++j) {
            StreamKey key = templateIn.getStaChanList().get(j);
            JSubplot plot = this.addSubplot();
            plot.Plot(0.0, 1.0 / rate, data[j]);
            streamPlotMap.put(key, plot);
            String label = key.toString();
            String tmp = getTBPLabel(data[j], rate);
            label = label + "  " + tmp;
            tmp = getEntropyLabel(data[j]);
            label = label + "  " + tmp;
            PinnedText text = new PinnedText(5.0, 3.0, label);
            plot.AddPlotObject(text);
        }

        if (templateIn instanceof SubspaceTemplate) {
            SubspaceTemplate template = (SubspaceTemplate) templateIn;
            for (int j = 0; j < data.length; ++j) {
                StreamKey key = templateIn.getStaChanList().get(j);
                JSubplot plot = streamPlotMap.get(key);
                TimeBandwidthComponents tbc = template.getTimeFreqStats(dimension, j);
                double centroid = tbc.getTimeCentroid();
                VPickLine vpl = new VPickLine((float) centroid, 0.8, "centroid");
                vpl.getWindow().setDuration(tbc.getTimeSigma());
                vpl.getWindow().setVisible(true);
                vpl.setAllDraggable(false);
                plot.AddPlotObject(vpl);
            }
        }

        String detectorInfo = TemplateModel.getInstance().getDetectorInfo();
        int detectorid = TemplateModel.getInstance().getDetectorid();
        String msg = String.format("Detector %d (%s) Template for dimension %d of %d: Singular value = %f, decimation rate = %d",
                detectorid, detectorInfo, dimension, numDimensions,
                currentSingularValue, templateIn.getProcessingParameters().decrate);
        this.getTitle().setText(msg);
        this.getXaxis().setLabelText("seconds");

    }

    private String getEntropyLabel(float[] ccf) {

        double entropy = SpectralOps.computeEntropy(ccf);
        return String.format("Entropy = %5.2f", entropy);
    }

    private String getTBPLabel(float[] ccf, double rate) {
        double dof = SeriesMath.getDegreesOfFreedom(ccf, 1.0 / rate);

        TimeBandwidthComponents tbc1 = SignalPairStats.computeTimeBandwidthProduct(ccf, 1 / rate);
        String msgx = String.format("TBP = %5.2f, DOF = %3.0f", tbc1.getTBP(), dof);
        return msgx;
    }

    void setSelector(DimensionSelector selector) {
        this.selector = selector;
    }

    void setDimension(int dimension) {
        this.dimension = dimension;
        this.clear();
        streamPlotMap.clear();
        streamLineMap.clear();
        plotSingleDimension(dimension);
    }

    private int plotSingleDimension(int dim) {
        clear();
        streamPlotMap.clear();
        streamLineMap.clear();
        EmpiricalTemplate template = TemplateModel.getInstance().getCurrentTemplate();
        int numDimensions = 0;
        if (template != null) {

            double currentSingularValue = 0;
            if (template instanceof SubspaceTemplate) {
                double[] sv = ((SubspaceTemplate) template).getSingularValues();
                numDimensions = sv.length;
                currentSingularValue = sv[dim];
            } else if (template instanceof ArrayCorrelationTemplate) {
                ArrayList<double[]> tmp = ((ArrayCorrelationTemplate) template).getSingularValues();
                for (double[] a : tmp) {
                    numDimensions = a.length;
                    currentSingularValue += a[dim];
                }
                currentSingularValue /= tmp.size();
            }
            plotTemplateDimension(template, numDimensions, currentSingularValue);
        }
        this.repaint();
        return numDimensions;
    }

    void secondTemplateAdded(SubspaceTemplate template, DetectorProjection detectorProjection) {

        ArrayList<float[][]> allData = template.getRepresentation();
        if (dimension > allData.size() - 1) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Comparison template is lower rank than current template!");
            return;
        }
        float[][] data = allData.get(dimension);
        double rate = template.getProcessingParameters().samplingRate / template.getProcessingParameters().decrate;
        for (int j = 0; j < data.length; ++j) {
            StreamKey key = template.getStaChanList().get(j);
            JSubplot plot = streamPlotMap.get(key);
            Line oldLine = streamLineMap.get(key);
            if (oldLine != null) {
                plot.DeletePlotObject(oldLine);
            }
            if (plot != null) {
                double delay = detectorProjection.getShift() / rate;
                Line line = new Line(-delay, 1.0 / rate, data[j], Color.red, PaintMode.COPY, PenStyle.DASH, 1);
                streamLineMap.put(key, line);
                plot.AddPlotObject(line);
            }
        }
        repaint();
    }

}
