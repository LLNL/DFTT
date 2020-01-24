/**
 * Created by: dodge1
 * Date: Jan 10, 2005
 *
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.util;

/**
 * This enumeration class is used to specify the way in which epoch times should be rendered
 * by gui controls. It is used, for instance, in the tables that display event data and arrival data.
 */
public class TimeFormat {
    public static final TimeFormat HUMAN = new TimeFormat( "Human-Readable Time" );
    public static final TimeFormat EPOCH = new TimeFormat( "Epoch Time" );

    private final String myName; // for debug only

    private TimeFormat( String name )
    {
        myName = name;
    }

    public String toString()
    {
        return myName;
    }

    public static TimeFormat[] getAllAvailableFormats()
    {
        return new TimeFormat[] { HUMAN, EPOCH };
    }
}
