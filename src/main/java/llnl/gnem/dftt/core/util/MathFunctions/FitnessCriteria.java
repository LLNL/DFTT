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
package llnl.gnem.dftt.core.util.MathFunctions;
import llnl.gnem.dftt.core.util.SeriesMath;
import java.util.HashMap;
import java.util.Collection;
/**
 *
 * @author matzel1
 */
public class FitnessCriteria
{

    /**
     * 
     * @param data
     * @param reference
     * @return
     */
    public static float[] Differential(float[] data, float[] reference)
    {
        //if (data.length != reference.length)       TODO encountering an error where one trace is a single element longer than the other
        //    return Float.MAX_VALUE;
        int minnpts = (data.length < reference.length) ? data.length : reference.length; // find the number of points in the shorter trace

        float[] Difference = new float[minnpts];// the sum of the squares of the differences (data - reference)

        for (int ii = 0; ii < minnpts; ii++)
        {
            Difference[ii] = data[ii] - reference[ii];
        }

        return Difference;
    }

    /**
     * Calculate the sum of the square of the differences between two series
     *
     *      SUM((data - reference)^2)
     *
     * @param data - the first series as an array of doubles
     * @param reference - the reference series as an array of doubles
     * @return the sum of the squares of the differences
     */
    public static Double SumSquareDifference(double[] data, double [] reference)
    {
        //if (data.length != reference.length)       TODO encountering an error where one trace is a single element longer than the other
        //    return Float.MAX_VALUE;
        int minnpts = (data.length < reference.length) ? data.length : reference.length; // find the number of points in the shorter trace

        Double SumSquareDifference = 0.;// the sum of the squares of the differences (data - reference)

        for (int ii = 0; ii < minnpts; ii++)
        {
            SumSquareDifference += Math.pow((data[ii] - reference[ii]), 2);
        }

        return SumSquareDifference;
    }

    /**
     * Calculate the sum of the square of the differences between two series
     *
     *      SUM((data - reference)^2)
     *
     * @param data - the first series as an array of floats
     * @param reference - the reference series as an array of floats
     * @return the sum of the squares of the differences
     */
    public static Double SumSquareDifference(float [] data, float [] reference)
    {
        //if (data.length != reference.length)       TODO encountering an error where one trace is a single element longer than the other
        //    return Float.MAX_VALUE;
        int minnpts = (data.length < reference.length) ? data.length : reference.length; // find the number of points in the shorter trace

        Double SumSquareDifference = 0.;// the sum of the squares of the differences (data - reference)

        for (int ii = 0; ii < minnpts; ii++)
        {
            SumSquareDifference += Math.pow((data[ii] - reference[ii]), 2);
        }

        return SumSquareDifference;
    }

    /**
     * Calculate the sum of the squares of the data series
     * @param data - an array of doubles
     * @return the sum of the squares of the series (NOTE unnormalized)
     */
    public static Double SumSquares(double [] data)
    {
        Double SumSquares = 0.;// the sum of the squares of the differences (data - reference)

        for (int ii = 0; ii < data.length; ii++)
        {
            SumSquares += data[ii] * data[ii];
        }

        return SumSquares;
    }

    /**
     * Calculate the sum of the squares of the data series
     * @param data - an array of floats
     * @return the sum of the squares of the series (NOTE unnormalized)
     */
    public static Double SumSquares(float [] data)
    {
        Double SumSquares = 0.;// the sum of the squares of the differences (data - reference)

        for (int ii = 0; ii < data.length; ii++)
        {
            SumSquares += data[ii] * data[ii];
        }

        return SumSquares;
    }


    /**
     * Root Mean Square Difference (aka Root Mean Square Deviation (RMSD)  or Root Mean Square Error (RMSE))
     * @param data   - a data series as a float array
     * @param reference  - a reference series as a float array
     * @return the RMSD of the two series
     */
    public static Double RMSD(float[] data, float[] reference)
    {
        //if (data.length != reference.length)       TODO encountering an error where one trace is a single element longer than the other
        //    return Float.MAX_VALUE;
        int minnpts = (data.length < reference.length) ? data.length : reference.length; // find the number of points in the shorter trace

        Double SumSquareDifference = SumSquareDifference(data, reference);
        Double MeanSumSquares = SumSquareDifference/minnpts;
        return Math.sqrt(MeanSumSquares);      // RMSD = sqrt of the SUM of the Squares of the Differences
    }

    /**
     * Normalized Root Mean Square Difference (NRMSD)
     *
     *      RMSD/ (xmax - xmin)
     *
     * @param data   - a data series as a float array
     * @param reference - a reference series as a float array
     * @return the Normalized RMSD of the two series
     */
    public static Double NRMSD(float[] data, float[] reference)
    {
        float xmax = SeriesMath.getMax(reference);    // the largest  number in the reference series
        float xmin = SeriesMath.getMin(reference);    // the smallest number in the reference series

        if (xmax == xmin)
        {
            return null;
        }

        Double RMSD = RMSD(data, reference);    // RMSD = sqrt of the SUM of the Squares of the Differences
        
        return RMSD/ (xmax - xmin);
    }


    // The following methods are various implementations a Root-Mean-Square fitness criteria
    // RMS = SUM((data - reference)^2) / SUM(reference^2)

    /**
     * The Coefficient of Variation of the Root-Mean-Square Difference   (aka CV(RMSD) or CV(RMSE))
     *
     * RMS = SUM((data - reference)^2) / SUM(reference^2)
     *
     * @param data      - an array of floats
     * @param reference - an array of floats (must be the same length as the data array)
     * @return a single float measure of the RMS fit
     */
    public static Double CVRMSD(float[] data, float[] reference)
    {
        //if (data.length != reference.length)       TODO encountering an error where one trace is a single element longer than the other due to measurement limits
        //    return Float.MAX_VALUE;

        int minnpts = (data.length < reference.length) ? data.length : reference.length; // find the number of points in the shorter trace

        if (minnpts <= 0)
        {
            return null;
        }
        
        Double SumSquareReference = SumSquares(reference); //sum of the square value of each reference value
        Double SumSquareDifference = SumSquareDifference(data, reference);// the sum of the squares of the differences (data - reference)

        return Math.sqrt(SumSquareDifference / SumSquareReference);   // CV (RMSD)
    }

    /**
     * The Coefficient of Variation of the Root-Mean-Square Difference  (aka CV(RMSD) or CV(RMSE))
     *
     * RMS = SUM((data - reference)^2) / SUM(reference^2)
     *
     * @param data      - an array of doubles
     * @param reference - an array of doubles (must be the same length as the data array)
     * @return a single double valued measure of the RMS fit
     */
    public static Double CVRMSD(double[] data, double[] reference)
    {
        //if (data.length != reference.length)    TODO encountering an error where one trace is a single element longer than the other
        //    return Double.MAX_VALUE;
        int minnpts = (data.length < reference.length) ? data.length : reference.length; // find the number of points in the shorter trace

        if (minnpts <= 0)
        {
            return null;
        }

        double SumSquareReference = SumSquares(reference); //sum of the square value of each reference value
        double SumSquareDifference = SumSquareDifference(data, reference);// the sum of the squares of the differences (data - reference)

        return Math.sqrt(SumSquareDifference / SumSquareReference);   // CV (RMSD)
    }

    /**
     * The Coefficient of Variation of the Root-Mean-Square Difference  (aka CV(RMSD) or CV(RMSE))
     * 
     * @param dataHashMap a HashMap of data and reference values to be compared
     * note that the HashMap key is not used in the RMS fit procedure
     * @return the single float valued measure of the RMS fit
     */
    public static Double CVRMSD(HashMap<Object, double[]> dataHashMap)
    {
        double result = Double.MAX_VALUE;

        Collection values = dataHashMap.values();

        Object[] object = values.toArray();

        int size = object.length;

        double[] data = new double[size];
        double[] reference = new double[size];

        try
        {
            for (int ii = 0; ii < size; ii++)
            {
                double[] thisvalue = (double[]) object[ii];
                reference[ii] = thisvalue[0];
                data[ii] = thisvalue[1];
            }

            result = CVRMSD(data, reference);
        }
        catch (Exception e)
        {
            System.out.println("Exception thrown in RMS fit: " + e);
        }

        return result;

    }

    /**
     * The Mean Difference fitness criteria
     *
     * MD = SUM(data - reference) / SUM(reference)
     *
     * @param data      - an array of floats
     * @param reference - an array of floats (must be the same length as the data array)
     * @return a single float measure of the MD fit
     */
    public static Float MeanDifference(float[] data, float[] reference)
    {
       //if (data.length != reference.length)    TODO encountering an error where one trace is a single element longer than the other
        //    return Double.MAX_VALUE;
        int minnpts = (data.length < reference.length) ? data.length : reference.length; // find the number of points in the shorter trace

        if (minnpts <= 0)
        {
            return null;
        }

        float SumReference = 0; //sum of the value of each reference value
        float SumDifference = 0;// the sum of the differences (data - reference)

        for (int ii = 0; ii < data.length; ii++)
        {
            SumReference += Math.abs(reference[ii]);
            SumDifference += Math.abs(data[ii] - reference[ii]);
        }

        return SumDifference / SumReference;
    }

    /**
     * the coefficient of variation (CV) is a normalized measure of dispersion of a probability distribution. AKA unitized risk or variation coefficient
     *
     *          CV = stdev / mean
     *
     *  This is only defined for non-zero mean, and is most useful for variables that are always positive.
     * @param data - an array of floats
     * @return  CV = stdev(data) / mean(data)
     */
    public static Double CoefficientOfVariation(float [] data)
    {
        double stdev = SeriesMath.getStDev(data);
        double mean = SeriesMath.getMean(data);

        if (mean == 0)
        {
            return null;
        }

        return stdev/mean;        
    }

}
