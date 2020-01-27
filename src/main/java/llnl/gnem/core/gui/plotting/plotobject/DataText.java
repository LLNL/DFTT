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
package llnl.gnem.core.gui.plotting.plotobject;


import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;
import llnl.gnem.core.gui.plotting.plotobject.BasicText;
import llnl.gnem.core.gui.plotting.HorizAlignment;
import llnl.gnem.core.gui.plotting.VertAlignment;
import llnl.gnem.core.gui.plotting.JBasicPlot;

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
 * Text displayed within a JSubplot that is tied to the real-world coordinates
 * shown on the X-, and Y-axes.
 *
 * @author Doug Dodge
 */
public class DataText extends BasicText {
    /**
     * Constructor for the DataText object
     *
     * @param x    The real-world X-coordinate
     * @param y    The real-world Y-coordinate
     * @param text The String to be displayed
     */
    public DataText( double x, double y, String text )
    {
        super( text );
        _X = x;
        _Y = y;
    }

    /**
     * Constructor for the DataText object
     *
     * @param x        The real-world X-coordinate
     * @param y        The real-world Y-coordinate
     * @param text     The String to be displayed
     * @param fontName The name of the font that will be used to render the text
     * @param fontSize The size in points of the font used to render the text
     * @param textC    The color of the text
     * @param hAlign   The horizontal alignment of the text. For example, left,
     *                 center, right
     * @param vAlign   The vertical alignment of the text. For example top,
     *                 center, bottom
     */
    public DataText( double x, double y, String text, String fontName, double fontSize, Color textC, HorizAlignment hAlign, VertAlignment vAlign )
    {
        super( text, fontName, fontSize, textC, hAlign, vAlign );
        _X = x;
        _Y = y;
    }

    /**
     * render the text to the supplied graphics context
     *
     * @param g     The graphics context on which to render the text
     * @param owner The JBasicPlot object to which the text belongs
     */
    public void render( Graphics g, JBasicPlot owner )
    {
        if( !visible || _Text.length() < 1 ) {
            return;
        }

        // Remove any pre-existing regions before creating new...
        region.clear();

        CoordinateTransform ct = owner.getCoordinateTransform();
        Coordinate coord = new Coordinate( 0.0, 0.0, _X, _Y );
        ct.WorldToPlot( coord );

        int xval = (int) coord.getX();
        int yval = (int) coord.getY();

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
}

