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
package llnl.gnem.dftt.core.waveform.seismogram;

import org.apache.commons.math3.complex.Complex;

import llnl.gnem.dftt.core.database.row.StoredFilterRow;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.Passband;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.util.seriesMathHelpers.DiscontinuityCollection;
import llnl.gnem.dftt.core.util.seriesMathHelpers.MinMax;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;

/**
 *
 * @author addair1
 */
public interface SeismicSignal {

    /**
     * Add a scalar to the time series of this CssSeismogram
     *
     * @param value The scalar value to be added to the time series
     */
    void AddScalar(double value);

    /**
     * Calculate the envelope of the time series of this Seismogram. replaces
     * the data with the envelope
     */
    void toEnvelope();

    Complex[] FFT();

    /**
     * Calculate the hilbert transform of the time series of this CssSeismogram.
     * replaces the data with the hilbert transform
     */
    void Hilbert();

    /**
     * Replaces each point in this Seismogram with its log10 value.
     */
    void Log10();

    /**
     * Multiply the time series values of this CssSeismogram by a scalar
     * constant.
     *
     * @param value The scalar value with which to multiply the time series
     * values
     */
    void MultiplyScalar(double value);

    /**
     * Remove the mean of the time series of this CssSeismogram
     */
    void RemoveMean();

    /**
     * Remove the median value of the time series of this CssSeismogram
     */
    void RemoveMedian();

    /**
     * Replaces each point in the Seismogram its signed sqrt
     * <p>
     * </p>
     * Note: values LT 0 are returned -1* sqrt(abs(value)).
     */
    void SignedSqrt();

    /**
     * Replaces each point in the Seismogram its signed square value
     * <p>
     * </p>
     * Note: values LT 0 are returned -1* value*value.
     */
    void SignedSquare();

    /**
     * Convert to single bit (+1, -1 or 0)
     */
    void Signum();

    /**
     * Smooth the Seismogram using a sliding window of width halfWidth. replaces
     * the data with it's smoothed result
     * <p>
     * </p>
     * Note halfwidth refers to number of samples, not seconds
     *
     * @param halfwidth half width in samples.
     */
    void Smooth(int halfwidth);

    /**
     * Replaces each point in the Seismogram with its sqrt
     * <p>
     * </p>
     * Note: values LT 0 are returned 0.
     */
    void Sqrt();

    /**
     * Replaces each point in the Seismogram with its square value
     */
    void Square();

    /**
     * Apply a cosine taper to the time series of this seismogram
     *
     * @param TaperPercent The (one-sided) percent of the time series to which a
     * taper will be applied. The value ranges from 0 (no taper) to 50 ( The
     * taper extends half the length of the CssSeismogram ). Since the taper is
     * symmetric, a 50% taper means that all but the center value of the
     * CssSeismogram will be scaled by some value less than 1.0.
     */
    void Taper(double TaperPercent);

    boolean applyFilter(StoredFilterRow filter);

    boolean applyFilter(StoredFilter filter);

    double computeExtremeStat();

    boolean contains(Epoch epoch, boolean allowGaps);

    /**
     * Differentiate the time series of this CssSeismogram. First two points are
     * differentiated using a forward-difference with error Oh2, Last two points
     * using a backward-difference operator with error Oh2. Remaining points
     * differentiated using a central-difference operator with order Oh4 (page
     * 397 - 399 of Applied Numerical Methods for Digital Computation by James
     * et al. ) Must be at least 4 points in series for this to work.
     */
    void differentiate();

    void filter(double lc, double hc);

    void filter(double lc, double hc, boolean twoPass);

    /**
     * Apply a Butterworth filter to the time series data of this CssSeismogram.
     *
     * @param order The order of the filter to be applied
     * @param passband The passband of the filter. Passband is one of
     * Passband.LOW_PASS, Passband.HIGH_PASS, Passband.BAND_PASS,
     * Passband.BAND_REJECT
     * @param cutoff1 For BAND_PASS and BAND_REJECT filters this is the low
     * corner of the filter. For HIGH_PASS and LOW_PASS filters, this is the
     * single corner frequency
     * @param cutoff2 For BAND_PASS and BAND_REJECT filters, this is the
     * high-frequency corner. For other filters, this argument is ignored.
     * @param two_pass When true, the filter is applied in both forward and
     * reverse directions to achieve zero-phase.
     */
    void filter(int order, Passband passband, double cutoff1, double cutoff2, boolean two_pass);

    DiscontinuityCollection findDiscontinuities(int winLength, double factor);

    double getDelta();

    double getDistinctValueRatio(int numSamples);

    /**
     * Gets the endtime attribute of the CssSeismogram object
     *
     * @return The endtime value
     */
    TimeT getEndtime();

    Epoch getEpoch();

    float getExtremum();

    Long getWaveformID();

    int getIndexForTime(double epochtime);

    int getJdate();

    int getLength();

    double getLengthInSeconds();

    /**
     * Gets the maximum value of the time series of the CssSeismogram object
     *
     * @return The max value
     */
    float getMax();

    /**
     * Gets the maximum value of the series and the time offset it occurs at.
     *
     * @return The (time offset at the max in seconds, the max value) of the
     * series
     */
    double[] getMaxTime();

    /**
     * Gets the mean value of the time series of the CssSeismogram object
     *
     * @return The mean value
     */
    double getMean();

    /**
     * Gets the median value of the time series of the CssSeismogram object
     *
     * @return The median value
     */
    double getMedian();

    /**
     * Gets the minimum value of the time series of the BasicSeismogram object
     *
     * @return The min value
     */
    float getMin();

    int getNsamp();

    /**
     * Gets the Nyquist Frequency of the CssSeismogram
     *
     * @return The nyquistFreq value
     */
    double getNyquistFreq();

    /**
     * Gets the peakToPeakAmplitude attribute of the CssSeismogram's timeseries
     * at the specified period. Advances a sliding window of length period
     * through the time series a point at a time. At each position, the
     * Peak-To-Peak range of the current window is computed. The maximum value
     * of all these values is returned.
     *
     * @param period The period in seconds at which to compute the value.
     * @return The maximum Peak-To-Peak value for the entire seismogram.
     */
    double getPeakToPeakAmplitude(double period);

    int getPointsIn(TimeT start, TimeT end);

    int getPointsIn(double timeRange);

    double getPower();

    /**
     * Gets the RMS value of the CssSeismogram's time series.
     *
     * @return The RMS value
     */
    double getRMS();

    /**
     * Gets the range of the time series
     *
     * @return The statistical range for all values in the data
     */
    double getRange();

    /**
     * Gets the samprate attribute of the CssSeismogram object
     *
     * @return The samprate value
     */
    double getSamprate();

    /**
     * Gets the segment Length in seconds of the CssSeismogram object
     *
     * @return The segmentLength value
     */
    double getSegmentLength();

    double getSnr(double pickEpochTime, double preSeconds, double postSeconds);

    double getSnr(double pickEpochTime, Epoch epoch, double preSeconds, double postSeconds);

    /**
     * Gets the variance of the time series of the CssSeismogram object
     *
     * @return The variance value
     */
    double getStDev();

    /**
     * Gets the sum of the time series values of this CssSeismogram
     *
     * @return The sum of the time series values
     */
    double getSum();

    /**
     * Gets the start time of the CssSeismogram as a TimeT object
     *
     * @return The time value
     */
    TimeT getTime();

    /**
     * Gets the start time of the CssSeismogram as a double holding the epoch
     * time of the start.
     *
     * @return The CssSeismogram start epoch time value
     */
    double getTimeAsDouble();

    /**
     * Get the value at a specific point in time
     *
     * @param epochtime The time expressed as a double epoch time.
     * @return the value at the requested time
     */
    float getValueAt(double epochtime);

    /**
     * Gets the variance of the time series of the CssSeismogram object
     *
     * @return The variance value
     */
    double getVariance();

    /**
     * In-place test to see if the subset of the seismogram is a single value
     * @param epoch
     * @return 
     */
    boolean hasVariance(Epoch epoch);

    boolean hasVariance();

    /**
     * Integrate the time series in the time domain
     */
    void integrate();

    boolean isEmpty();

    void normalize();

    /**
     * Computed the median in O(n) time in contrast to standard O(n lg n) time.
     * The only tradeoff is that the median is not the average of the two center
     * points for series with even length. Rather, the median is arbitrarily
     * selected from amongst one of those two approximate centers.
     *
     * @return The median or pseudo-median for even length series
     */
    double quickMedian();

    /**
     * remove glitches from the seismogram where glitches are defined by data
     * that exceed a threshhold above the variance defined by a moving window
     * <p>
     * </p>
     * value = Math.abs((data[j] - median)); if (value GT Threshhold *
     * Math.sqrt(variance)) replace data[j] with the median value
     *
     * @param Threshhold - the threshhold value
     */
    void removeGlitches(double Threshhold);

    /**
     * Remove a linear trend from the time series data of this CssSeismogram.
     */
    void removeTrend();

    /**
     * Reverse the data series. This method is used in cross correlation
     * routines. Note none of the times are being reset. The user must be
     * careful to understand the implications
     */
    void reverse();

    void scaleTo(double min, double max);

    void setMaximumRange(double maxRange);

    void setSamprate(double samprate);

    void triangleTaper(double taperPercent);

    /**
     * Spectrally whiten the seismogram
     */
    void whiten();

    MinMax getMinMax();

}
