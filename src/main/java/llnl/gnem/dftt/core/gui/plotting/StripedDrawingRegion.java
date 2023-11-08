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
package llnl.gnem.dftt.core.gui.plotting;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.YAxis;
import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;

/**
 *
 * @author addair1
 */
public class StripedDrawingRegion extends DrawingRegion {

    private final YAxis axis;

    public StripedDrawingRegion(YAxis axis) {
        super(false);
        this.axis = axis;
        //stripeColor = new Color(0xf8f8ff); // ghost white
    }

    @Override
    public void render(Graphics g) {
        super.render(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(getOffsetColor(7));

        CoordinateTransform ct = axis.getCoordinateTransform();

        Rectangle box = getRect();
        TickMetrics ticks = axis.getTickMetrics(box.height);
        boolean skip = true;
        int last = 0;
        boolean first = true;
        while (ticks.hasNext()) {
            double value = ticks.getNext();
            Coordinate c = new Coordinate(0.0, 0.0, 0.0, value);
            ct.WorldToPlot(c);
            int y = (int) c.getY();

            if (first) {
                first = false;
            } else {
                if (skip) {
                    last = y;
                } else {
                    g2d.fillRect(box.x, y, box.width, last - y);
                }
                skip = !skip;
            }
        }

        drawBox(g);
    }
}
