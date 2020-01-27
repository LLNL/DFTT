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
package llnl.gnem.core.gui.plotting.histogram;

import llnl.gnem.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.JSubplot;

import llnl.gnem.core.gui.plotting.keymapper.KeyMapperModel;

import llnl.gnem.core.gui.plotting.plotobject.JPolygon;
import llnl.gnem.core.gui.plotting.plotobject.PlotObject;

import java.awt.*;



/**
 * A class that presents an interactive histogram plot.
 *
 * Created by: dodge1
 * Date: Jan 6, 2005
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class Histogram extends JMultiAxisPlot {


    private static final Color PLOT_BACKGROUND_COLOR = new Color( 0.96F, 0.96F, 0.96F );
    private JSubplot sp1;
  //  private BinCollection binCollection;

    public Histogram()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        setcontrolKeyMapper( KeyMapperModel.getInstance() );
        getPlotBorder().setBackgroundColor( PLOT_BACKGROUND_COLOR );
        getPlotBorder().setDrawBox( true );
        getPlotBorder().setFillRegion( true );
        getPlotRegion().setDrawBox( false );
        getPlotRegion().setFillRegion( true );
        getPlotRegion().setBackgroundColor( PLOT_BACKGROUND_COLOR );
    }


    /**
     * Creates a Histogram plot given an array of data values and the required number of bins.
     * @param values  The array of data to be plotted as a histogram.
     * @param nbins The number of bins in the histogram.
     */
    public Histogram(float[] values, int nbins )
    {
        super();
        initialize();
        setData( values, nbins );

    }

    public void setData( float[] values, int nbins )
    {
        clear();
        sp1 = addSubplot();
        sp1.getPlotRegion().setDrawBox( true );
        sp1.getPlotRegion().setFillRegion( true );
        sp1.getPlotRegion().setBackgroundColor( Color.white );
        BinCollection binCollection = new BinCollection( values, nbins );
        int maxBinCount = binCollection.getMaxBinCount();
        
        sp1.SetAxisLimits( binCollection.getMinValue(), binCollection.getMaxValue(), 0.0, (double) maxBinCount );

        HistogramBin[] bins = binCollection.getBins();
        for ( int j = 0; j < bins.length; ++j ) {
            JPolygon shape = bins[j].getBar();
            sp1.AddPlotObject( shape );
        }
        repaint();
        setAllXlimits(binCollection.getMinValue(), binCollection.getMaxValue());
    }

    /**
     * Convenience method to add any kind of PlotObject to the histogram plot.
     * @param po  The reference to the PlotObject to add.
     */
    public void addPlotObject( PlotObject po )
    {
        sp1.AddPlotObject( po );
    }
}

