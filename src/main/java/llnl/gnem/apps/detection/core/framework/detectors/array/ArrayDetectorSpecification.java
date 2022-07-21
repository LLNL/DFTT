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
package llnl.gnem.apps.detection.core.framework.detectors.array;

import llnl.gnem.apps.detection.core.dataObjects.AbstractSpecification;

import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import llnl.gnem.apps.detection.core.dataObjects.TriggerPositionType;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;

import java.util.ArrayList;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.apps.detection.util.ArrayInfoModel;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.util.StreamKey;

public class ArrayDetectorSpecification extends AbstractSpecification implements Serializable {

    private static final long serialVersionUID = -7768540165481026013L;

    private final float STADuration;
    private final float LTADuration;
    private final float gapDuration;
    private final boolean spawnOnTriggers;
    private final SlownessSpecification slownessSpecification;
    private final ArrayConfiguration geometry;

    public static ArrayDetectorSpecification create(float threshold,
            float blackoutPeriod,
            ArrayList< StreamKey> staChanList,
            float STADuration,
            float LTADuration,
            float gapDuration,
            boolean enableSpawning,
            float backAzimuth,
            float velocity,
            String arrayName,
            int jdate) throws DataAccessException {
        ArrayConfiguration config = ArrayInfoModel.getInstance().getGeometry(arrayName);
           
        return new ArrayDetectorSpecification(threshold,
                blackoutPeriod,
                staChanList,
                STADuration,
                LTADuration,
                gapDuration,
                enableSpawning,
                backAzimuth,
                velocity,
                config);
    }

    public static ArrayDetectorSpecification create(String specFile, int jdate) throws  DataAccessException, IOException {
        try (FileInputStream stream = new FileInputStream(specFile)) {
            return new ArrayDetectorSpecification(stream, jdate);
        }

    }

    public ArrayDetectorSpecification(InputStream stream,
            int jdate) throws   DataAccessException, IOException {

        super(stream);

        STADuration = Float.parseFloat(parameterList.getProperty("STADuration", "4.0"));
        LTADuration = Float.parseFloat(parameterList.getProperty("LTADuration", "40.0"));
        gapDuration = Float.parseFloat(parameterList.getProperty("gapDuration", "2.0"));

        spawnOnTriggers = Boolean.parseBoolean(parameterList.getProperty("enableSpawning", "true"));

        float baz = Float.parseFloat(parameterList.getProperty("backAzimuth", "0.0"));
        float vel = Float.parseFloat(parameterList.getProperty("velocity", "99999.0"));

        slownessSpecification = new SlownessSpecification(vel, baz);

        triggerPositionType = TriggerPositionType.THRESHOLD_EXCEED_POINT;
        detectorType = DetectorType.ARRAYPOWER;

        geometry = ArrayInfoModel.getInstance().getGeometry(staChanList);
 
        verifyChannelConsistency();
    }

    /*
     * Constructor to support instantiation of detectors stored in the database.
     */
    private ArrayDetectorSpecification(float threshold,
            float blackoutPeriod,
            ArrayList< StreamKey> staChanList,
            float STADuration,
            float LTADuration,
            float gapDuration,
            boolean enableSpawning,
            float backAzimuth,
            float velocity,
            ArrayConfiguration config)  {

        super(threshold, blackoutPeriod, staChanList);
        geometry = config;
        this.STADuration = STADuration;
        this.LTADuration = LTADuration;
        this.gapDuration = gapDuration;

        spawnOnTriggers = enableSpawning;

        slownessSpecification = new SlownessSpecification(velocity, backAzimuth);

        triggerPositionType = TriggerPositionType.THRESHOLD_EXCEED_POINT;
        detectorType = DetectorType.ARRAYPOWER;
    }

    public float getSTADuration() {
        return STADuration;
    }

    public float getLTADuration() {
        return LTADuration;
    }

    public float getGapDuration() {
        return gapDuration;
    }

    @Override
    public boolean spawningEnabled() {
        return spawnOnTriggers;
    }

    public float[] getSlownessVector() {
        return slownessSpecification.getSlownessVector();
    }

    public ArrayConfiguration getArrayConfiguration() {
        return geometry;
    }

    public static void printSpecificationTemplate(PrintStream ps) {

        AbstractSpecification.printSpecificationTemplate(ps);

        ps.println("STADuration    =  <duration (sec)>");
        ps.println("LTADuration    =  <duration (sec)>");
        ps.println("gapDuration    =  <duration (sec)>");
        ps.println("enableSpawning =  false");
        ps.println("backAzimuth    =  <backazimuth (degrees east of north)>");
        ps.println("velocity       =  <velocity (km/sec)>");
        ps.println("arrayDefinitions = <CSS_SiteFile> <arrayName> <jdate>");
        ps.println("detectorType     = ArrayPower");
    }

    @Override
    public void printSpecification(PrintStream ps) {

        super.printSpecification(ps);
        ps.println();

        ps.println("STADuration     = " + STADuration);
        ps.println("LTADuration     = " + LTADuration);
        ps.println("gapDuration     = " + gapDuration);
        ps.println("spawning enabled: " + spawnOnTriggers);
        ps.println("slowness vector:  " + slownessSpecification);
        ps.println("array geometry = " + geometry.getArrayName());
    }



    private void verifyChannelConsistency() {
        for (StreamKey sck : this.getStreamKeys()) {
            if (geometry != null && !geometry.hasElement(sck.getSta())) {
                throw new IllegalStateException("Element: " + sck + " not found in geometry!");
            }
        }
    }

    public double getBackAzimuth() {
        return slownessSpecification.getBackAzimuth();
    }

    public double getVelocity() {
        return slownessSpecification.getVelocity();
    }

    SlownessSpecification getSlownessSpecification() {
        return slownessSpecification;
    }

    @Override
    public boolean isArraySpecification() {
        return true;
    }

}
