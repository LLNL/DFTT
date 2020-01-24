package llnl.gnem.core.gui.plotting.jmultiaxisplot;

import llnl.gnem.core.gui.plotting.plotobject.SymbolStyle;

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
