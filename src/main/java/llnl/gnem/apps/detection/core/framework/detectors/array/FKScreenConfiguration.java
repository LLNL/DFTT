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


import llnl.gnem.apps.detection.dataAccess.dataobjects.DetectorType;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenParams;
import llnl.gnem.apps.detection.core.dataObjects.FKScreenRange;
import llnl.gnem.apps.detection.core.dataObjects.SlownessRangeSpecification;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.waveform.seismogram.BasicSeismogram;

/**
 *
 * @author dodge1
 */
public class FKScreenConfiguration implements Serializable {

    private static final long serialVersionUID = -1209009891713213762L;

    private final HashMap<StreamKey, ArrayElementInfo> ourElements;
    private transient ArrayList<float[]> waveforms;
    private transient float[] dNorth;
    private transient float[] dEast;
    private transient double delta;
    private transient ArrayList<StreamKey> orderedKeys; // Stream keys ordered by buildArrays
    private FKScreenParams screenParams;
    private SlownessRangeSpecification srs;

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(srs);
        s.writeObject(screenParams);
        s.writeInt(ourElements.size());
        for (StreamKey sck : ourElements.keySet()) {
            s.writeObject(sck);
            s.writeObject(ourElements.get(sck));
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        srs = (SlownessRangeSpecification) s.readObject();
        screenParams = (FKScreenParams) s.readObject();
        int numElements = s.readInt();
        for (int j = 0; j < numElements; ++j) {
            StreamKey sck = (StreamKey) s.readObject();
            ArrayElementInfo ae = (ArrayElementInfo) s.readObject();
            ourElements.put(sck, ae);
        }
    }

    public static FKScreenConfiguration buildCompositeConfiguration(Collection<FKScreenConfiguration> fkConfigs, double decimatedRate) {
        if (fkConfigs.isEmpty()) {
            return null;
        } else if (fkConfigs.size() == 1) {
            return fkConfigs.iterator().next();
        } else {
            double delAz = 0;
            double delVel = 0;
            double minAz = 360;
            double maxAz = 0;
            double minSlow = Double.MAX_VALUE;
            double maxSlow = 0;
            double avgAz = 0;
            double avgSlow = 0;
            int num = 0;
            FKScreenParams commonScreenParams = null;
            HashMap<StreamKey, ArrayElementInfo> elements = null;
            for (FKScreenConfiguration fkConfig : fkConfigs) {
                commonScreenParams = fkConfig.screenParams;
                elements = fkConfig.ourElements;
                delAz = fkConfig.screenParams.getFKScreenRange().getDelAzimuth();
                delVel = fkConfig.screenParams.getFKScreenRange().getDelVelocity();
                double az = fkConfig.srs.getNominal().getBackAzimuth();
                if (az < minAz) {
                    minAz = az;
                }
                if (az > maxAz) {
                    maxAz = az;
                }
                avgAz += az;
                double slow = 1.0 / fkConfig.srs.getNominal().getVelocity();
                if (slow < minSlow) {
                    minSlow = slow;
                }
                if (slow > maxSlow) {
                    maxSlow = slow;
                }
                avgSlow += slow;
                ++num;
            }
            avgAz /= num;
            avgSlow /= num;
            double avgVel = 1 / avgSlow;
            delAz += (maxAz - minAz) / 2;
            double vmax = 1 / minSlow + delVel;
            double vmin = 1 / maxSlow - delVel;
            double velRange = vmax - vmin;
            FKScreenRange newRange = new FKScreenRange(delAz, velRange / 2);
            SlownessSpecification slownessSpecification = new SlownessSpecification(avgVel, avgAz);
            SlownessRangeSpecification srs = new SlownessRangeSpecification(slownessSpecification, newRange);

            FKScreenParams params = new FKScreenParams(newRange, commonScreenParams.getMaxSlowness(),
                    commonScreenParams.getMinFKFreq(),
                    Math.min(commonScreenParams.getMaxFKFreq(), decimatedRate / 2),
                    commonScreenParams.getfKWindowLength(),
                    commonScreenParams.getMinFKQual(),
                    commonScreenParams.getMinVelocity(),
                    commonScreenParams.getMaxVelocity(),
                    commonScreenParams.isComputeFKParams(),
                    commonScreenParams.isScreenPowerTriggers(),
                    commonScreenParams.isRequireMinimumVelocity(),
                    commonScreenParams.isRequireMaximumVelocity());
            return new FKScreenConfiguration(params, srs, elements);
        }
    }

    public FKScreenConfiguration(FKScreenParams screenParams,
            SlownessRangeSpecification srs,
            Map<StreamKey, ArrayElementInfo> ourElements) {
        this.screenParams = screenParams;
        this.srs = srs;
        this.ourElements = new HashMap<>(ourElements);
        waveforms = null;
        dNorth = null;
        dEast = null;
    }

    public void buildArrays(Collection<? extends BasicSeismogram> traces, TimeT triggerTime) {
        orderedKeys = new ArrayList<>();
        waveforms = new ArrayList<>();
        dNorth = new float[traces.size()];
        dEast = new float[traces.size()];
        int j = 0;
        for (BasicSeismogram seis : traces) {
            StreamKey key = seis.getStreamKey();
            ArrayElementInfo element = ourElements.get(key);
            if (element == null) {
                throw new IllegalStateException("Failed to retrieve ArrayElement for key: " + key);
            }
            orderedKeys.add(key);
            dEast[j] = (float) element.getDeast();
            dNorth[j++] = (float) element.getDnorth();
            waveforms.add(buildSingleWaveform(seis, triggerTime));
            delta = seis.getDelta();
        }
    }

    public boolean isComputeFKOnTriggers() {
        return screenParams.isComputeFKOnTriggers();
    }

    public boolean isScreenTrigger(DetectorType type) {
        return screenParams.isScreenTrigger(type);
    }

    /**
     * @return the slowTol
     */
    public double getSlowTol() {
        return srs.getDelSlow();
    }

    /**
     * @return the minFKQual
     */
    public double getMinFKQual() {
        return screenParams.getMinFKQual();
    }

    /**
     * @return the sMax
     */
    public double getsMax() {
        return screenParams.getMaxSlowness();
    }

    /**
     * @return the slownessVector
     */
    public float[] getSlownessVector() {
        return srs.getNominal().getSlownessVector();
    }

    public double getBackAzimuth() {
        return srs.getNominal().getBackAzimuth();
    }

    public double getVelocity() {
        return srs.getNominal().getVelocity();
    }

    private float[] buildSingleWaveform(BasicSeismogram seis, TimeT triggerTime) {
        BasicSeismogram tmp = new BasicSeismogram(seis);
        TimeT end = new TimeT(triggerTime.getEpochTime() + screenParams.getfKWindowLength());
        tmp.cut(triggerTime, end);
        return tmp.getData();
    }

    /**
     * @return the waveforms
     */
    public ArrayList<float[]> getWaveforms() {
        return waveforms;
    }

    public float[][] getWaveformsAsFloatArray() {
        float[][] result = new float[waveforms.size()][];
        for (int j = 0; j < waveforms.size(); ++j) {
            result[j] = waveforms.get(j).clone();
        }
        return result;
    }
    
    public float[][] getWaveformsFromStreamSegment(StreamSegment stream){
        float[][] result = new float[orderedKeys.size()][];
        int j = 0;
        for( StreamKey key : orderedKeys){
            float[] channel = stream.getChannelData(key);
            result[j++] = channel.clone();
        }
        return result;
    }

    /**
     * @return the dNorth
     */
    public float[] getdNorth() {
        return dNorth;
    }

    /**
     * @return the dEast
     */
    public float[] getdEast() {
        return dEast;
    }
    
    public double[][] getChannelCoordinates()
    {
        double[][] result = new double[dNorth.length][2];
        for(int j = 0; j < dNorth.length; ++j){
            result[j][0] = dNorth[j];
            result[j][1] = dEast[j];
        }
        return result;
    }

    public double getDnorth(StreamKey key) {
        ArrayElementInfo element = ourElements.get(key);
        if (element == null) {
            throw new IllegalStateException("Failed to retrieve ArrayElement for key: " + key);
        }
        return element.getDnorth();
    }

    public double getDeast(StreamKey key) {
        ArrayElementInfo element = ourElements.get(key);
        if (element == null) {
            throw new IllegalStateException("Failed to retrieve ArrayElement for key: " + key);
        }
        return element.getDeast();
    }

    /**
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    public boolean isRequireMinimumVelocity() {
        return screenParams.isRequireMinimumVelocity();
    }

    public boolean isRequireMaximumVelocity() {
        return screenParams.isRequireMaximumVelocity();
    }

    private float[] buildFKLimitsArray() {
        float[] result = new float[2];
        double nyQuist = 1.0 / (2 * delta);
        double maxFreq = Math.min(screenParams.getMaxFKFreq(), .9 * nyQuist);
        double minFreq = screenParams.getMinFKFreq();
        if (maxFreq < minFreq) {
            minFreq = maxFreq / 10;
        }
        result[0] = (float) minFreq;
        result[1] = (float) maxFreq;
        return result;
    }

    public float[] getFreqLimits() {
        return buildFKLimitsArray();
    }

    public double getMinAllowableVelocity() {
        return screenParams.getMinVelocity();
    }

    public double getMaxAllowableVelocity() {
        return screenParams.getMaxVelocity();
    }
}
