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
