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
package llnl.gnem.apps.detection.sdBuilder.histogramDisplay;

import llnl.gnem.apps.detection.statistics.HistogramData;
import java.awt.Color;
import java.awt.geom.Point2D;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.VPickLine;
import llnl.gnem.core.gui.plotting.plotobject.JPolygon;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import org.apache.commons.math3.distribution.BetaDistribution;

/**
 *
 * @author dodge1
 */
public class HistogramView extends JMultiAxisPlot {

    private static final long serialVersionUID = -7881094011765753541L;

    public HistogramView() {
        this.getSubplotManager().setplotSpacing(0);
    }

    void templateWasUpdated() {
        this.clear();

    }

    void displayHistogram() {
        this.clear();
        HistogramData result = HistogramModel.getInstance().getHistogram();
        JSubplot plot = this.addSubplot();
        float[] bins = result.getBins();
        float[] values = result.getValues();
        float binHalfWidth = bins[0];
        for (int j = 0; j < bins.length; ++j) {
            float min = bins[j] - binHalfWidth;
            float max = bins[j] + binHalfWidth;
            float val = values[j];
            if (val > 0) {
                JPolygon poly = getBar(min, max, val);
                poly.setFillColor(Color.blue);
                plot.AddPlotObject(poly);
            }
        }
        plot.setAxisLimits(bins, values);
        Line line = getBetaDistributionLine();
        if (line != null) {
            plot.AddPlotObject(line);
        }
        double threshold = HistogramModel.getInstance().getThreshold();
        VPickLine vpl = new VPickLine(threshold, 0.8, "Threshold");
        vpl.setAllDraggable(false);
        plot.AddPlotObject(vpl);
        getXaxis().setLabelText("Linear Correlation");
        plot.getYaxis().setLabelText("Count");
        getTitle().setText(String.format("Detection statistics for detector %d, runid %d", 
                HistogramModel.getInstance().getDetectorid(),
                HistogramModel.getInstance().getRunid()));
        repaint();
    }

    private Line getBetaDistributionLine() {
        BetaDistribution dist = HistogramModel.getInstance().getHistogramFitBetaDistribution();
        if (dist == null) {
            return null;
        }
        float[] x = new float[5000];
        float[] v = new float[5000];
        for (int j = 1; j < 5000; ++j) {
            x[j] = j / 5000.0f;
            double value = dist.density(x[j]);
            v[j] = (float) value;
        }
        Line line = new Line(x, v);
        line.setColor(Color.RED);
        line.setWidth(3);
        return line;
    }

    JPolygon getBar(float minValue, float maxValue, float height) {
        Point2D[] vertex = new Point2D.Float[4];
        vertex[0] = new Point2D.Float(minValue, 0.0f);
        vertex[1] = new Point2D.Float(minValue, height);
        vertex[2] = new Point2D.Float(maxValue, height);
        vertex[3] = new Point2D.Float(maxValue, 0.0f);
        return new JPolygon(vertex);
    }

}
