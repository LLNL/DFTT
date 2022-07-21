/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.fkPlot;

import java.awt.Color;
import llnl.gnem.core.gui.plotting.PaintMode;
import llnl.gnem.core.gui.plotting.PenStyle;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.Pcolor;
import llnl.gnem.core.signalprocessing.arrayProcessing.FKResult;
import llnl.gnem.core.signalprocessing.arrayProcessing.SlownessContourValues;
import llnl.gnem.core.signalprocessing.arrayProcessing.SlownessValue;

/**
 *
 * @author dodge1
 */
public class FKPlot extends JMultiAxisPlot {

    private JSubplot sp1;

    public FKPlot() {
        getXaxis().setLabelText("East Slowness (s/km)");
        sp1 = addSubplot();
        sp1.getPlotRegion().setDrawBox(true);
    }

    public void plotFK(FKResult result) {
        sp1.Clear();

        sp1.setXmin(result.getSeastMin());
        sp1.setXmax(result.getSeastMax());
        sp1.getYaxis().setMin(result.getSnorthMin());
        sp1.getYaxis().setMax(result.getSnorthMax());

        Pcolor signalFkPcolor = new Pcolor(result.getFksDouble(), result.getXeastDouble(), result.getXnorthDouble());
        signalFkPcolor.setVisible(true);
        sp1.AddPlotObject(signalFkPcolor);
        addAxisLines(result);
        addSlownessCircles();

        SlownessContourValues values = result.getContourAroundPeak(2.0); // 2 dB down
        SlownessContour slow = new SlownessContour(values);
        sp1.AddPlotObject(slow);
        
        SlownessValue smax = result.getPeakValue();
        addMaxPowerCrosshairs(smax);
        sp1.getYaxis().setLabelText("North Slowness (s/km)");
        String title = createTitle(result);
        this.getTitle().setText(title);
        repaint();
    }


    public void addMaxPowerCrosshairs(SlownessValue smax) {
        Line lh = addHorizontalLine(smax.getsEast() - .1, smax.getsEast() + .1, smax.getsNorth(), 500, Color.green, PenStyle.SOLID);
        lh.setWidth(2);
        sp1.AddPlotObject(lh);
        Line lv = addVerticalLine(smax.getsEast(), smax.getsNorth() - .1, smax.getsNorth() + .1, 500, Color.green, PenStyle.SOLID);
        lv.setWidth(2);
        sp1.AddPlotObject(lv);
    }
    
    public void addAxisLines(FKResult results) {
        Line lh0 = addHorizontalLine(results.getSeastMin(), results.getSeastMax(), 0.0, 500, Color.LIGHT_GRAY, PenStyle.DASH);
        sp1.AddPlotObject(lh0);
        Line lv0 = addVerticalLine(0.0, results.getSnorthMin(), results.getSnorthMax(), 500, Color.LIGHT_GRAY, PenStyle.DASH);
        sp1.AddPlotObject(lv0);
    }

    public void addSlownessCircles() {
        SlownessCircle sc = new SlownessCircle(0, 0, 1.0 / 6.0, "6 km/s");
        sp1.AddPlotObject(sc);
        sc = new SlownessCircle(0, 0, 1.0 / 3.0, "3 km/s");
        sp1.AddPlotObject(sc);
        sc = new SlownessCircle(0, 0, 1.0 / 8.0, "8 km/s");
        sp1.AddPlotObject(sc);
        sc = new SlownessCircle(0, 0, 1.0 / 12.0, "12 km/s");
        sp1.AddPlotObject(sc);
    }

    Line addHorizontalLine(double xmin, double xmax, double yval, int npts, Color c, PenStyle style) {
        float dx = (float) (xmax - xmin) / (npts - 1);
        float[] x = new float[npts];
        float[] y = new float[npts];
        for (int j = 0; j < npts; ++j) {
            x[j] = (float) xmin + dx * j;
            y[j] = (float) yval;
        }
        return new Line(x, y, c, PaintMode.COPY, style, 1);
    }

    Line addVerticalLine(double xval, double ymin, double ymax, int npts, Color c, PenStyle style) {
        float dy = (float) (ymax - ymin) / (npts - 1);
        float[] x = new float[npts];
        float[] y = new float[npts];
        for (int j = 0; j < npts; ++j) {
            x[j] = (float) xval;
            y[j] = (float) ymin + dy * j;
        }
        return new Line(x, y, c, PaintMode.COPY, style, 1);
    }

    private String createTitle(FKResult result) {
        return String.format("North slowness = %6.3f s/km, East slowness = %6.3f s/km, Azimuth = %5.1f, Velocity = %5.1f km/s, Quality = %5.3f", 
                result.getPeakValue().getsNorth(),
                result.getPeakValue().getsEast(),
                result.getPeakValue().getAzimuth(),
                result.getPeakValue().getVelocity(),
                result.getQuality());
    }

}
