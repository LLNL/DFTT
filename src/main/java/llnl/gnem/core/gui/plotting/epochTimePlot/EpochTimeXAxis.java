/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.epochTimePlot;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import llnl.gnem.core.gui.plotting.HorizAlignment;
import llnl.gnem.core.gui.plotting.TickLabel;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.XAxis;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge
 */
public class EpochTimeXAxis extends XAxis {

    public EpochTimeXAxis(JMultiAxisPlot plot) {
        super(plot);
    }

    @Override
    protected void renderLinearTicks(Graphics g, double minIn, double maxIn) {
        double min = minIn;
        double max = maxIn;
        PlottingEpoch pe = new PlottingEpoch(minIn, maxIn);
        ArrayList<TickValue> majorTicks = pe.getTicks(getNumMinorTicks());
        TickTimeType type = TickTimeType.SECONDS;
        for (TickValue tmv : majorTicks) {
            type = tmv.getType();
            double val = min + tmv.getOffset();
            if (val >= minIn && val <= maxIn) {
                if(fullyDecorateAxis || tmv.isMajor())
                renderTick(g, min + tmv.getOffset(), tmv.getLabel(), tmv.isMajor(), HorizAlignment.CENTER);
            }
        }
        String refString = String.format("%s (%s)", pe.getTime().toString(), type.toString());
        renderReferenceTickLabel(g, refString, pe.getTime().getEpochTime());
    }

    private void renderReferenceTickLabel(Graphics g, String refString, double time) {
        TickLabel refLabel = new TickLabel("", refString);
        TickValue tmv = new TickValue(0.0, refLabel, true, HorizAlignment.LEFT, TickTimeType.SECONDS);
        renderTick(g, time + tmv.getOffset(), tmv.getLabel(), tmv.isMajor(), tmv.getHorizAlignment());
     }

}
