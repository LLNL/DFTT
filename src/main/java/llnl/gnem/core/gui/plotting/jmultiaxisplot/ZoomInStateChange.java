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
package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import java.awt.Rectangle;
import llnl.gnem.core.gui.plotting.ZoomLimits;

/**
 * Created by: dodge1 Date: Dec 10, 2004 COPYRIGHT NOTICE GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class ZoomInStateChange {

    /**
     * @return the realWorldYMin
     */
    public double getRealWorldYMin() {
        return realWorldYMin;
    }

    /**
     * @return the realWorldYMax
     */
    public double getRealWorldYMax() {
        return realWorldYMax;
    }

    private final Rectangle rect;
    private final double realWorldXMin;
    private final double realWorldXMax;
    private final JMultiAxisPlot initiator;
    private final double realWorldYMin;
    private final double realWorldYMax;

    public ZoomInStateChange(Rectangle rect, double xMin, double xMax, double yMin, double yMax,JMultiAxisPlot initiator) {
        this.rect = rect;
        realWorldXMin = xMin;
        realWorldXMax = xMax;
        realWorldYMin = yMin;
        realWorldYMax = yMax;
        this.initiator = initiator;
    }

    public Rectangle getZoomBounds() {
        return rect;
    }

    /**
     * @return the realWorldXMin
     */
    public double getRealWorldXMin() {
        return realWorldXMin;
    }

    /**
     * @return the realWorldXMax
     */
    public double getRealWorldXMax() {
        return realWorldXMax;
    }

    /**
     * @return the initiator
     */
    public JMultiAxisPlot getInitiator() {
        return initiator;
    }

    public ZoomLimits getZoomLimits() {
        return new ZoomLimits(realWorldXMin, realWorldXMax,realWorldYMin, realWorldYMax);
    }
}
