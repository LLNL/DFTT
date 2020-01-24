package llnl.gnem.core.gui.plotting.beachball;

import llnl.gnem.core.util.PairT;

/**
 * Created by dodge1
 * Date: Mar 5, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class Boundaries {
    private final double[] x1;
    private final double[] y1;
    private final double[] x2;
    private final double[] y2;
    private final double[] xPaxis;
    private final double[] yPaxis;
    private final PairT<double[], double[]> boundingCircle;

    Boundaries(double[] x1, double[] y1, double[] x2, double[] y2, double[] xPaxis, double[] yPaxis, PairT<double[], double[]> boundingCircle) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.xPaxis = xPaxis;
        this.yPaxis = yPaxis;
        this.boundingCircle = boundingCircle;
    }

    public double[] getX1() {
        return x1;
    }

    public double[] getY1() {
        return y1;
    }

    public double[] getX2() {
        return x2;
    }

    public double[] getY2() {
        return y2;
    }

    public PairT<double[], double[]> getBoundingCircle() {
        return boundingCircle;
    }

    public double[] getXPaxis() {
        return xPaxis;
    }

    public double[] getYPaxis() {
        return yPaxis;
    }
}
