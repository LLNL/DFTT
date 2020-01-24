package llnl.gnem.core.gui.plotting.plotobject;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A symbol that consists of a circle with optional text underneath the circle.
 *
 * @author Doug Dodge
 */
public class Circle extends Symbol {
    /**
     * Constructor for the Circle object
     *
     * @param X        X-coordinate of the center of the symbol
     * @param Y        Y-coordinate of the center of the symbol
     * @param size     Size of the symbol in millimeters
     * @param fillC    Fill color of the symbol
     * @param edgeC    Edge color of the symbol edge
     * @param textC    Color of the text
     * @param text     Optional text associated with the symbol.
     * @param visible  Controls whether the symbol is visible.
     * @param textVis  Controls whether the text associated with the symbol is
     *                 visible.
     * @param fontsize The fontsize of the associated text.
     */
    public Circle( double X, double Y, double size, Color fillC, Color edgeC, Color textC, String text, boolean visible, boolean textVis, double fontsize )
    {
        super( X, Y, size, fillC, edgeC, textC, text, visible, textVis, fontsize );
    }

    /**
     * Constructor for the Circle object
     *
     * @param X    X-coordinate of the center of the symbol
     * @param Y    Y-coordinate of the center of the symbol
     * @param size Size of the symbol in millimeters
     */
    public Circle( double X, double Y, double size )
    {
        super( X, Y, size );
    }

    public Circle()
    {
        super();
    }


    /**
     * render the symbol to the supplied graphics context. This method is called
     * by the base class render method. Text is rendered separately.
     *
     * @param g The graphics context
     * @param x The X-value ( in user space pixels )
     * @param y The Y-value ( in user space pixels )
     * @param h The height of the symbol in pixels
     */
    public void PaintSymbol( Graphics g, int x, int y, int h )
    {
        int du = (int) ( 0.530 * h );
        Graphics2D g2d = (Graphics2D) g;
        Ellipse2D circle = new Ellipse2D.Double( x - du, y - du, 2 * du, 2 * du );
        g2d.setColor( getFillColor() );
        g2d.fill( circle );
        g2d.setColor( _EdgeColor );
        g2d.setStroke( new BasicStroke( 1.0F ) );
        g2d.draw( circle );
        addToRegion( circle );

    }

    /**
     * Return a string descriptor of this object
     *
     * @return The String descriptor.
     */
    public String toString()
    {
        return "Circle" + super.toString();
    }


}

