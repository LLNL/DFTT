/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import java.awt.Color;
import java.awt.geom.Point2D;
import llnl.gnem.apps.detection.statistics.HistogramData;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.plotobject.JPolygon;

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
