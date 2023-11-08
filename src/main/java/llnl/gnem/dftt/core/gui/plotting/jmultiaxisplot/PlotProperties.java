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

import llnl.gnem.dftt.core.gui.plotting.plotobject.SymbolStyle;

import java.awt.*;

/**
 *
 * User: Eric Matzel
 * Date: Oct 23, 2007
 */
public class PlotProperties
{
    public SymbolStyle getSymbolStyle()
    {
        return symbolStyle;
    }

    public void setSymbolStyle( SymbolStyle symbolStyle )
    {
        this.symbolStyle = symbolStyle;
    }

    public double getSymbolSize()
    {
        return symbolSize;
    }

    public void setSymbolSize( double symbolSize )
    {
        this.symbolSize = symbolSize;
    }

    public Color getSymbolEdgeColor()
    {
        return symbolEdgeColor;
    }

    public void setSymbolEdgeColor( Color symbolEdgeColor )
    {
        this.symbolEdgeColor = symbolEdgeColor;
    }

    public Color getSymbolFillColor()
    {
        return symbolFillColor;
    }

    public void setSymbolFillColor( Color symbolFillColor )
    {
        this.symbolFillColor = symbolFillColor;
    }

    public double getMinYAxisValue()
    {
        return minYAxisValue;
    }

    public void setMinYAxisValue( double minYAxisValue )
    {
        this.minYAxisValue = minYAxisValue;
    }

    public double getMaxYAxisValue()
    {
        return maxYAxisValue;
    }

    public void setMaxYAxisValue( double maxYAxisValue )
    {
        this.maxYAxisValue = maxYAxisValue;
    }

    public boolean getAutoCalculateYaxisRange()
    {
        return autoCalculateYaxisRange;
    }

    public void setAutoCalculateYaxisRange( boolean autoCalculateYaxisRange )
    {
        this.autoCalculateYaxisRange = autoCalculateYaxisRange;
    }

    private SymbolStyle symbolStyle = SymbolStyle.DIAMOND;
    private double symbolSize = 3;
    private Color symbolEdgeColor = Color.black;
    private Color symbolFillColor = Color.red;

    private double minYAxisValue = -2.5;
    private double maxYAxisValue = 2.5;
    private boolean autoCalculateYaxisRange = true;;
}
