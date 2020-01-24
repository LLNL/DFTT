/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
