package llnl.gnem.core.gui.waveform.continuous;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.seriesMathHelpers.MinMax;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.continuous.ContinuousSeismogram;

public class ContinuousSeismogramLine extends PlotObject {

    private final Map<Line, CssSeismogram> lineSegmentMap;

    private final XPinnedText text;

    public ContinuousSeismogramLine(ContinuousSeismogram seis, float yPos, float yRange) {
        lineSegmentMap = new HashMap<>();
        double mean = seis.getMean();
        MinMax minmax = seis.getMinMax();
        double dataRange = minmax.getRange();
        Collection<CssSeismogram> segments = seis.getSegments();
        double scale = dataRange > 0 ? yRange / dataRange : 1;
        for (CssSeismogram segment : segments) {
            double start = segment.getTimeAsDouble();
            double dt = segment.getDelta();
            float[] data = segment.getData();
            data = SeriesMath.Add(data, -mean);
            SeriesMath.MultiplyScalar(data, scale);
            data = SeriesMath.Add(data, yPos);
            Line line = new Line(start, dt, data);
            lineSegmentMap.put(line, segment);
        }

        StreamKey key = seis.getIdentifier();

        text = new XPinnedText(2.0, yPos, String.format("%s - %s", key.getSta(), key.getChan()));
        text.setVerticalAlignment(VertAlignment.BOTTOM);
    }

    @Override
    public void render(Graphics g, JBasicPlot owner) {
        for (Line line : lineSegmentMap.keySet()) {
            line.render(g, owner);
        }
        text.render(g, owner);
    }

    @Override
    public void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy) {
        for (Line line : lineSegmentMap.keySet()) {
            line.ChangePosition(owner, graphics, dx, dy);
        }
    }

    public void scaleTraces(double factor) {
        for (Line line : lineSegmentMap.keySet()) {
            float[] data = line.getYdata();
            double mean = SeriesMath.getMean(data);
            SeriesMath.RemoveMean(data);
            SeriesMath.MultiplyScalar(data, factor);
            data = SeriesMath.Add(data, mean);
            line.replaceYarray(data);
        }
    }

    public PairT<Double, Double> ScaleTraces(double xmin, double xmax) {

        double Ymax = Double.NEGATIVE_INFINITY;
        double Ymin = Double.POSITIVE_INFINITY;
        for (Line line : lineSegmentMap.keySet()) {
            Point2D limits = line.getYMinMax(xmin, xmax);
            Ymin = Math.min(Ymin, limits.getX());
            Ymax = Math.max(Ymax, limits.getY());
        }
        return new PairT<>(Ymin, Ymax);
    }

}
