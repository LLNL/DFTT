package llnl.gnem.core.gui.plotting.jmultiaxisplot;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum to represent the position of text associated with a
 * vertical pick line.
 *
 * @author Doug Dodge
 */
public enum PickTextPosition {
    TOP( "top" ), BOTTOM( "bottom" );
    String name;

    private PickTextPosition( String name )
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


    public static PickTextPosition getTextItemType( String style )
    {
        for ( PickTextPosition pos : PickTextPosition.values() ){
            if( pos.toString().equalsIgnoreCase( style ) )
                return pos;
        }
        throw new IllegalArgumentException( "Invalid style string: " + style );
    }

    public static PickTextPosition[] getAllPossiblePositions()
    {
        return PickTextPosition.values();
    }
}

