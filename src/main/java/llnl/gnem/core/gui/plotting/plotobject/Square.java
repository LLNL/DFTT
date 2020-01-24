package llnl.gnem.core.gui.plotting.plotobject;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A Symbol shaped like a square
 *
 * @author Doug Dodge
 */
public class Square extends Symbol {
    /**
     * Constructor for the Square object that allows all properties to be set.
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
    public Square( double X, double Y, double size, Color fillC, Color edgeC, Color textC, String text, boolean visible, boolean textVis, double fontsize )
    {
        super( X, Y, size, fillC, edgeC, textC, text, visible, textVis, fontsize );
    }

    /**
     * Constructor for the Square object that only requires a location and size.
     *
     * @param X    X-coordinate of the center of the symbol
     * @param Y    Y-coordinate of the center of the symbol
     * @param size Size of the symbol in millimeters
     */
    public Square( double X, double Y, double size )
    {
        super( X, Y, size );
    }


    public Square()
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
        Rectangle2D square = new Rectangle2D.Double( x - h2, y - h2, h, h );
        g2d.setColor( getFillColor() );
        g2d.fill( square );
        g2d.setColor( _EdgeColor );
        g2d.setStroke( new BasicStroke( 1.0F ) );
        g2d.draw( square );
        addToRegion( square );
    }

    public String toString()
    {
        return "Square" + super.toString();
    }
}
