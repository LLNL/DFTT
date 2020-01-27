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
package llnl.gnem.core.waveform.classification.peaks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import llnl.gnem.core.util.SeriesMath;

public class PeakCollection {

    private final ArrayList<Peak> thePeaks;
    private double maxArea;
    private final static double significanceRatio = .33;

    public static PeakCollection getSignalPeaks(float[] data, double dt) {
        PeakCollection result = new PeakCollection();

        int smoothWinLen = 20;
        if (data.length < 4 * smoothWinLen) {
            return result;
        }
        float[] x = data.clone();

        SeriesMath.RemoveMean(x);
        SeriesMath.Taper(x, 2);
        float[] padded = new float[x.length + 2 * smoothWinLen];
        Arrays.fill(padded, 0, smoothWinLen-1, x[0]);
        Arrays.fill(padded, x.length + smoothWinLen, padded.length-1, x[x.length-1]);
        System.arraycopy(x, 0, padded, smoothWinLen, x.length);
        
        float[] tmpEnvelope = SeriesMath.envelope(padded);
        for (int k = 0; k < 4; ++k) {
            tmpEnvelope = SeriesMath.MeanSmooth(tmpEnvelope, smoothWinLen);
        }
        float[] envelope = new float[x.length];
        System.arraycopy(tmpEnvelope, smoothWinLen, envelope, 0, x.length);
        int N = envelope.length;


        PeakFinder pf = new PeakFinder();
        double erange = SeriesMath.getRange(envelope);
        
        ArrayList<BasePeak> thePeaks = pf.findPeaks(envelope, dt, erange/2);

        int previousEndIndex = -1;
        for (int m = 0; m < thePeaks.size(); ++m) {
            BasePeak aPeak = thePeaks.get(m);

            double prePeakMedian = getPrePeakMedian(envelope, thePeaks, m);
            int idx0 = getPeakStartIndex(aPeak, prePeakMedian, envelope, dt);
            if (idx0 < previousEndIndex) {
                idx0 = previousEndIndex + 1;
            }
            int idx2 = getPeakEndIndex(aPeak, prePeakMedian, envelope, dt);
            previousEndIndex = idx2;
            double area = getArea(envelope, idx0, idx2);
            result.addPeak(new Peak(aPeak, idx0, idx2, area));
        }

        return result;
    }

    public static int getPeakStartIndex(BasePeak aPeak, double prePeakMedian, float[] envelope, double dt) {
        double peakHeight = aPeak.getValue() - prePeakMedian;
        double threshold = peakHeight / 10;
        int idx = aPeak.getIndex();
        double lastValue = envelope[idx] - prePeakMedian;
        for (int k = idx - 1; k >= 0; --k) {
            double v = envelope[k] - prePeakMedian;
            if (v < lastValue) {
                lastValue = v;
            }
            if (v <= threshold) {
                return k;
            }
        }
        return 0;
    }

    private static int getPeakEndIndex(BasePeak aPeak, double prePeakMedian, float[] envelope, double dt) {
        double peakHeight = aPeak.getValue() - prePeakMedian;
        double threshold = peakHeight / 10;
        int idx = aPeak.getIndex();
        double lastValue = envelope[idx] - prePeakMedian;
        for (int k = idx + 1; k < envelope.length; ++k) {
            double v = envelope[k] - prePeakMedian;
            if (v < lastValue) {
                lastValue = v;
            }
            if (v <= threshold) {
                return k;
            }
        }
        return envelope.length - 1;
    }

    private static double getPrePeakMedian(float[] envelope, ArrayList<BasePeak> peaks, int idx) {
        BasePeak aPeak = peaks.get(idx);
        if (idx == 0) {
            int pidx = aPeak.getIndex();
            int npts = pidx / 3;
            if (npts >= 3) {
                ArrayList<Double> samples = new ArrayList<>();
                for (int j = 0; j <= npts; ++j) {
                    samples.add((double) envelope[j]);
                }
                return SeriesMath.getMedian(samples);
            } else {
                return envelope[0];
            }
        } else {
            BasePeak peakBefore = peaks.get(idx - 1);
            int lastPeakIdx = peakBefore.getIndex();
            int thisPeakIdx = aPeak.getIndex();
            int numMedianPoints = (thisPeakIdx - lastPeakIdx) / 3;
            if (numMedianPoints >= 3) {
                ArrayList<Double> samples = new ArrayList<>();
                for (int j = lastPeakIdx + numMedianPoints; j <= lastPeakIdx + 2 * numMedianPoints; ++j) {
                    samples.add((double) envelope[j]);
                }
                return SeriesMath.getMedian(samples);
            } else {
                return envelope[lastPeakIdx + numMedianPoints];
            }
        }
    }

    public double getSignalEnd() {
        return thePeaks.get(thePeaks.size() - 1).getEndTime();
    }

    public PeakCollection() {
        thePeaks = new ArrayList<>();
        maxArea = -Double.MAX_VALUE;
    }

    public void addPeak(Peak peak) {
        thePeaks.add(peak);
        maxArea = peak.getArea() > maxArea ? peak.getArea() : maxArea;
    }

    public int size() {
        return thePeaks.size();
    }

    public Peak getPeak(int i) {
        return thePeaks.get(i);
    }

    public void removeInsignificantPeaks() {
        if (thePeaks.size() < 2) {
            return;
        }
        Iterator<Peak> it = thePeaks.iterator();
        while (it.hasNext()) {
            Peak peak = it.next();
            if (peak.getArea() < significanceRatio * maxArea) {
                it.remove();
            }
        }
    }

    public void list() {
        for (Peak peak : thePeaks) {
            System.out.println(peak);
        }
    }

    public int getEndPoint() {
        return thePeaks.get(thePeaks.size() - 1).getEndIndex();
    }

    private static double getArea(float[] envelope, int idx0, int idx1) {
        double area = 0;
        for (int j = idx0; j < idx1; ++j) {
            area += (envelope[j] + envelope[j + 1]) / 2;
        }
        return area;
    }

    public double getTotalWidth() {
        double sum = 0;
        for (Peak peak : thePeaks) {
            sum += peak.getWidth();
        }
        return sum;
    }

    public double getTotalArea() {
        double sum = 0;
        for (Peak peak : thePeaks) {
            sum += peak.getArea();
        }
        return sum;
    }

    public Collection<Peak> getPeaks() {
        return new ArrayList<>(thePeaks);
    }

}
