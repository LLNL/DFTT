package llnl.gnem.core.gui.plotting;

import java.io.Serializable;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum to represent horizontal pinning of text objects.
 *
 * @author Doug Dodge
 */
public class HorizPinEdge implements Serializable{
    private final String name;

    private HorizPinEdge( String name )
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
     * Text is pinned horizontally relative to the left edge of the axis
     */
    public final static HorizPinEdge LEFT = new HorizPinEdge( "left" );
    /**
     * Text is pinned horizontally relative to the right edge of the axis
     */
    public final static HorizPinEdge RIGHT = new HorizPinEdge( "right" );

    public static HorizPinEdge getHorizPinEdge( String str )
    {
        if( str.equals( "left" ))
            return LEFT;
        else if( str.equals( "right" ))
            return RIGHT;
        else
            throw new IllegalArgumentException( "Invalid type name: " + str );
    }
}

