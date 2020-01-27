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
package llnl.gnem.core.gui.plotting.contour;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import llnl.gnem.core.gui.plotting.HorizPinEdge;
import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.Legend;
import llnl.gnem.core.gui.plotting.VertPinEdge;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

public class ContourLegend extends PlotObject {
    private Legend legend;

    public ContourLegend(String fontName, double fontSize, Collection<ContourLine> contours) {
        List<Line> lines = new ArrayList<Line>();
        List<String> labels = new ArrayList<String>();
        Map<String, Integer> defined = new HashMap<String, Integer>();

        for (ContourLine contourLine : contours) {
            String label = contourLine.getLabel();
            if (defined.containsKey(label)) continue;

            defined.put(label, 1);
            labels.add(label);
            Color color = contourLine.getColor();
            Line line = new Line();
            line.setColor(color);
            lines.add(line);
        }

        legend = new Legend(labels, lines, fontName, fontSize, HorizPinEdge.RIGHT, VertPinEdge.TOP, 5, 5);
    }

    @Override
    public void render(Graphics g, JBasicPlot owner) {
        legend.render(g, owner);
    }

    @Override
    public void ChangePosition(JBasicPlot owner, Graphics graphics, double dx, double dy) {
        legend.ChangePosition(owner, graphics, dx, dy);
    }
}
