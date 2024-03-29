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
package llnl.gnem.dftt.core.gui.plotting.plotobject;


import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;
import llnl.gnem.dftt.core.gui.plotting.VertAlignment;

import java.awt.*;
import java.awt.font.TextLayout;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Base class for all text that can be displayed in the plotting area of a
 * JAxis
 *
 * @author Doug Dodge
 */
public abstract class BasicText extends PlotObject {
    /**
     * Constructor for the BasicText object
     *
     * @param text The String to be displayed
     */
    public BasicText( String text )
    {
        _Text = text;
        _FontName = "Arial";
        _FontSize = 12;
        _Color = Color.black;
        _HorAlign = HorizAlignment.LEFT;
        _VertAlign = VertAlignment.TOP;
    }

    // For now text cannot be moved
    public void ChangePosition( JBasicPlot owner, Graphics graphics, double dx, double dy )
    {
    }

    /**
     * Constructor for the BasicText object
     *
     * @param text     The String to be displayed
     * @param fontName The name of the font which will be used to display the text
     * @param fontSize The size of the font to use
     * @param textC    The color with which to render the string
     * @param hAlign   The horizontal alignment of the string
     * @param vAlign   The vertical alignment of the string
     */
    public BasicText( String text, String fontName, double fontSize, Color textC, HorizAlignment hAlign, VertAlignment vAlign )
    {
        _Text = text;
        _FontName = fontName;
        _FontSize = fontSize;
        _Color = textC;
        _HorAlign = hAlign;
        _VertAlign = vAlign;
    }

    /**
     * Gets the string
     *
     * @return The string value
     */
    public String getText()
    {
        return _Text;
    }

    /**
     * Gets the fontName of the BasicText object
     *
     * @return The fontName value
     */
    public String getFontName()
    {
        return _FontName;
    }

    /**
     * Gets the fontSize of the BasicText object
     *
     * @return The fontSize value
     */
    public double getFontSize()
    {
        return _FontSize;
    }

    /**
     * Gets the color of the BasicText object
     *
     * @return The color value
     */
    public Color getColor()
    {
        return _Color;
    }

    /**
     * Gets the horizontalAlignment of the BasicText object
     *
     * @return The horizontalAlignment value
     */
    public HorizAlignment getHorizontalAlignment()
    {
        return _HorAlign;
    }

    /**
     * Gets the verticalAlignment of the BasicText object
     *
     * @return The verticalAlignment value
     */
    public VertAlignment getVerticalAlignment()
    {
        return _VertAlign;
    }

    /**
     * Sets the string of the BasicText object
     *
     * @param v The new text value
     */
    public void setText( String v )
    {
        _Text = v;
    }

    /**
     * Sets the fontName of the BasicText object
     *
     * @param v The new fontName value
     */
    public void setFontName( String v )
    {
        _FontName = v;
    }

    /**
     * Sets the fontSize of the BasicText object
     *
     * @param v The new fontSize value
     */
    public void setFontSize( double v )
    {
        _FontSize = v;
    }

    /**
     * Sets the color of the BasicText object
     *
     * @param v The new color value
     */
    public void setColor( Color v )
    {
        _Color = v;
    }

    /**
     * Sets the horizontalAlignment of the BasicText object
     *
     * @param v The new horizontalAlignment value
     */
    public void setHorizontalAlignment( HorizAlignment v )
    {
        _HorAlign = v;
    }

    /**
     * Sets the verticalAlignment of the BasicText object
     *
     * @param v The new verticalAlignment value
     */
    public void setVerticalAlignment( VertAlignment v )
    {
        _VertAlign = v;
    }

    /**
     * render this text on the supplied graphics context of the supplied JAxis
     *
     * @param g     The graphics context
     * @param owner The JBasicPlot that contains this object
     */
    public abstract void render( Graphics g, JBasicPlot owner );

    /**
     * Produce a string representation of this object
     *
     * @return The String description
     */
    public String toString()
    {
        StringBuffer s = new StringBuffer( "Text = " + _Text + ", FontName = " );
        s.append( _FontName + ", FontSize = " + _FontSize + ", Color = " );
        s.append( _Color + ", Horizontal Alignment = " + _HorAlign );
        s.append( ", Vertical Alignment = " + _VertAlign );
        return s.toString();
    }

    /**
     * Gets the horizontal Offset necessary to produce the desired horizontal
     * alignment
     *
     * @param textTl The TextLayout for this text
     * @return The horizontal Offset value
     */
    protected float getHorizontalAlignmentOffset( TextLayout textTl )
    {
        float advance = textTl.getAdvance();
        float xshift = 0.0F;
        if( _HorAlign == HorizAlignment.CENTER )
            xshift = -advance / 2;
        else if( _HorAlign == HorizAlignment.RIGHT )
            xshift = -advance;
        return xshift;
    }

    /**
     * Gets the vertical Offset necessary to produce the desired vertical
     * alignment
     *
     * @param textTl The TextLayout for this text
     * @return The vertical Offset value
     */
    protected float getVerticalAlignmentOffset( TextLayout textTl )
    {
        float ascent = textTl.getAscent();
        double height = textTl.getBounds().getHeight();
        float yshift = ascent - 3;
        // Need to shift up by 3 points for some reason.
        if( _VertAlign == VertAlignment.BOTTOM )
            yshift -= height;
        else if( _VertAlign == VertAlignment.CENTER )
            yshift -= height / 2;
        return yshift;
    }

    protected String _Text;
    protected String _FontName;
    protected double _FontSize;
    protected Color _Color;
    protected HorizAlignment _HorAlign;
    protected VertAlignment _VertAlign;
}

