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
package llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import llnl.gnem.dftt.core.gui.plotting.PaintMode;
import llnl.gnem.dftt.core.gui.plotting.PenStyle;
import llnl.gnem.dftt.core.gui.plotting.plotobject.JDataRectangle;
import llnl.gnem.dftt.core.gui.plotting.plotobject.Line;
import llnl.gnem.dftt.core.gui.plotting.plotobject.PlotObject;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.google.common.primitives.Doubles;

/**
 *
 * @author dodge1
 */
public class JHistogramPlot extends JMultiAxisPlot {

    private final JSubplot histPlot;
    private final int nBins;
	private EmpiricalDistribution distribution;

    public JHistogramPlot()
    {
        nBins = 10;
        histPlot = this.addSubplot();
    }
    public JHistogramPlot(int bins )
    {
        nBins = bins;
        histPlot = this.addSubplot();
    }

    public void clearHistogram()
    {
        histPlot.Clear();
    }

    public void addVerticalMarkerLine(double value) {
        double max = histPlot.getYaxis().getMax();
        float[] xVals = {(float)value,(float)value};
        float[] yVals = {0.0f,(float)max};
        Line line = new Line(xVals, yVals);
        line.setColor(Color.red);
        line.setWidth(2);
        histPlot.AddPlotObject(line);
        double xmin = getXaxis().getMin();
        if( value < xmin)
            xmin = value;
        double xmax = getXaxis().getMax();
        if( value > xmax)
            xmax = value;
        double ymin = 0;
        double ymax = histPlot.getYaxis().getMax();
        histPlot.SetAxisLimits(xmin, xmax, ymin, ymax);
    }
    
    public void addVerticalMarkerLine(double value, Color lineColor, PenStyle penStyle) {
        double max = histPlot.getYaxis().getMax();
        float[] xVals = {(float)value,(float)value};
        float[] yVals = {0.0f,(float)max};
        Line line = new Line(xVals, yVals, lineColor, PaintMode.COPY, penStyle, 2);
        histPlot.AddPlotObject(line);
        double xmin = getXaxis().getMin();
        if( value < xmin)
            xmin = value;
        double xmax = getXaxis().getMax();
        if( value > xmax)
            xmax = value;
        double ymin = 0;
        double ymax = histPlot.getYaxis().getMax();
        histPlot.SetAxisLimits(xmin, xmax, ymin, ymax);
    }

    public void addPlotObject(PlotObject plotObject) {
    	histPlot.AddPlotObject(plotObject);
    }
    
	public void setValues(List<Double> values) {
		Collections.sort(values);
		setSortedValues(values);
	}
	
	public void setSortedValues(List<Double> values) {
		histPlot.Clear();

		int numBins = nBins < values.size() ? nBins : values.size();

		distribution = new EmpiricalDistribution(numBins);
		distribution.load(Doubles.toArray(values));
		
		int maxCount = 0;
		double min = distribution.getSampleStats().getMin();
		double max = distribution.getSampleStats().getMax();
		double dataRange = max - min;
		double binWidth = dataRange / numBins;
		double binStart = min;
		for (SummaryStatistics stats : distribution.getBinStats()) {
			// create the Histrogram bar
			long count = stats.getN();
			JDataRectangle rect = new JDataRectangle(binStart, 0.0, binWidth, count);
			//Royal Blue
			rect.setFillColor(new Color(65,105,225));
			histPlot.AddPlotObject(rect);
			binStart += binWidth;
			if (count > maxCount) {
				maxCount = (int) stats.getN();
			}
		}
		
		
		histPlot.SetAxisLimits(min, max, 0, maxCount);
	}
	
	/**
	 * @return List of Apache Commons Math {@link SummaryStatistics} representing the bins of the Histogram.
	 */
	public List<SummaryStatistics> getBinStats() {
	    return distribution.getBinStats();
    }
	
	/**
	 * @return List of Apache Commons Math {@link StatisticalSummary} representing the Histogram.
	 */
	public StatisticalSummary getDistributionStats() {
		return distribution.getSampleStats();
	}
	
	/**
	 * @return  Apache Commons Math {@link SummaryStatistics} representing the maximum frequency bin
	 */
	public SummaryStatistics getMaxFrequencyBinStatistics() {
	    
		SummaryStatistics maxBin = null;
		int maxCount = 0;
		if (distribution != null) {
			for (SummaryStatistics stats : distribution.getBinStats()) {
				// create the Histrogram bar
				long count = stats.getN();

				if (count > maxCount) {
					maxCount = (int) stats.getN();
					maxBin = stats;
				}
			}
		}
		
		return maxBin;
    }

}
