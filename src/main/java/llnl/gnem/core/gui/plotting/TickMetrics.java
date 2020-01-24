package llnl.gnem.core.gui.plotting;

import java.util.NoSuchElementException;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
/**
 * Class that contains parameters used to layout the ticks when rendering an
 * axis
 *
 * @author Doug Dodge
 */
public abstract class TickMetrics {

    private static final double MINIMUM_INCREMENT = 1.0e-12;
    /**
     * Minimum value of the axis
     */
    private final Double min;
    /**
     * Maximum value of the axis
     */
    private final double max;
    private final boolean fullyDecorate;
    private Double val;

    /**
     * Constructor for the TickMetrics object
     */
    public TickMetrics() {
        this(0.0, 1.0, false);
    }

    /**
     * Constructor for the TickMetrics object
     *
     * @param min Minimum value of the axis
     * @param max Maximum value of the axis
     * @param fullyDecorate
     */
    public TickMetrics(double min, double max, boolean fullyDecorate) {
        this.min = min;
        if (min == max) {
            this.max = max + MINIMUM_INCREMENT;
        } else {
            this.max = max;
        }
        this.fullyDecorate = fullyDecorate;
        val = min;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean fullyDecorate() {
        return fullyDecorate;
    }

    protected double getValue() {
        return val;
    }

    protected void setValue(double val) {
        this.val = val;
    }

    public boolean hasNext() {
        return val >= Math.min(min, max) && val <= Math.max(min, max);
    }

    /**
     * @return The next element.
     * @throws {@link NoSuchElementException} if there is no next element.
     */
    public double getNext() {
        if (hasNext()) {
            double next = scale(val);

            if (fullyDecorate()) {
                if (getIncrement() > MINIMUM_INCREMENT) {
                    val += getIncrement();
                } else if (val.equals(min)) {
                    val = max;
                } else {
                    val = max + MINIMUM_INCREMENT;
                }
            } else {
                val += max - min;
            }

            return next;
        } else {
            throw new NoSuchElementException();
        }
    }

    public abstract double scale(double v);

    public abstract double getIncrement();

    public static class LinearTickMetrics extends TickMetrics {

        /**
         * Tick increment
         */
        private final double inc;

        /**
         * Constructor for the TickMetrics object
         */
        public LinearTickMetrics() {
            this(0.0, 1.0, 0.1, false);
        }

        /**
         * Constructor for the TickMetrics object
         *
         * @param min Minimum value of the axis
         * @param max Maximum value of the axis
         * @param inc tick increment
         */
        public LinearTickMetrics(double min, double max, double inc, boolean fullyDecorate) {
            super(min, max, fullyDecorate);
            this.inc = inc;
        }

        @Override
        public double scale(double v) {
            return v;
        }

        @Override
        public double getIncrement() {
            return inc;
        }
    }

    public static class LogTickMetrics extends TickMetrics {

        public LogTickMetrics(double min, double max, boolean fullyDecorate) {
            super(min, max, fullyDecorate);
        }

        @Override
        public double scale(double v) {
            return Math.pow(10.0, v);
        }

        @Override
        public double getIncrement() {
            return 1;
        }
    }
}
