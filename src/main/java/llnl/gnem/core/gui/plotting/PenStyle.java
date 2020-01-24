package llnl.gnem.core.gui.plotting;

import java.awt.*;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * A type-safe enum to represent the dashing style of lines drawn in the axis.
 *
 * @author Doug Dodge
 */
public enum PenStyle {
    NONE( "None", null ),
    SOLID( "Solid", null ),
    DASH( "Dash", new float[]{10.0F, 10.0F} ),
    DOT( "Dot", new float[]{1.0F, 5.0F} ),
    DASHDOT( "DashDot", new float[]{10.0F, 5.0F, 1.0F, 5.0F} ),
    DASHDOTDOT( "DashDotDot", new float[]{10.0F, 5.0F, 1.0F, 5.0F, 1.0F, 5.0F} );

    String name;
    float[] pattern;

    private PenStyle( String name, float[] template )
    {
        this.name = name;
        pattern = template;
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
     * Gets the float array that defines the dashing pattern of a BasicStroke
     * object for this PenStyle.
     *
     * @return The pattern value
     */
    public float[] getPattern()
    {
        return pattern;
    }

    /**
     * Gets a new stroke of the specified width using the pattern for this
     * PenStyle.
     *
     * @param width Width of the requested BasicStroke
     * @return The new BasicStroke object
     */
    public BasicStroke getStroke( float width )
    {
        return new BasicStroke( width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, pattern, 0.0f );
    }


    public static PenStyle[] getAllStyles()
    {
        return PenStyle.values();
    }


    public static PenStyle getPenStyle( String style )
    {
        for ( PenStyle astyle : PenStyle.values() ){
            if( astyle.toString().equalsIgnoreCase( style ) )
                return astyle;
        }
        throw new IllegalArgumentException( "Invalid style string: " + style );
    }

}

