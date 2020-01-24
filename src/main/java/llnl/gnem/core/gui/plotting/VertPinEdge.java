package llnl.gnem.core.gui.plotting;

import java.io.Serializable;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum to represent vertical pinning of text objects.
 *
 * @author Doug Dodge
 */
public class VertPinEdge implements Serializable{
    private final String name;

    private VertPinEdge( String name )
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
     * Description of the Field
     */
    public final static VertPinEdge TOP = new VertPinEdge( "top" );
    /**
     * Description of the Field
     */
    public final static VertPinEdge BOTTOM = new VertPinEdge( "bottom" );

    public static VertPinEdge getVertPinEdge( String str )
    {
        if( str.equals( "top" ) )
            return TOP;
        else if( str.equals( "bottom" ) )
            return BOTTOM;
        else
            throw new IllegalArgumentException( "Invalid type: " + str );
    }
}

