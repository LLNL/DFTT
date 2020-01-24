package llnl.gnem.core.gui.plotting.jmultiaxisplot;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum class for Pick Error Symbol direction.
 *
 * @author Doug Dodge
 */
public class PickErrorDir {
    private final String name;

    private PickErrorDir( String name )
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
     * Numbers increase to the left
     */
    public final static PickErrorDir LEFT = new PickErrorDir( "left" );
    /**
     * Numbers increase to the right
     */
    public final static PickErrorDir RIGHT = new PickErrorDir( "right" );
}

