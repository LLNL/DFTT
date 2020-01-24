package llnl.gnem.core.gui.plotting;

import java.io.Serializable;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum to represent the vertical alignment of text objects.
 *
 * @author Doug Dodge
 */
public class VertAlignment implements Serializable{
    private final String name;

    private VertAlignment( String name )
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
    public final static VertAlignment BOTTOM = new VertAlignment( "bottom" );
    /**
     * Description of the Field
     */
    public final static VertAlignment CENTER = new VertAlignment( "center" );
    /**
     * Description of the Field
     */
    public final static VertAlignment TOP = new VertAlignment( "top" );

    public static VertAlignment getVertAlignment( String str )
    {
        if( str.equals( "bottom" ) )
            return BOTTOM;
        else if ( str.equals( "center" ) )
            return CENTER;
        else if( str.equals( "top" ) )
            return TOP;
        else
            throw new IllegalArgumentException( "Invalid type: " + str );

    }
}

