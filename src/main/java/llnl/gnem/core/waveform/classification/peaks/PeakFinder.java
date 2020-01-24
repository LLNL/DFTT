/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.classification.peaks;

import java.util.ArrayList;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;

/**
 * Noise tolerant fast peak finding algorithm adapted from Matlab algorithm by
 * Nathanael C. Yoder 2011 (nyoder@gmail.com)
 */
public class PeakFinder {
   
    public PeakFinder() {}

    public enum ExtremumType {

        MAX, MIN
    }

    /**
     * Returns the peaks of the time series including endpoint peaks using a
     * default sel of range / 4
     *
     * @param x0 An array of floats representing the time series
     * @param dt The sample interval of the time series
     * @return a Collection of peaks found by the algorithm
     */
    public ArrayList<BasePeak> findPeaks(float[] x0, double dt) {
        double sel = (SeriesMath.getMax(x0) - SeriesMath.getMin(x0)) / 4;
        double thresh = -1;

        boolean includeEndpoints = true;
        return findPeaks(x0, dt, sel, thresh, ExtremumType.MAX, includeEndpoints);
    }

    /**
     * Returns the peaks of the time series including endpoint peaks using a
     * user-supplied sel (The amount above surrounding data for a peak to be
     * identified)
     *
     * @param x0 An array of floats representing the time series
     * @param dt The sample interval of the time series
     * @param sel The amount above surrounding data for a peak to be identified
     * @return a Collection of peaks found by the algorithm
     */
    public ArrayList<BasePeak> findPeaks(float[] x0, double dt, double sel) {
        double thresh = -1;
        boolean includeEndpoints = true;
        return findPeaks(x0, dt, sel, thresh, ExtremumType.MAX, includeEndpoints);
    }

    /**
     * Returns the peaks of the time series
     *
     * @param x0 The data to be examined
     * @param dt The sample interval
     * @param sel The amount above surrounding data for a peak to be identified
     * @param thresh A threshold value which peaks must be larger than to be
     * maxima or smaller than to be minima
     * @return a Collection of peaks found by the algorithm
     */
    public ArrayList<BasePeak> findPeaks(float[] x0, double dt, double sel, double thresh) {
        boolean includeEndpoints = true;
        return findPeaks(x0, dt, sel, thresh, ExtremumType.MAX, includeEndpoints);
    }

    /**
     * Returns the peaks of the time series
     *
     * @param x0 The data to be examined
     * @param dt The sample interval
     * @param sel The amount above surrounding data for a peak to be identified
     * @param thresh A threshold value which peaks must be larger than to be
     * maxima or smaller than to be minima
     * @param type Either MAX or MIN
     * @return a Collection of peaks found by the algorithm
     */
    public ArrayList<BasePeak> findPeaks(float[] x0, double dt, double sel, double thresh, ExtremumType type) {
        boolean includeEndpoints = true;
        return findPeaks(x0, dt, sel, thresh, type, includeEndpoints);
    }

    /**
     * Returns the peaks of the time series
     *
     * @param x0 The data to be examined
     * @param dt The sample interval
     * @param sel The amount above surrounding data for a peak to be identified
     * @param thresh A threshold value which peaks must be larger than to be
     * maxima or smaller than to be minima
     * @param type Either MAX or MIN
     * @param includeEndpoints If true, then peaks can be found at either the
     * beginning or end of the data.
     * @return a Collection of peaks found by the algorithm
     */
    public ArrayList<BasePeak> findPeaks(float[] x0, double dt, double sel, double thresh, ExtremumType type, boolean includeEndpoints) {

        int extrema = type == ExtremumType.MAX ? 1 : -1;
        ArrayList<BasePeak> result = new ArrayList<>();
        SeriesMath.MultiplyScalar(x0, extrema); // Make it so we are finding maxima regardless
        thresh = thresh * extrema; // Adjust threshold according to extrema.

        float[] dx0 = getFirstDifference(x0);

        ArrayList<Integer> ind = new ArrayList<>(); // Find where the derivative changes sign   
        for (int j = 0; j < dx0.length - 1; ++j) {
            float v = dx0[j] * dx0[j + 1];
            if (v < 0) {
                ind.add(j + 1);
            }
        }

        // Include endpoints in potential peaks and valleys as desired
        float[] x = buildFromIndices(x0, ind, includeEndpoints);
        float minMag = SeriesMath.getMin(x);
        float leftMin;
        if (includeEndpoints) {
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(0);
            tmp.addAll(ind);
            tmp.add(x0.length - 1);
            ind.clear();
            ind.addAll(tmp);

            leftMin = minMag;
        } else {

            leftMin = x0[0];
        }
        int len = x.length;

        int[] peakLoc = null;
        float[] peakMag;
        int[] peakInds;
        float[] peakMags;
        if (len > 2) { // Function with peaks and valleys
            // Set initial parameters for loop
            float tempMag = minMag;
            boolean foundPeak = false;
            int ii;

            if (includeEndpoints) {
                // Deal with first point a little differently since tacked it on
                // Calculate the sign of the derivative since we tacked the first 
                //  point on it does not neccessarily alternate like the rest.
                int[] signDx = SeriesMath.sign(SeriesMath.matlabDiff(SeriesMath.getSubSection(x, 0, 2)));
                if (signDx[0] <= 0) { // The first point is larger or equal to the second
                    if (signDx[0] == signDx[1]) // Want alternating signs
                    {
                        x = removeElementAt(x, 1);
                        ind = removeElementAt(ind, 1);
                        --len;
                    }
                } else { // First point is smaller than the second
                    if (signDx[0] == signDx[1]) { // Want alternating signs
                        x = removeElementAt(x, 0);
                        ind = removeElementAt(ind, 0);
                        --len;
                    }
                }
            }
            // Skip the first point if it is smaller so we always start on a maxima
            if (x[0] >= x[1]) {
                ii = -1;
            } else {
                ii = 0;
            }
            
            // Preallocate max number of maxima
            int maxPeaks = (int) Math.ceil(len / 2.0);
            peakLoc = new int[maxPeaks];
            peakMag = new float[maxPeaks];
            int cInd = 0;
            int tempLoc = 0;
            // Loop through extrema which should be peaks and then valleys
            while (ii < len - 1) {
                ++ii; // This is a peak
                // Reset peak finding if we had a peak and the next peak is bigger
                // than the last or the left min was small enough to reset.
                if (foundPeak) {
                    tempMag = minMag;
                    foundPeak = false;
                }

                // Make sure we don't iterate past the length of our vector
                if (ii == len - 1) {
                    break;
                } // We assign the last point differently out of the loop end

                // Found new peak that was lager than temp mag and selectivity larger than the minimum to its left
                if (x[ii] > tempMag && x[ii] > leftMin + sel) {
                    tempLoc = ii;
                    tempMag = x[ii];
                }

                ++ii; // Move onto the valley // Come down at least sel from peak
                if (!foundPeak && tempMag > sel + x[ii]) {
                    foundPeak = true;// We have found a peak

                    leftMin = x[ii];
                    peakLoc[cInd] = tempLoc; // Add peak to index 
                    peakMag[cInd] = tempMag;
                    ++cInd;
                } else if (x[ii] < leftMin) { // New left minima
                    leftMin = x[ii];
                }
            }

            // Check end point
            if (includeEndpoints) {
                if (x[len - 1] > tempMag && x[len - 1] > leftMin + sel) {
                    peakLoc[cInd] = len - 1;
                    peakMag[cInd] = x[len - 1];
                    ++cInd;

                } else if (!foundPeak && tempMag > minMag) { // Check if we still need to add the last point 
                    peakLoc[cInd] = tempLoc;
                    peakMag[cInd] = tempMag;
                    ++cInd;
                }
            } else if (!foundPeak) {
                if (tempMag > x0[x0.length - 1] + sel) {
                    peakLoc[cInd] = tempLoc;
                    peakMag[cInd] = tempMag;
                    ++cInd;
                }
            }

            // Create output
            peakInds = getSelectedElements(ind, SeriesMath.getSubSection(peakLoc, 0, cInd - 1));
            peakMags = SeriesMath.getSubSection(peakMag, 0, cInd - 1);
        } else { // This is a monotone function where an endpoint is the only peak
            PairT<Integer, Float> maxIdx = SeriesMath.getMaxIndex(x);
            peakMags = new float[]{maxIdx.getSecond()};
            int xInd = maxIdx.getFirst();
            if (includeEndpoints && peakMags[0] > minMag + sel) {
                peakInds = getSubSection(ind, xInd, xInd);
            } else {
                peakMags = null;
                peakInds = null;
            }

        }

         if (peakInds != null) { // There is at least one peak...
            if (thresh > 0) {
                for (int j = 0; j < peakInds.length; ++j) {
                    if (peakMags[j] > thresh) {
                        result.add(new BasePeak(peakInds[j], peakMags[j] * extrema, dt));
                    }
                }
            } else {
                for (int j = 0; j < peakInds.length; ++j) {
                    result.add(new BasePeak(peakInds[j], peakMags[j] * extrema, dt));
                }
            }
        }
        return result;
    }

    public float[] getFirstDifference(float[] x0) {
        float[] dx0 = x0.clone();// calculate first difference
        for (int j = 0; j < dx0.length - 1; ++j) {
            dx0[j] = dx0[j + 1] - dx0[j];
        }
        SeriesMath.replaceValue(dx0, 0, -Float.MIN_VALUE); //This is so we find the first of repeated values
        return dx0;
    }

    private float[] buildFromIndices(float[] in, ArrayList<Integer> ind, boolean useEndPoints) {
        int size = ind.size() + (useEndPoints ? 2 : 0);
        float[] result = new float[size];
        int j = 0;
        if (useEndPoints) {
            result[j++] = in[0];
        }
        for (int index : ind) {
            result[j++] = in[index];
        }
        if (useEndPoints) {
            result[j++] = in[in.length - 1];
        }
        return result;
    }

    private float[] removeElementAt(float[] x, int i) {
        float[] result = new float[x.length - 1];
        int k = 0;
        for (int j = 0; j < x.length; ++j) {
            if (j != i) {
                result[k++] = x[j];
            }
        }
        return result;
    }

    private ArrayList<Integer> removeElementAt(ArrayList<Integer> ind, int i) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int j = 0; j < ind.size(); ++j) {
            if (j != i) {
                result.add(ind.get(j));
            }
        }
        return result;
    }

    private int[] getSelectedElements(ArrayList<Integer> ind, int[] aaa) {
        int[] result = new int[aaa.length];
        for (int j = 0; j < aaa.length; ++j) {
            result[j] = ind.get(aaa[j]);
        }
        return result;
    }
    
    
    private int[] getSubSection(ArrayList<Integer> ind, int idx0, int idx1) {
        ArrayList<Integer> tmp = new ArrayList<>();
        for( int j = idx0; j <= idx1; ++j){
            tmp.add(ind.get(j));
        }
        int[] result = new int[tmp.size()];
        for( int j = 0; j < tmp.size(); ++j){
            result[j] = tmp.get(j);
        }
        return result;
    }


}
