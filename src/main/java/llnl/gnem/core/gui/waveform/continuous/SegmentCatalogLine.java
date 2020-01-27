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
package llnl.gnem.core.gui.waveform.continuous;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;
import llnl.gnem.core.gui.plotting.plotobject.XPinnedText;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.waveform.continuous.segments.ChannelSegmentCatalog;
import llnl.gnem.core.waveform.continuous.segments.ContiguousSegmentCollection;


public class SegmentCatalogLine extends PlotObject {

    private final Map<Line, ContiguousSegmentCollection> lineSegmentMap;

    private final XPinnedText text;

    public SegmentCatalogLine(ChannelSegmentCatalog catalog, float yPos) {
        lineSegmentMap = new HashMap<>();
        ArrayList<ContiguousSegmentCollection> segments = catalog.getContiguousSegments();
        for (ContiguousSegmentCollection csc : segments) {
            double start = csc.getStart();
            double end = csc.getEnd();
            float[] data = {yPos, yPos};

            double dt = end - start;
            if (dt > 0) {
                Line line = new Line(start, dt, data);
                line.setWidth(2);
                lineSegmentMap.put(line, csc);
            }

        }
        StreamKey key = catalog.getName();

        text = new XPinnedText(2.0, yPos, String.format("%s", key.getPlotLabel()));
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

}
