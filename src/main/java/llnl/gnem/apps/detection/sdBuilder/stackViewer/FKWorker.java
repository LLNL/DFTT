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
package llnl.gnem.apps.detection.sdBuilder.stackViewer;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import llnl.gnem.apps.detection.core.signalProcessing.Beamformer;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.fkDisplay.SingleEventFKFrame;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.fkDisplay.SingleEventFKModel;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.stackViewer.StackModel.StackData;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.util.DebugHelpers;
import llnl.gnem.core.gui.plotting.MouseMode;
import llnl.gnem.core.signalprocessing.arrayProcessing.FKProducer;
import llnl.gnem.core.signalprocessing.arrayProcessing.FKResult;
import llnl.gnem.core.signalprocessing.arrayProcessing.SlownessValue;
import llnl.gnem.core.util.ApplicationLogger;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;

/**
 *
 * @author dodge1
 */
public class FKWorker extends SwingWorker<Void, Void> {

    private FKResult result = null;

    public FKWorker() {
    }

    @Override
    protected Void doInBackground() throws Exception {
        double fkWindowLength = FKWindowParams.getInstance().getFkWindowLength();
        double prePickSeconds = ParameterModel.getInstance().getPrepickSeconds();
        double windowStart = FKWindowParams.getInstance().getWindowStart();
        Collection<StreamKey> channels = StackModel.getInstance().getElementKeys();
        ArrayList<Double> xNorth = new ArrayList<>();
        ArrayList<Double> yEast = new ArrayList<>();
        ArrayList<float[]> waveforms = new ArrayList<>();
        ArrayList<float[]> unCut = new ArrayList<>();
        double delta = 1.0;
        for (StreamKey sk : channels) {
            StackData sd = StackModel.getInstance().getStackData(sk);
            CssSeismogram fullSeis = sd.getData().getSeismogram();
            unCut.add(fullSeis.getData());
            CssSeismogram seis = new CssSeismogram(fullSeis);
            delta = seis.getDelta();

            double time = seis.getTimeAsDouble() + prePickSeconds + windowStart;

            seis.cut(new TimeT(time), new TimeT(time + fkWindowLength));
            float[] tmp = seis.getData();
            xNorth.add(sd.getdNorth());
            yEast.add(sd.getdEast());
            waveforms.add(tmp);
        }
        unCut = maybeTrimUncutArrays(unCut);
        float[] xnorth = new float[xNorth.size()];
        float[] yeast = new float[yEast.size()];
        for (int j = 0; j < xnorth.length; ++j) {
            xnorth[j] = (float) (double) xNorth.get(j);
            yeast[j] = (float) (double) yEast.get(j);
        }

        float smax = (float) ParameterModel.getInstance().getFKMaxSlowness();
        int numSlowness = ParameterModel.getInstance().getFKNumSlowness();
        double minFrequency = ParameterModel.getInstance().getFKMinFrequency();
        double maxFrequency = ParameterModel.getInstance().getFKMaxFrequency();
        if (xnorth.length < 2 || yeast.length < 2 || (SeriesMath.getStDev(yeast) == 0 && SeriesMath.getStDev(xnorth) == 0)) {
            ApplicationLogger.getInstance().log(Level.WARNING, "No array geometry available!");
            return null;
        }
        result = new FKProducer().produce(smax, numSlowness, xnorth, yeast, waveforms, (float) delta, (float) minFrequency, (float) maxFrequency);
        SlownessValue sv = result.getPeakValue();

        double backAzimuth = sv.getAzimuth();
        double apparentVelocity = sv.getVelocity();
        double[][] channelCoordinates = getChannelCoordinates(xnorth, yeast);
        Beamformer beamFormer = new Beamformer(channelCoordinates, backAzimuth, apparentVelocity, delta);
        float[][] beamInput = new float[unCut.size()][];
        for (int j = 0; j < beamInput.length; ++j) {
            beamInput[j] = unCut.get(j);
        }
        float[] beam = beamFormer.beam((beamInput));
        return null;
    }

    public double[][] getChannelCoordinates(float[] xnorth, float[] yeast) {
        double[][] result = new double[xnorth.length][2];
        for (int j = 0; j < xnorth.length; ++j) {
            result[j][0] = xnorth[j];
            result[j][1] = yeast[j];
        }
        return result;
    }

    @Override
    public void done() {
        CorrelatedTracesModel.getInstance().setMouseMode(MouseMode.SELECT_ZOOM);
        ClusterBuilderFrame.getInstance().returnFocusToTree();

        try {
            get();
            if (result != null && result.isValid()) {
                SingleEventFKFrame.getInstance().setVisible(true);
                SingleEventFKModel.getInstance().addFKResult(result);
            }
            ClusterBuilderFrame.getInstance().returnFocusToTree();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(FKWorker.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<float[]> maybeTrimUncutArrays(ArrayList<float[]> data) {
        ArrayList<float[]> result = new ArrayList<>();
        int minLength = Integer.MAX_VALUE;
        for (float[] fa : data) {
            if (fa.length < minLength) {
                minLength = fa.length;
            }
        }
        if (minLength < 2) {
            throw new IllegalStateException("One or more arrays are too short to process!");
        }
        for (int j = 0; j < data.size(); ++j) {
            float[] fa = data.get(j);
            if (fa.length > minLength) {
                float[] tmp = new float[minLength];
                System.arraycopy(fa, 0, tmp, 0, minLength);
                result.add(tmp);

            } else {
                result.add(fa);
            }
        }
        return result;
    }

}
