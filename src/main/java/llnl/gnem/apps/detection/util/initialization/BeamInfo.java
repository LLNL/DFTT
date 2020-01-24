package llnl.gnem.apps.detection.util.initialization;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;



/**
 * Created by dodge1
 * Date: Feb 12, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class BeamInfo {
    public static final int DEGREES_PER_RADIAN = 180;

    private double northSlowness;
    private double eastSlowness;
    private double downSlowness;

    public BeamInfo(double northSlowness,
            double eastSlowness,
            double downSlowness) {
        this.northSlowness = northSlowness;
        this.eastSlowness = eastSlowness;
        this.downSlowness = downSlowness;
    }

    public BeamInfo(BeamParams bp) {
        double baz = bp.getBaz();
        double velocity = bp.getVelocity();
        double theta = baz * Math.PI / DEGREES_PER_RADIAN;
        eastSlowness = (1.0 / velocity) * Math.sin(theta);
        northSlowness = (1.0 / velocity) * Math.cos(theta);
        downSlowness = 0.0;
    }

    public double getNorthSlowness() {
        return northSlowness;
    }

    public double getEastSlowness() {
        return eastSlowness;
    }

    public double getDownSlowness() {
        return downSlowness;
    }

    public void setNorthSlowness(double northSlowness) {
        this.northSlowness = northSlowness;
    }

    public void setEastSlowness(double eastSlowness) {
        this.eastSlowness = eastSlowness;
    }

    public void setDownSlowness(double downSlowness) {
        this.downSlowness = downSlowness;
    }

    public Vector3D getSlownessVector() {
        return new Vector3D(northSlowness, eastSlowness, downSlowness);
    }
}