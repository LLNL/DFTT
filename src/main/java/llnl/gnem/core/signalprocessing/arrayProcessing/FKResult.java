/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.signalprocessing.arrayProcessing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import llnl.gnem.core.util.SeriesMath;

/**
 *
 * @author dodge1
 */
public class FKResult {

    private final float[] xnorth;
    private final float[] xeast;
    private final float[][] fks;
    private final SlownessValue peakValue;
    private final double quality;
    private final double maxPower;
    private final double twoDBRadius;
    private final int ixmax;
    private final int iymax;
    private final boolean valid;

    public FKResult(float[] xnorth, float[] xeast, float[][] fks, SlownessValue result, double maxPower, double quality) {
        this.xnorth = xnorth.clone();
        this.xeast = xeast.clone();
        this.fks = fks.clone();
        this.peakValue = result;
        this.quality = quality;
        this.maxPower = maxPower;

// Compute indices of max power point...        
       double mmp = 0;
        int ii = -1;
        int jj = -1;
        for(int i = 0; i < fks.length; ++i){
            for(int k = 0; k <fks.length; ++k){
                if(fks[i][k] > mmp){
                    ii = i;
                    jj = k;
                    mmp = fks[i][k];
                }
            }
        }
        
        
        
        this.ixmax = ii;
        this.iymax = jj;
        this.twoDBRadius = computeTwoDbRadius(xnorth,
                xeast,
                fks,
                result,
                maxPower);
        valid = true;
    }

    public FKResult() {
        ixmax = -1;
        iymax = -1;
        maxPower = -999;
        quality = -999;
        twoDBRadius = -999;
        peakValue = new SlownessValue(10, 0);
        fks = new float[1][1];
        xeast = new float[1];
        xnorth = new float[1];
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public float[] getXnorth() {
        return xnorth;
    }
    
    public double[] getXnorthDouble() {
        double[] result = new double[xnorth.length];
        for (int j = 0; j < xnorth.length; ++j) {
            result[j] = xnorth[j];
        }
        return result;
    }

    public float[] getXeast() {
        return xeast;
    }

    public double[] getXeastDouble() {
        double[] result = new double[xeast.length];
        for (int j = 0; j < xeast.length; ++j) {
            result[j] = xeast[j];
        }
        return result;
    }

    public float[][] getFks() {
        return fks;
    }

    public SlownessValue getPeakValue() {
        return peakValue;
    }

    public double getQuality() {
        return quality;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public double getTwoDBRadius() {
        return twoDBRadius;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Arrays.hashCode(this.xnorth);
        hash = 59 * hash + Arrays.hashCode(this.xeast);
        hash = 59 * hash + Arrays.deepHashCode(this.fks);
        hash = 59 * hash + Objects.hashCode(this.peakValue);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.quality) ^ (Double.doubleToLongBits(this.quality) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.maxPower) ^ (Double.doubleToLongBits(this.maxPower) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.twoDBRadius) ^ (Double.doubleToLongBits(this.twoDBRadius) >>> 32));
        hash = 59 * hash + this.ixmax;
        hash = 59 * hash + this.iymax;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FKResult other = (FKResult) obj;
        if (Double.doubleToLongBits(this.quality) != Double.doubleToLongBits(other.quality)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxPower) != Double.doubleToLongBits(other.maxPower)) {
            return false;
        }
        if (Double.doubleToLongBits(this.twoDBRadius) != Double.doubleToLongBits(other.twoDBRadius)) {
            return false;
        }
        if (this.ixmax != other.ixmax) {
            return false;
        }
        if (this.iymax != other.iymax) {
            return false;
        }
        if (!Arrays.equals(this.xnorth, other.xnorth)) {
            return false;
        }
        if (!Arrays.equals(this.xeast, other.xeast)) {
            return false;
        }
        if (!Arrays.deepEquals(this.fks, other.fks)) {
            return false;
        }
        if (!Objects.equals(this.peakValue, other.peakValue)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FKResult{" + "xnorth=" + xnorth + ", xeast=" + xeast + ", fks=" + fks + ", peakValue=" + peakValue + ", quality=" + quality + ", maxPower=" + maxPower + ", twoDBRadius=" + twoDBRadius + '}';
    }

    private double computeTwoDbRadius(float[] sNorthArray,
            float[] sEastArray,
            float[][] fks,
            SlownessValue result,
            double maxPower) {
        double ratio = Math.pow(10.0, -2.0 / 20);
        double contourValue = ratio * maxPower;

        
        int npts = 12;
        double dTheta = (Math.PI * 2) / (npts - 1);
        ArrayList<Double> values = new ArrayList<>();
        for (int j = 0; j < npts; ++j) {
            double theta = j * dTheta;
            SlownessValue p = getEdgeValues(sNorthArray, sEastArray, fks, ixmax, iymax, theta, contourValue);
            double sx = (Double) p.getsNorth();
            double sy = (Double) p.getsEast();
            double dsx = result.getsNorth() - sx;
            double dsy = result.getsEast() - sy;
            values.add(Math.sqrt(dsx * dsx + dsy * dsy));
        }
        return SeriesMath.getMean(values);
    }

    private SlownessValue getEdgeValues(float[] sNorthArray,
            float[] sEastArray,
            float[][] fks,
            int ixmax,
            int iymax,
            double theta,
            double contourValue) {
        double dsx = Math.abs(sNorthArray[1] - sNorthArray[0]);
        double dsy = Math.abs(sEastArray[1] - sEastArray[0]);
        double ds = Math.min(dsx, dsy);
        double sint = Math.sin(theta);
        double cost = Math.cos(theta);

        double slownessRadius = 0;
        while (true) {
            slownessRadius += ds;
            double xChange = slownessRadius * cost;
            double yChange = slownessRadius * sint;
            int nX = (int) Math.round(xChange / dsx);
            int nY = (int) Math.round(yChange / dsy);
            int j = ixmax + nX;
            int k = iymax + nY;
            if (j >= sNorthArray.length || k >= sEastArray.length || j < 0 || k < 0) {
                j = Math.max(Math.min(sNorthArray.length - 1, j), 0);
                k = Math.max(Math.min(sEastArray.length - 1, k), 0);
                return new SlownessValue(sNorthArray[j], sEastArray[k]);
            }
            double power = fks[j][k];
            if (power <= contourValue) {
                return new SlownessValue(sNorthArray[k], sEastArray[j]);
            }
        }
    }

    public SlownessContourValues getContourAroundPeak(double dbDown) {
        dbDown = dbDown > 0 ? -dbDown : dbDown;
        double ratio = Math.pow(10.0, dbDown / 20);
        double contourValue = ratio * maxPower;

        int npts = 50;
        double dTheta = (Math.PI * 2) / (npts - 1);
        ArrayList<SlownessValue> values = new ArrayList<>();
        for (int j = 0; j < npts; ++j) {
            double theta = j * dTheta;
            values.add(getEdgeValues(xnorth, xeast, fks, ixmax, iymax, theta, contourValue));
        }
        ArrayList<SlownessValue> result = getSmoothedContour(npts, values);
        return new SlownessContourValues(result, String.format("%3.1f dB", dbDown));
    }

    private ArrayList<SlownessValue> getSmoothedContour(int npts, ArrayList<SlownessValue> values) {
        ArrayList<SlownessValue> result = new ArrayList<>();
        for (int j = 0; j < npts; ++j) {
            SlownessValue p = averagePairs(values, j);
            result.add(p);
        }
        result.add(new SlownessValue(result.get(0).getsX(), result.get(0).getsY()));
        return result;
    }

    private SlownessValue averagePairs(ArrayList<SlownessValue> values, int j) {
        int idx1 = j - 1;
        if (idx1 < 0) {
            idx1 = values.size() - 1;
        }
        SlownessValue p1 = values.get(idx1);
        SlownessValue p2 = values.get(j);
        int idx2 = j + 1;
        if (idx2 >= values.size()) {
            idx2 = 0;
        }
        SlownessValue p3 = values.get(idx2);
        double v1 = ((Double) p1.getsX() + (Double) p2.getsX() + (Double) p3.getsX()) / 3;
        double v2 = ((Double) p1.getsY() + (Double) p2.getsY() + (Double) p3.getsY()) / 3;
        return new SlownessValue(v1, v2);
    }

    public void writeSpectrumASCII(String filename) throws FileNotFoundException {
        int nx = this.xnorth.length;
        int ny = this.xeast.length;
        try (PrintWriter pw = new PrintWriter(filename)) {
            for (int j = 0; j < nx; ++j) {
                for (int k = 0; k < ny; ++k) {
                    pw.print(fks[j][k]);
                    pw.print(" ");
                }
                pw.println();
            }
        }
    }

    public void writeNorthSlownessASCI(String filename) throws FileNotFoundException {
        int nx = this.xnorth.length;
        try (PrintWriter pw = new PrintWriter(filename)) {
            for (int j = 0; j < nx; ++j) {
                pw.println(xnorth[j]);
            }
        }
    }

    public void writeEastSlownessASCI(String filename) throws FileNotFoundException {
        int ny = this.xeast.length;
        try (PrintWriter pw = new PrintWriter(filename)) {
            for (int j = 0; j < ny; ++j) {
                pw.println(xeast[j]);
            }
        }
    }

    public double getSeastMin() {
        return xeast[0];
    }

    public double getSeastMax() {
        return xeast[xeast.length - 1];
    }

    public double getSnorthMin() {
        return xnorth[0];
    }

    public double getSnorthMax() {
        return xnorth[xnorth.length - 1];
    }

    public double[][] getFksDouble() {
        double[][] result = new double[fks.length][fks[0].length];
        for(int j = 0; j < fks.length;++j){
            for(int k = 0; k < fks[0].length;++k){
                result[j][k] = fks[j][k];
            }
        }
        return result;
    }

}
