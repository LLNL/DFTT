package llnl.gnem.core.gui.plotting.histogram;


import java.util.Arrays;


/**
 * Class to construct the set of HistogramBin objects that will be used to construct
 * the Histogram. Given the data and the number of required bins, identifies the
 * range of the data and the corresponding bin ranges, and assigns the data counts to each bin.
 * Created by: dodge1
 * Date: Jan 6, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class BinCollection {

    private HistogramBin[] bins;
    private float minValue;
    private float maxValue;

    /**
     * Gets the minimum value of the data used to construct this collection.
     * @return  The minimum data value.
     */
    public float getMinValue()
    {
        return minValue;
    }

    /**
     * Gets the maximum value of the data used to construct this collection.
     * @return  The maximum value of the data.
     */
    public float getMaxValue()
    {
        return maxValue;
    }

    /**
     * Constructs the collection given the data and the required number of bins.
     * @param values The array of data to be binned.
     * @param nBins The number of bins to produce.
     */
    public BinCollection( float[] values, int nBins )
    {
        bins = new HistogramBin[nBins];
        float[] tmp = values.clone();
        int dataLength = tmp.length;
        Arrays.sort( tmp );
        initializeAllBins( tmp, nBins );

        int currentBin = 0;
        for ( int j = 0; j < dataLength; ++j ) {
            currentBin = findContainingBin( currentBin, tmp[j] );
            if( currentBin < 0 && j == dataLength - 1 )
                currentBin = nBins - 1;

            if( currentBin >= 0 )
                bins[currentBin].incrementCount();
        }
    }

    private void initializeAllBins( float[] tmp, int nBins )
    {
        minValue = tmp[0];
        maxValue = tmp[tmp.length-1];
        float range = maxValue - minValue;
        float binRange = range / (float) nBins;

        float binStart = minValue;
        float binEnd = binStart + binRange;

        for ( int j = 0; j < nBins; ++j ) {
            bins[j] = new HistogramBin( binStart, binEnd );
            binStart = binEnd;
            binEnd += binRange;
        }
    }

    /**
     * Gets an array of HistogramBin objects to be used in plotting the histogram.
     * @return  The array of HistogramBin objects.
     */
    public HistogramBin[] getBins()
    {
        return bins;
    }

    /**
     * Returns the maximum number of values stored in any of the bins in this collection.
     * This is used to scale the Y-axis of the histogram plot.
     * @return  The maximum count over all bins.
     */
    public int getMaxBinCount()
    {
        int result = 0;
        for ( int j = 0; j < bins.length; ++j ) {
            result = bins[j].getNumberOfValues() > result ? bins[j].getNumberOfValues() : result;
        }
        return result;
    }

    private int findContainingBin( int currentBin, float value )
    {
        for ( int j = currentBin; j < bins.length; ++j )
            if( bins[j].containsValue( value ) )
                return j;
        return bins.length - 1;
    }
}
