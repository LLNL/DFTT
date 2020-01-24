/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.continuous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.gui.plotting.TickMetrics;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.PlotAxis;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.YAxis;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.continuous.ContinuousSeismogram;
import llnl.gnem.core.waveform.continuous.ContinuousSeismogramModel;
import llnl.gnem.core.waveform.continuous.ContinuousSeismogramView;
import llnl.gnem.core.waveform.merge.MergeException;

/**
 *
 * @author dodge1
 */
public class ContinuousSeismogramPlot extends JMultiAxisPlot implements ContinuousSeismogramView {

    protected final JSubplot subplot;
    private final ContinuousSeismogramModel model;
    private final Map<ContinuousSeismogramLine, StreamKey> lineKeyMap;

    public ContinuousSeismogramPlot(ContinuousSeismogramModel model) {
        super(JMultiAxisPlot.XAxisType.EpochTime);
        subplot = this.addSubplot();

        subplot.getYaxis().setTicksVisible(false);
        this.model = model;
        lineKeyMap = new HashMap<>();
        this.setMouseMode(MouseMode.ZOOM_ONLY);
    }

    @Override
    public void clear() {
        subplot.Clear();
        lineKeyMap.clear();
        repaint();
        setMouseMode(MouseMode.ZOOM_ONLY);
    }

    protected boolean hasData() {
        return !lineKeyMap.isEmpty();
    }

    @Override
    public void seismogramWasAdded(StreamKey identifier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection<CssSeismogram> getVisibleSeismograms() throws MergeException {
        double minTime = getXaxis().getMin();
        double maxTime = getXaxis().getMax();
        Epoch epoch = new Epoch(minTime, maxTime);

        List<StreamKey> keys = model.getSeismogramList();
        ArrayList<CssSeismogram> result = new ArrayList<>();
        for (StreamKey key : keys) {
            ContinuousSeismogram catalog = model.getContinuousSeismogram(key);
            CssSeismogram seis = catalog.getSegment(epoch);
            if (seis != null) {
                result.add(seis);
            }
        }
        return result;
    }

    @Override
    public void replaceContents() {
        this.clear();
        List<StreamKey> keys = model.getSeismogramList();
        int numLines = keys.size();
        if (numLines > 0) {
            double ymin = 0;
            double ymax = 1;
            double xmin = Double.MAX_VALUE;
            double xmax = -xmin;
            double spacing = 1.0 / (numLines + 1);
            int count = 0;
            for (StreamKey key : keys) {
                ContinuousSeismogram catalog = model.getContinuousSeismogram(key);
                double yValue = ++count * spacing - spacing / 2;
                ContinuousSeismogramLine line = new ContinuousSeismogramLine(catalog, (float) yValue, (float) spacing);
                subplot.AddPlotObject(line);
                lineKeyMap.put(line, key);
                double tmp = catalog.getTime();
                if (tmp < xmin) {
                    xmin = tmp;
                }
                tmp = catalog.getEnd();
                if (tmp > xmax) {
                    xmax = tmp;
                }
            }
            subplot.SetAxisLimits(xmin, xmax, ymin, ymax);

        }
        repaint();
    }

    public void magnifyTraces() {
        scaleTraces(2.0);

    }

    private void scaleTraces(double factor) {
        for (ContinuousSeismogramLine line : lineKeyMap.keySet()) {
            line.scaleTraces(factor);
        }
        this.repaint();
    }

    public void reduceTraces() {
        scaleTraces(0.5);
    }

    @Override
    public void scaleAllTraces(boolean resetYlimits) {
        scaleAllTraces(getXaxis().getMin(), getXaxis().getMax(), resetYlimits);
    }

    @Override
    public void scaleAllTraces(double xmin, double xmax, boolean autoRescaleY) {
        YAxis axis = subplot.getYaxis();

        double Ymax = Double.NEGATIVE_INFINITY;
        double Ymin = Double.POSITIVE_INFINITY;
        for (ContinuousSeismogramLine line : lineKeyMap.keySet()) {
            PairT<Double, Double> minMax = line.ScaleTraces(xmin, xmax);
            Ymin = Math.min(Ymin, minMax.getFirst());
            Ymax = Math.max(Ymax, minMax.getSecond());
        }
        if (autoRescaleY) {
            TickMetrics tm = PlotAxis.defineAxis(Ymin, Ymax);
            axis.setMin(tm.getMin());
            axis.setMax(tm.getMax());
        }
        this.repaint();
    }

}
