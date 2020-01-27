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
