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
