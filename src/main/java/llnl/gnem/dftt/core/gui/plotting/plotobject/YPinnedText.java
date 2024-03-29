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


import llnl.gnem.dftt.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.dftt.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.dftt.core.gui.plotting.plotobject.BasicText;
import llnl.gnem.dftt.core.gui.plotting.VertPinEdge;
import llnl.gnem.dftt.core.gui.plotting.HorizAlignment;
import llnl.gnem.dftt.core.gui.plotting.VertAlignment;
import llnl.gnem.dftt.core.gui.plotting.JBasicPlot;

import java.awt.*;
import java.awt.Font;
import java.awt.geom.*;
import java.awt.font.*;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * Text that is pinned in the vertical direction a fixed amount from one of
 * the plot's edges. In the horizontal direction, the text is fixed to a data
 * value. The effect is that upon zooming, the text stays the same distance
 * from the edge (vertically) but floats to the new position of the X-value.
 *
 * @author Doug Dodge
 */
public class YPinnedText extends BasicText {
    /**
     * Constructor for the YPinnedText object
     *
     * @param x        The X-data value (real-world) of the text
     * @param y        The vertical offset in mm from the plot edge
     * @param text     The text string
     * @param vp       The plot edge to pin to ( TOP, BOTTOM )
     * @param fontName The name of the font used to render the text
     * @param fontSize The size of the font used to render the text
     * @param textC    The color of the text
     * @param hAlign   The horizontal alignment of the text relative to the pin
     *                 point.
     * @param vAlign   The vertical alignment of the text relative to the pin
     *                 point.
     */
    public YPinnedText( double x, double y, String text, VertPinEdge vp, String fontName, double fontSize, Color textC, HorizAlignment hAlign, VertAlignment vAlign )
    {
        super( text, fontName, fontSize, textC, hAlign, vAlign );
        _X = x;
        _Y = y;
        V_Pin = vp;
    }

    /**
     * Constructor for the YPinnedText object
     *
     * @param x    The X-data value (real-world) of the text
     * @param y    The vertical offset in mm from the plot edge
     * @param text The text string
     */
    public YPinnedText( double x, double y, String text )
    {
        super( text );
        _X = x;
        _Y = y;
        V_Pin = VertPinEdge.TOP;
    }

    /**
     * render this text string
     *
     * @param g     The graphics context on which to render the text
     * @param owner The JBasicPlot that owns this text
     */
    public void render( Graphics g, JBasicPlot owner )
    {
        if( !visible || _Text.length() < 1 || !owner.getCanDisplay() )
            return;

        // Remove any pre-existing regions before creating new...
        region.clear();
        int yOffset = owner.getUnitsMgr().getVertUnitsToPixels( _Y );
        CoordinateTransform ct = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0.0, 0.0, _X, 0.0 );
        ct.WorldToPlot( coord );
        int xval = (int) coord.getX();
        int yval = V_Pin == VertPinEdge.TOP ? owner.getPlotTop() + yOffset : owner.getPlotTop() + owner.getPlotHeight() - yOffset;
        Graphics2D g2d = (Graphics2D) g;
        // Save old color
        Color oldColor = g2d.getColor();

        // Create new font and color
        g2d.setColor( _Color );

        // Layout and render text
        TextLayout textTl = new TextLayout( _Text, new Font( _FontName, Font.PLAIN, (int) _FontSize ), new FontRenderContext( null, false, false ) );
        float xshift = getHorizontalAlignmentOffset( textTl );
        float yshift = getVerticalAlignmentOffset( textTl );
        textTl.draw( g2d, xval + xshift, yval + yshift );
        AffineTransform textAt = new AffineTransform();
        textAt.translate( xval + xshift, yval + yshift );
        Shape s = textTl.getOutline( textAt );
        addToRegion( s.getBounds2D() );

        // restore old color
        g2d.setColor( oldColor );
    }

    private double _X;
    private double _Y;
    private VertPinEdge V_Pin;
}

