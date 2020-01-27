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

import java.awt.*;
import java.awt.geom.GeneralPath;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A Symbol shaped like a equilateral triangle with the apex pointing down
 *
 * @author Doug Dodge
 */
public class TriangleDn extends Symbol {
    /**
     * Constructor for the TriangleDn object that allows all properties to be set.
     *
     * @param X        X-coordinate of the center of the symbol
     * @param Y        Y-coordinate of the center of the symbol
     * @param size     Size of the symbol in millimeters
     * @param fillC    Fill color of the symbol
     * @param edgeC    Edge color of the symbol edge
     * @param textC    Color of the text
     * @param text     Optional text associated with the symbol.
     * @param visible  Controls whether the symbol is visible.
     * @param textVis  Controls whether the text associated with the symbol is visible.
     * @param fontsize The fontsize of the associated text.
     */
    public TriangleDn( double X, double Y, double size, Color fillC, Color edgeC, Color textC, String text, boolean visible, boolean textVis, double fontsize )
    {
        super( X, Y, size, fillC, edgeC, textC, text, visible, textVis, fontsize );
    }

    /**
     * Constructor for the TriangleDn object that only requires location and size.
     *
     * @param X    X-coordinate of the center of the symbol
     * @param Y    Y-coordinate of the center of the symbol
     * @param size Size of the symbol in millimeters
     */
    public TriangleDn( double X, double Y, double size )
    {
        super( X, Y, size );
    }

    public TriangleDn()
    {
        super();
    }


    /**
     * Description of the Method
     *
     * @param g Description of the Parameter
     * @param x Description of the Parameter
     * @param y Description of the Parameter
     * @param h Description of the Parameter
     */
    public void PaintSymbol( Graphics g, int x, int y, int h )
    {
        int h2 = h / 2;
        Graphics2D g2d = (Graphics2D) g;
        GeneralPath triangle = new GeneralPath();
        triangle.moveTo( x - h2, y - h2 );
        triangle.lineTo( x + h2, y - h2 );
        triangle.lineTo( x, y + h2 );
        triangle.lineTo( x - h2, y - h2 );
        g2d.setColor( getFillColor() );
        g2d.fill( triangle );
        g2d.setColor( _EdgeColor );
        g2d.setStroke( new BasicStroke( 1.0F ) );
        g2d.draw( triangle );
        addToRegion( triangle );
    }

    public String toString()
    {
        return "TriangleDn" + super.toString();
    }


}
