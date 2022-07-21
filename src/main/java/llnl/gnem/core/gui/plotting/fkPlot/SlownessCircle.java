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
import java.awt.Graphics;
import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.plotobject.DataText;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

public class SlownessCircle extends PlotObject {

    private final int npts = 100;
    private final LineData lineData;
    private Color color = Color.green;

    public SlownessCircle(double sx0, double sy0, double slownessRadius, String label) {
        lineData = makeLine(sx0, sy0, slownessRadius, label);

    }

    private LineData makeLine(double sx0, double sy0, double slownessRadius, String label) {
        double dTheta = Math.PI * 2 / (npts - 1);
        float[] x = new float[npts];
        float[] y = new float[npts];
        for (int j = 0; j < npts; ++j) {
            double theta = j * dTheta;
            double dx = Math.cos(theta) * slownessRadius;
            double dy = Math.sin(theta) * slownessRadius;
            x[j] = (float) (sx0 + dx);
            y[j] = (float) (sy0 + dy);
        }
        Line line1 = new Line(x, y);
        line1.setColor(getColor());
        DataText text = new DataText(sx0, sy0 - slownessRadius - .005, label);
        text.setColor(getColor());
        text.setFontSize(14);
        return new LineData(line1, text);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        lineData.line.setColor(getColor());
        lineData.text.setColor(getColor());
    }

    class LineData {

        Line line;
        DataText text;

        public LineData(Line line, DataText text) {
            this.line = line;
            this.text = text;
        }
    }

    @Override
    public void render(Graphics g, JBasicPlot owner) {
        if (!visible) {
            return;
        }
        lineData.line.render(g, owner);
        lineData.text.render(g, owner);
    }

    @Override
    public void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy) {

    }

}
