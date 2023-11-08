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
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import java.awt.Color;
import java.awt.geom.Point2D;
import llnl.gnem.apps.detection.statistics.HistogramData;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.dftt.core.gui.plotting.plotobject.JPolygon;

/**
 *
 * @author dodge1
 */
public class HistogramProjectionView  extends JMultiAxisPlot implements ProjectionView {

    public HistogramProjectionView() {
        this.getSubplotManager().setplotSpacing(0);
    }

    @Override
    public void updateForNewProjection() {
        ProjectionCollection pc = ProjectionModel.getInstance().getProjectionCollection();
        this.clear();
        HistogramData result = pc.getHistogram();
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
        getXaxis().setLabelText("Linear Correlation");
        plot.getYaxis().setLabelText("Count");
        getTitle().setText(String.format("Projection of other templates on template for detector %d", pc.getDetectorid()));
        repaint();
    }

    private JPolygon getBar(float minValue, float maxValue, float height) {
        Point2D[] vertex = new Point2D.Float[4];
        vertex[0] = new Point2D.Float(minValue, 0.0f);
        vertex[1] = new Point2D.Float(minValue, height);
        vertex[2] = new Point2D.Float(maxValue, height);
        vertex[3] = new Point2D.Float(maxValue, 0.0f);
        return new JPolygon(vertex);
    }

}
