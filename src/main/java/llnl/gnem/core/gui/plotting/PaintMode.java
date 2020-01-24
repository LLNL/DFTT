package llnl.gnem.core.gui.plotting;

import java.awt.*;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum to represent the paint mode of objects drawn in the axis.
 *
 * @author Doug Dodge
 */
public class PaintMode {
    private final String name;

    private PaintMode( String name )
    {
        this.name = name;
    }

    /**
     * Return a String description of this type.
     *
     * @return The String description
     */
    public String toString()
    {
        return name;
    }

    /**
     * Sets the graphics PaintMode based on the PaintMode type
     *
     * @param g The graphics context to be operated on.
     */
    public void setGraphicsPaintMode( Graphics g )
    {
        if( name.equals( "Copy" ) )
            g.setPaintMode();
        else
            g.setXORMode( Color.white );
    }

    /**
     * PaintMode for doing SRCCOPY
     */
    public final static PaintMode COPY = new PaintMode( "Copy" );
    /**
     * PaintMode for doing XOR
     */
    public final static PaintMode XOR = new PaintMode( "Xor" );
}

